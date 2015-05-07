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
package org.jboss.ide.eclipse.freemarker.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.MacroLibrary;


public class MacroInstance extends AbstractDirective {

	private MacroEndInstance endInstance;
	private String name;

	public MacroInstance(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
		name = getSplitValue(0);
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof MacroEndInstance)
			endInstance = (MacroEndInstance) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		if (directive instanceof MacroEndInstance) {
			MacroEndInstance endDirective = (MacroEndInstance) directive;
			return (null == endDirective.getName()
					|| endDirective.getName().length() == 0
					|| endDirective.getName().equals(getName()));
		}
		else return false;
	}

	public MacroEndInstance getEndDirective() {
		return endInstance;
	}

	@Override
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			ArrayList<Item> l = new ArrayList<Item>();
			if (null != getEndDirective())
				l.add(getEndDirective());
			relatedItems = l.toArray(new Item[l.size()]);
		}
		return relatedItems;
	}
	private Item[] relatedItems;

	@Override
	public String getTreeImage() {
		return "macro_instance.png"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ICompletionProposal[] getCompletionProposals(int offset, Map<String, Class<?>> context) {
		ContentWithOffset contentWithOffset = splitContents(offset);
		int index = contentWithOffset.getIndex();
		int subOffset = contentWithOffset.getOffsetInIndex();
		String[] contents = contentWithOffset.getContents();
		if (index == 0 && !contentWithOffset.wasLastCharSpace()) {
			// name
			String prefix = contents[index].substring(0, subOffset);
			List<ICompletionProposal> l = new ArrayList<ICompletionProposal>();
			for (Iterator<MacroDirective> i=getItemSet().getMacroDefinitions().iterator(); i.hasNext(); ) {
				MacroDirective macro = i.next();
				if (macro.getName().startsWith(prefix)) {
					l.add(getCompletionProposal(offset, subOffset,
							macro.getName(), contents[0]));
				}
			}
			MacroLibrary[] libraries = ConfigurationManager.getInstance(getResource().getProject()).getMacroLibraries();
			for (int i=0; i<libraries.length; i++) {
				for (int j=0; j<libraries[i].getMacros().length; j++) {
					MacroDirective macro = libraries[i].getMacros()[j];
					if (macro.getName().startsWith(prefix)) {
						l.add(getCompletionProposal(offset, subOffset,
								macro.getName(), contents[0]));
					}
				}
			}
			return completionProposals(l);
		}
		else if ((contentWithOffset.wasLastCharSpace())
				|| !contents[index-1].equals("=")) { //$NON-NLS-1$
			String name = contents[0];
			// see if we can find a macro match
			MacroDirective match = null;
			for (Iterator<MacroDirective> i=getItemSet().getMacroDefinitions().iterator(); i.hasNext(); ) {
				MacroDirective macro = i.next();
				if (macro.getName().equals(name)) {
					match = macro;
					break;
				}
			}
			if (null == match) {
				MacroLibrary[] libraries = ConfigurationManager.getInstance(getResource().getProject()).getMacroLibraries();
				for (int i=0; i<libraries.length; i++) {
					for (int j=0; j<libraries[i].getMacros().length; j++) {
						MacroDirective macro = libraries[i].getMacros()[j];
						if (macro.getName().equals(name)) {
							match = macro;
							break;
						}
					}
					if (null != match) break;
				}
			}
			if (null != match) {
				String prefix = null;
				if (contentWithOffset.wasLastCharSpace() || contents.length < index+1)
					prefix = ""; //$NON-NLS-1$
				else
					prefix = contents[index].substring(0, subOffset);
				List<ICompletionProposal> l = new ArrayList<ICompletionProposal>();
				for (int i=0; i<match.getAttributes().length; i++) {
					if (match.getAttributes()[i].startsWith(prefix)) {
						l.add(getCompletionProposal(offset, subOffset,
								match.getAttributes()[i], (contentWithOffset.isNextCharSpace() || contents.length < index+1) ? "" : contents[index])); //$NON-NLS-1$
					}
				}
				return completionProposals(l);
			}
		}
		return null;
	}
}