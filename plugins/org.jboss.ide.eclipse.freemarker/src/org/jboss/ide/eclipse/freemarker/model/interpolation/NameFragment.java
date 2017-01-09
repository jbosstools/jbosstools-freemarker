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
package org.jboss.ide.eclipse.freemarker.model.interpolation;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class NameFragment extends AbstractFragment {

	public NameFragment(int offset, String content) {
		super(offset, content);
	}

	@Override
	public ICompletionProposal[] getCompletionProposals (int subOffset, int offset, Class<?> parentClass,
			List<Fragment> fragments, ISourceViewer sourceViewer, Map<String, Class<?>> context, IResource file, IProject project) {
		if (isStartFragment()) {
			// pull from context
			String prefix = getContent().substring(0, subOffset);
			List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
			for (Iterator<String> i=context.keySet().iterator(); i.hasNext(); ) {
				String key = i.next();
				if (key.startsWith(prefix)) proposals.add(getCompletionProposal(
						offset, subOffset, key, getContent()));
			}
			return completionProposals(proposals);
		}
		else {
			if (null == parentClass) return null;
			return getMethodCompletionProposals (subOffset, offset, parentClass, file);
		}
	}

	private Class<?> returnClass;
	@Override
	public Class<?> getReturnClass (Class<?> parentClass, List<Fragment> fragments, Map<String, Class<?>> context, IResource resource, IProject project){
		if (null == returnClass) {
			String content = getContent();
			if (isStartFragment()) {
				returnClass = context.get(content);
			}
			else {
				if (null == parentClass) {
					returnClass = Object.class;
				}
				else {
					content = Character.toUpperCase(content.charAt(1)) + content.substring(2, getContent().length());
					String getcontent = "get" + content; //$NON-NLS-1$
					for (int i=0; i<parentClass.getMethods().length; i++) {
						Method m = parentClass.getMethods()[i];
						if (m.getName().equals(content) || m.getName().equals(getcontent)) {
							returnClass = m.getReturnType();
							break;
						}
					}
				}
			}
		}
		return returnClass;
	}

	private Class<?> singulaReturnClass;
	@Override
	public Class<?> getSingularReturnClass(Class<?> parentClass, List<Fragment> fragments, Map<String, Class<?>> context, IResource resource, IProject project) {
		if (null == singulaReturnClass) {
			String content = getContent();
			if (isStartFragment()) {
				ContextValue contextValue = ConfigurationManager.getInstance(project).getContextValue(content, resource, true);
				if (null == contextValue || null == contextValue.singularClass)
					singulaReturnClass = Object.class;
				else
					singulaReturnClass = contextValue.singularClass;
			}
			else {
				if (null == parentClass) {
					singulaReturnClass = Object.class;
				}
				else {
					content = Character.toUpperCase(content.charAt(1)) + content.substring(2, getContent().length());
					String getcontent = "get" + content; //$NON-NLS-1$
					for (int i=0; i<parentClass.getMethods().length; i++) {
						Method m = parentClass.getMethods()[i];
						if (m.getName().equals(content) || m.getName().equals(getcontent)) {
							Type type = m.getGenericReturnType();
							if (type instanceof ParameterizedType) {
								ParameterizedType pType = (ParameterizedType) type;
								if (pType.getActualTypeArguments().length > 0) {
									singulaReturnClass = (Class<?>) pType.getActualTypeArguments()[0];
									break;
								}
							}
							singulaReturnClass = Object.class;
							break;
						}
					}
				}
			}
		}
		return singulaReturnClass;
	}

	public boolean isStartFragment () {
		return !getContent().startsWith("."); //$NON-NLS-1$
	}

	public static final String[] invalidMethods = {
		"clone", "equals", "finalize", "getClass", "hashCode", "notify", "notifyAll", "toString", "wait"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	public ICompletionProposal[] getMethodCompletionProposals (int subOffset, int offset, Class<?> parentClass, IResource file) {
		if (instanceOf(parentClass, String.class)
				|| instanceOf(parentClass, Number.class)
				|| instanceOf(parentClass, Date.class)
				|| instanceOf(parentClass, Collection.class)
				|| instanceOf(parentClass, List.class)
				|| instanceOf(parentClass, Map.class))
			return null;
		String prefix = getContent().substring(1, subOffset);
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String pUpper = prefix.toUpperCase();
		for (int i=0; i<parentClass.getMethods().length; i++) {
			Method m = parentClass.getMethods()[i];
			if (!Modifier.isPublic(m.getModifiers())) continue;
			if (m.isSynthetic()) continue;
			if (m.getParameterTypes().length != 0) continue;
			String mName = m.getName();
			if (mName.startsWith("get") || mName.startsWith("is")) { //$NON-NLS-1$ //$NON-NLS-2$
			        String propertyName = mName.startsWith("get") ? mName.substring(3) : mName.substring(2); //$NON-NLS-1$
			        propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
			        if (propertyName.toUpperCase().startsWith(pUpper)) { //$NON-NLS-1$
        				StringBuilder display = new StringBuilder();
        				display.append(propertyName);
        				display.append(" - "); //$NON-NLS-1$
        				display.append(m.getReturnType().getName()); //$NON-NLS-1$
        				proposals.add(new CompletionProposal(propertyName,
        						offset - subOffset + 1, 
        						getContent().length()-1, 
        						propertyName.length(),
        						null, display.toString(), null, null));
        			}
			}
		}
		return completionProposals(proposals);
	}
}