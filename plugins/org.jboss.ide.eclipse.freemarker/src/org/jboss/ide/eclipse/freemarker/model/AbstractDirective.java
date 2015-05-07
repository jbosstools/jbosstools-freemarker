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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.ide.eclipse.freemarker.Plugin;


public abstract class AbstractDirective extends AbstractItem {

	String contents;

	public AbstractDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	public String getContents() {
		if (null == contents) {
			contents = super.getContents();
			if (null != contents) {
				contents = contents.substring(2, contents.length()-1);
			}
		}
		return contents;
	}

	public static String[] directives = new String[] {
		"if", "else", "elseif", "switch", "case", "default", "break", "list", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		"break", "include", "import", "noparse", "compress", "escape", "noescape", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		"assign", "global", "local", "setting", "macro", "nested", "return", "flush", "function", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
		"stop", "ftl", "t", "lt", "rt", "nt", "attempt", "recover", "visit", "recurse", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
		"fallback" //$NON-NLS-1$
	};
	@Override
	public ICompletionProposal[] getCompletionProposals(int offset, Map<String, Class<?>> context) {
		if (offset < 2) return null;
		ContentWithOffset contentWithOffset = splitContents(offset);
		int index = contentWithOffset.getIndex();
		if (index == 0) {
			int subOffset = contentWithOffset.getOffsetInIndex();
			String[] contentWithOffsetContents = contentWithOffset.getContents();
			// name
			if (contentWithOffset.wasLastCharSpace()) {
				if (contentWithOffsetContents.length == 1) {
					// first param
					CompletionInterpolation completionInterpolation = new CompletionInterpolation(
							getItemSet() , "${", offset - contentWithOffset.getOffsetInIndex() - 2, getResource()); //$NON-NLS-1$
					return completionInterpolation.getCompletionProposals(offset, context);
				}
				else {
					return null;
				}
			}
			String prefix = contentWithOffsetContents[index].substring(0, subOffset);
			List<ICompletionProposal> l = new ArrayList<ICompletionProposal>();
			for (int i=0; i<directives.length; i++) {
				String name = directives[i];
				if (name.startsWith(prefix)) {
					l.add(getCompletionProposal(offset, subOffset,
							name, contentWithOffsetContents[0]));
				}
			}
			return completionProposals(l);
		}
		else if (index == 1 && !contentWithOffset.wasLastCharSpace()) {
			String value = ""; //$NON-NLS-1$
			value = contentWithOffset.getContents()[index].substring(0, contentWithOffset.getOffsetInIndex());
			CompletionInterpolation completionInterpolation = new CompletionInterpolation(
					getItemSet() , "${" + value, offset - contentWithOffset.getOffsetInIndex() - 2, getResource()); //$NON-NLS-1$
			return completionInterpolation.getCompletionProposals(offset, context);
		}
		return null;
	}

	public ICompletionProposal[] completionProposals (List<ICompletionProposal> l) {
		Collections.sort(l, COMPLETION_PROPOSAL_COMPARATOR);
		return l.toArray(new ICompletionProposal[l.size()]);
	}

	public ICompletionProposal getCompletionProposal (int offset, int subOffset,
			String replacementString, String replacingString) {
		return new CompletionProposal (
				replacementString, offset-subOffset,
				replacingString.length(), replacementString.length());
	}

	private static final Comparator<ICompletionProposal> COMPLETION_PROPOSAL_COMPARATOR = new Comparator<ICompletionProposal>() {
		@Override
		public int compare(ICompletionProposal arg0, ICompletionProposal arg1) {
			return arg0.getDisplayString().compareTo(arg1.getDisplayString());
		}
	};
}