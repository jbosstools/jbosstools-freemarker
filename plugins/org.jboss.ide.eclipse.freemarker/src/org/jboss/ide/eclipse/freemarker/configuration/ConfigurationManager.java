/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.configuration;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class ConfigurationManager {

	private static final Map<String, ConfigurationManager> instances = new HashMap<String, ConfigurationManager>();

	private IProject project;
	private ProjectClassLoader projectClassLoader;
	private Map<String, ContextValue[]> contextValues = new HashMap<String, ContextValue[]>();
	private Map<String, MacroLibrary> macroLibrary = new HashMap<String, MacroLibrary>();
	private MacroLibrary[] macroLibraryArr;

	private ConfigurationManager () {}

	public synchronized static final ConfigurationManager getInstance (IProject project) {
		ConfigurationManager configuration = instances.get(project.getName());
		if (null == configuration) {
			configuration = new ConfigurationManager();
			configuration.project = project;
			configuration.reload();
			instances.put(project.getName(), configuration);
		}
		return configuration;
	}

	public MacroLibrary[] getMacroLibraries () {
		return macroLibraryArr;
	}

	public void associateMappingLibraries (List<?> libraries, Shell shell) {
		for (Iterator<?> i=libraries.iterator(); i.hasNext(); ) {
			Object obj = i.next();
			if (obj instanceof IFile) {
				IFile file = (IFile) obj;
				String namespace = file.getName();
				int index = namespace.indexOf("."); //$NON-NLS-1$
				if (index >= 0) namespace = namespace.substring(0, index);
				InputDialog inputDialog = new InputDialog(
						shell, Messages.ConfigurationManager_TITLE_CHOOSE_NAMESPACE,
						MessageFormat.format(
								Messages.ConfigurationManager_CHOOSE_NAMESPACE_FOR, file
										.getName()),
						namespace, null);
				int rtn = inputDialog.open();
				if (rtn == IDialogConstants.OK_ID) {
					namespace = inputDialog.getValue();
					try {
						this.macroLibrary.put(namespace, new MacroLibrary(namespace, file));
					}
					catch (CoreException e) {
						Plugin.error(e);
					}
					catch (IOException e) {
						Plugin.error(e);
					}
				}
			}
			else if (obj instanceof IJarEntryResource && ((IJarEntryResource) obj).isFile()) {
				IJarEntryResource jef = (IJarEntryResource) obj;
				String namespace = jef.getName();
				int index = namespace.indexOf("."); //$NON-NLS-1$
				if (index >= 0) namespace = namespace.substring(0, index);
				InputDialog inputDialog = new InputDialog(
						shell, Messages.ConfigurationManager_TITLE_CHOOSE_NAMESPACE,
						MessageFormat.format(
								Messages.ConfigurationManager_CHOOSE_NAMESPACE_FOR, jef
										.getName()),
						namespace, null);
				int rtn = inputDialog.open();
				if (rtn == IDialogConstants.OK_ID) {
					namespace = inputDialog.getValue();
					try {
						InputStream is = getProjectClassLoader().getResourceAsStream(jef.getFullPath().toString());
						if (null != is) {
							this.macroLibrary.put(namespace, new MacroLibrary(namespace, is, jef.getFullPath().toString(), MacroLibrary.TYPE_JAR_ENTRY));
						}
						else {
							// FIXME: add error dialog here
						}
					}
					catch (CoreException e) {
						Plugin.error(e);
					}
					catch (IOException e) {
						Plugin.error(e);
					}
				}
			}
		}
		save();
	}

	public MacroLibrary getMacroLibrary (String namespace) {
		return macroLibrary.get(namespace);
	}

	private void writeMacroLibrary(StringBuilder sb) {
		for (Iterator<MacroLibrary> i=macroLibrary.values().iterator(); i.hasNext(); ) {
			MacroLibrary ml = i.next();
			sb.append('\t').append('\t');
			ml.toXML(sb);
			sb.append('\n');
		}
	}

	private Map<String, MacroLibrary> loadMacroTemplates (Element element) {
		Map<String, MacroLibrary> map = new HashMap<String, MacroLibrary>();
        try {
            NodeList nl = element
                    .getElementsByTagName("entry"); //$NON-NLS-1$
            for (int i = 0; i < nl.getLength(); i++) {
                try {
                    Node n = nl.item(i);
                    MacroLibrary ml = MacroLibrary.fromXML(project, (Element) n, getProjectClassLoader());
                    if (null != ml) {
                    	map.put(ml.getNamespace(), ml);
                    }

                } catch (Exception e) {
                	Plugin.log(e);
                }
            }
	    }
        catch (Exception e) {
        	Plugin.log(e);
        }
        return map;
	}

	private Map<String, ContextValue[]> loadContextValues (Element element) {
		Map<String, ContextValue[]> map = new HashMap<String, ContextValue[]>();
        try {
            NodeList nl = element
                    .getElementsByTagName("resource"); //$NON-NLS-1$
            for (int i = 0; i < nl.getLength(); i++) {
                try {
                    Node n = nl.item(i);
                    String path = ((Element) n).getAttribute("path"); //$NON-NLS-1$
                    List<ContextValue> contextVals = new ArrayList<ContextValue>();
                    NodeList nl2 = ((Element) n)
                            .getElementsByTagName("value"); //$NON-NLS-1$
                    for (int j = 0; j < nl2.getLength(); j++) {
                        Node n2 = nl2.item(j);
                        String key = ((Element) n2).getAttribute("key"); //$NON-NLS-1$
                        Class<?> value = getClass(((Element) n2)
                                .getAttribute("object-class")); //$NON-NLS-1$
                        String singularName = ((Element) n2)
                                .getAttribute("item-class"); //$NON-NLS-1$
                        Class<?> singularClass = null;
                        if (null != singularName && singularName.trim().length()>0)
                            singularClass = getClass(singularName);
                        contextVals.add(new ContextValue(key, value,
                                singularClass));
                    }
                    map.put(path,
                            contextVals
                                    .toArray(new ContextValue[contextVals
                                            .size()]));
                } catch (Exception e) {
                	Plugin.log(e);
                }
            }
	    }
        catch (Exception e) {
        	Plugin.log(e);
        }
        return map;
	}

    public synchronized Class<?> getClass(String className)
    throws JavaModelException, ClassNotFoundException {
    	return getProjectClassLoader().loadClass(className);
    }

    public synchronized ClassLoader getProjectClassLoader() throws JavaModelException {
    	if (null == this.projectClassLoader)
    		this.projectClassLoader = new ProjectClassLoader(JavaCore.create(project));
    	return this.projectClassLoader;
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("<config>\n"); //$NON-NLS-1$
		sb.append("\t<context-values>\n"); //$NON-NLS-1$
        writeContextValues(sb);
        sb.append("\t</context-values>\n"); //$NON-NLS-1$
		sb.append("\t<macro-library>\n"); //$NON-NLS-1$
        writeMacroLibrary(sb);
        sb.append("\t</macro-library>\n"); //$NON-NLS-1$
		sb.append("</config>"); //$NON-NLS-1$
		IFile file = project.getFile(".freemarker-ide.xml"); //$NON-NLS-1$
        try {
            if (file.exists())
                file.setContents(new ByteArrayInputStream(sb.toString()
                        .getBytes()), true, true, null);
            else
                file.create(new ByteArrayInputStream(sb.toString().getBytes()),
                        true, null);
        } catch (Exception e) {
            Plugin.error(e);
        }
        reload();
    }

    public void reload() {
    	this.projectClassLoader = null;
        IFile file = project.getFile(".freemarker-ide.xml"); //$NON-NLS-1$
        if (file.exists()) {
        	try { file.refreshLocal(1, null); } catch (CoreException e) {}
            try {
                Document document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(file.getContents());
                NodeList nl = document.getDocumentElement()
                        .getElementsByTagName("context-values"); //$NON-NLS-1$
                if (nl.getLength() > 0)
                    this.contextValues = loadContextValues((Element) nl.item(0));
                else
                    this.contextValues = new HashMap<String, ContextValue[]>();
                nl = document.getDocumentElement()
                	.getElementsByTagName(
						"macro-library"); //$NON-NLS-1$
                List<MacroLibrary> libraries = new ArrayList<MacroLibrary>();
                if (nl.getLength() > 0) {
                	this.macroLibrary = loadMacroTemplates((Element) nl.item(0));
                	for (Iterator<MacroLibrary> i=macroLibrary.values().iterator(); i.hasNext(); ) {
                		libraries.add(i.next());
                	}
                }
                else
                	this.macroLibrary = new HashMap<String, MacroLibrary>();
                macroLibraryArr = libraries.toArray(new MacroLibrary[libraries.size()]);
            } catch (Exception e) {
            	Plugin.error(e);
            }
        }
    }

    private void writeContextValues(StringBuilder sb) {
        for (Iterator<Map.Entry<String, ContextValue[]>> i = contextValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, ContextValue[]> entry = i.next();
            String fileName = entry.getKey();
            ContextValue[] values = entry.getValue();
            if (null != values && values.length > 0) {
                sb.append("\t\t<resource path=\"" + fileName + "\">\n");  //$NON-NLS-1$//$NON-NLS-2$
                for (int j = 0; j < values.length; j++) {
                    sb
							.append("\t\t\t<value key=\"" + values[j].name //$NON-NLS-1$
									+ "\" object-class=\"" + values[j].objClass.getName() //$NON-NLS-1$
									+ "\""); //$NON-NLS-1$
                    if (null != values[j].singularClass)
                        sb.append(" item-class=\"" //$NON-NLS-1$
								+ values[j].singularClass.getName() + "\""); //$NON-NLS-1$
					sb.append("/>\n"); //$NON-NLS-1$
                }
                sb.append("\t\t</resource>\n"); //$NON-NLS-1$
            }
        }
    }

    public ContextValue[] getContextValues(IResource resource, boolean recurse) {
        Map<String, ContextValue> newValues = new HashMap<String, ContextValue>();
        addRootContextValues(resource, newValues, recurse);
        return newValues.values().toArray(new ContextValue[newValues.size()]);
    }

    private void addRootContextValues(IResource resource, Map<String, ContextValue> newValues, boolean recurse) {
        String key = null;
        if (null != resource.getParent()) {
            key = resource.getProjectRelativePath().toString();
            if (recurse) addRootContextValues(resource.getParent(), newValues, true);
        }
        else
			key = ""; //$NON-NLS-1$
        if (null != resource.getProject()) {
	        ContextValue[] values = contextValues.get(key);
	        if (null != values) {
	            for (int i=0; i<values.length; i++) {
	                newValues.put(values[i].name, values[i]);
	            }
	        }
        }
    }

    public ContextValue getContextValue(String name, IResource resource, boolean recurse) {
        ContextValue[] values = getContextValues(resource, recurse);
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equals(name))
                return values[i];
        }
        return null;
    }

    public void addContextValue(ContextValue contextValue, IResource resource) {
        ContextValue[] contextValues = getContextValues(resource, false);
        boolean found = false;
        for (int i = 0; i < contextValues.length; i++) {
            if (contextValues[i].name.equals(contextValue.name)) {
                found = true;
                contextValues[i] = contextValue;
                this.contextValues.put(
                		resource.getProjectRelativePath().toString(),
						contextValues);
                break;
            }
        }
        if (!found) {
            ContextValue[] newContextValues = new ContextValue[contextValues.length + 1];
            int index = 0;
            while (index < contextValues.length) {
                newContextValues[index] = contextValues[index++];
            }
            newContextValues[index] = contextValue;
            this.contextValues.put(resource.getProjectRelativePath().toString(), newContextValues);
        }
        save();
    }

    public void updateContextValue(ContextValue contextValue, IFile file) {
        addContextValue(contextValue, file);
    }

    public void removeContextValue(String name, IResource resource) {
        ContextValue[] values = getContextValues(resource, false);
        int index = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equals(name)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            ContextValue[] newValues = new ContextValue[values.length - 1];
            int j = 0;
            for (int i = 0; i < values.length; i++) {
                if (i != index)
                    newValues[j++] = values[i];
            }
            this.contextValues.put(resource.getProjectRelativePath().toString(), newValues);
            save();
        }
    }
}