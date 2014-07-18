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
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.model.interpolation.BuiltInFragment;
import org.jboss.ide.eclipse.freemarker.model.interpolation.Fragment;
import org.jboss.ide.eclipse.freemarker.model.interpolation.NameFragment;
import org.jboss.ide.eclipse.freemarker.model.interpolation.NullFragment;
import org.jboss.ide.eclipse.freemarker.model.interpolation.ParametersFragment;
import org.jboss.ide.eclipse.freemarker.model.interpolation.StringFragment;

public class Interpolation extends AbstractDirective {

	private List<Fragment> fragments;

	public Interpolation(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer,
			IResource resource) throws Exception {
	}

	@Override
	public String getTreeImage() {
		return "interpolation.png"; //$NON-NLS-1$
	}

	@Override
	public synchronized ICompletionProposal[] getCompletionProposals(
			int offset, Map<String, Class<?>> context) {
		Item parent = getParentItem();
		List<Item> parents = new ArrayList<Item>();
		Item tempItem = getParentItem();
		while (null != parent) {
			parents.add(0, parent);
			parent = parent.getParentItem();
			if (null != parent)
				tempItem = parent;
		}
		if (null == tempItem)
			tempItem = this;
		Item[] items = getItemSet().getRootItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (tempItem.equals(item))
				break;
			if (item.isStartItem()) {
				item.addToContext(context);
				if (null != item.getEndItem()
						&& item.getEndItem().getRegion().getOffset() < (offset - 1))
					item.removeFromContext(context);
			} else
				item.addToContext(context);
		}
		for (Iterator<Item> i = parents.iterator(); i.hasNext();) {
			Item item = i.next();
			for (Iterator<Item> i2 = item.getChildItems().iterator(); i2
					.hasNext();) {
				Item item2 = i2.next();
				if (parents.contains(item2))
					break;
				item2.addToContext(context);
			}
			item.addToContext(context);
		}

		initFragments();
		// find the fragment matching the offset
		int subOffset = offset - getOffset() - 2;
		if (subOffset < 0)
			return null;
		Fragment fragment = null;
		for (Iterator<Fragment> i = fragments.iterator(); i.hasNext();) {
			Fragment fragmentSub = i.next();
			if (fragmentSub.getOffset() <= subOffset)
				fragment = fragmentSub;
			else
				break;
		}
		if (null != fragment) {
			// find the parent class
			Class<?> parentClass = null;
			for (Iterator<Fragment> i = fragments.iterator(); i.hasNext();) {
				Fragment fragmentSub = i.next();
				if (fragmentSub.equals(fragment))
					break;
				else
					parentClass = fragmentSub.getReturnClass(parentClass,
							fragments, context, getResource(), getResource()
									.getProject());
			}
			return fragment.getCompletionProposals(
					subOffset - fragment.getOffset(), offset, parentClass,
					fragments, getViewer(), context, getResource(),
					getResource().getProject());
		} else if (getContents().length() == 0 && subOffset == 0) {
			return new NullFragment().getCompletionProposals(subOffset, offset,
					null, fragments, getViewer(), context, getResource(),
					getResource().getProject());
		} else
			return null;
	}

	private synchronized void initFragments() {
		if (null != fragments)
			return;
		fragments = new ArrayList<Fragment>();
		StringBuilder sb = new StringBuilder();
		String contents = getFullContents();
		contents = contents.substring(2, contents.length() - 1);
		int offsetStart = 0;
		boolean inString = false;
		boolean inBuiltIn = false;
		boolean inNameFragment = false;
		boolean inParameters = false;
		boolean escape = false;
		boolean doEscape = false;
		for (int i = 0; i < contents.length(); i++) {
			doEscape = false;
			char c = contents.charAt(i);
			if (Character.isLetterOrDigit(c) && !inString && !inBuiltIn
					&& !inNameFragment && !inParameters)
				inNameFragment = true;
			if (inNameFragment) {
				if (c == LexicalConstants.QUESTION_MARK) {
					fragments.add(new NameFragment(offsetStart, sb.toString()));
					offsetStart = i;
					sb.delete(0, sb.length());
					inNameFragment = false;
					inBuiltIn = true;
				} else if (c == LexicalConstants.LEFT_PARENTHESIS) {
					fragments.add(new NameFragment(offsetStart, sb.toString()));
					offsetStart = i;
					sb.delete(0, sb.length());
					inNameFragment = false;
					inParameters = true;
				} else if (c == LexicalConstants.PERIOD) {
					fragments.add(new NameFragment(offsetStart, sb.toString()));
					offsetStart = i;
					sb.delete(0, sb.length());
					inNameFragment = true;
					sb.append(c);
				} else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
					// for now, forget about the stack
					fragments.add(new NameFragment(offsetStart, sb.toString()));
					offsetStart = i + 1;
					sb.delete(0, sb.length());
					inNameFragment = false;
				} else
					sb.append(c);
			} else if (inBuiltIn) {
				if (c == LexicalConstants.QUESTION_MARK) {
					fragments.add(new BuiltInFragment(offsetStart, sb
							.toString()));
					offsetStart = i;
					sb.delete(0, sb.length());
				} else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
					fragments.add(new BuiltInFragment(offsetStart, sb
							.toString()));
					offsetStart = i + 1;
					sb.delete(0, sb.length());
					inBuiltIn = false;
				} else if (c == LexicalConstants.PERIOD) {
					fragments.add(new BuiltInFragment(offsetStart, sb
							.toString()));
					inNameFragment = true;
					offsetStart = i;
					sb.delete(0, sb.length());
					inBuiltIn = false;
					sb.append(c);
				} else
					sb.append(c);
			} else if (inParameters) {
				if (inString)
					if (!escape && c == LexicalConstants.QUOT)
						inString = false;
				if (!inString && c == LexicalConstants.RIGHT_PARENTHESIS) {
					fragments.add(new ParametersFragment(offsetStart, sb
							.toString()));
					offsetStart = i + 1;
					sb.delete(0, sb.length());
				} else
					sb.append(c);
			} else if (inString) {
				if (escape)
					sb.append(c);
				else if (c == LexicalConstants.QUOT) {
					fragments
							.add(new StringFragment(offsetStart, sb.toString()));
					offsetStart = i + 1;
					sb.delete(0, sb.length());
				} else if (c == LexicalConstants.BACKSLASH) {
					doEscape = true;
					sb.append(c);
				} else
					sb.append(c);
			} else if (c == LexicalConstants.PERIOD) {
				if (sb.length() > 0) {
					if (inBuiltIn)
						fragments.add(new BuiltInFragment(offsetStart, sb
								.toString()));
					else
						fragments.add(new NameFragment(offsetStart, sb
								.toString()));
				}
				inNameFragment = true;
				offsetStart = i;
				sb.delete(0, sb.length());
			} else if (c == LexicalConstants.QUESTION_MARK) {
				if (inBuiltIn)
					fragments.add(new BuiltInFragment(offsetStart, sb
							.toString()));
				else
					fragments.add(new NameFragment(offsetStart, sb.toString()));
				inBuiltIn = true;
				offsetStart = i;
				sb.delete(0, sb.length());
			} else if (c == LexicalConstants.LEFT_PARENTHESIS) {
				if (fragments.size() == 0) {
					// for now, forget about the stack
				} else {
					if (sb.length() > 0 && !inBuiltIn)
						fragments.add(new NameFragment(offsetStart, sb
								.toString()));
					inParameters = true;
					offsetStart = i;
					sb.delete(0, sb.length());
				}
			} else if (c == '"') {
				if (sb.length() > 0)
					fragments.add(new NameFragment(offsetStart, sb.toString()));
				inString = true;
				offsetStart = i;
				sb.delete(0, sb.length());
			} else
				sb.append(c);
			escape = doEscape;
		}
		if (sb.length() > 0 || inBuiltIn) {
			if (inBuiltIn)
				fragments.add(new BuiltInFragment(offsetStart, sb.toString()));
			else
				fragments.add(new NameFragment(offsetStart, sb.toString()));
		}
	}

	@Override
	public boolean isNestable() {
		return false;
	}

	public Class<?> getReturnClass(Map<String, Class<?>> context) {
		initFragments();
		Class<?> returnClass = null;
		for (Iterator<Fragment> i = fragments.iterator(); i.hasNext();) {
			Fragment fragment = i.next();
			returnClass = fragment.getReturnClass(returnClass, fragments,
					context, getResource(), getResource().getProject());
		}
		return returnClass;
	}

	public Class<?> getSingularReturnClass(Map<String, Class<?>> context) {
		initFragments();
		Class<?> returnClass = null;
		for (Iterator<Fragment> i = fragments.iterator(); i.hasNext();) {
			Fragment fragment = i.next();
			if (i.hasNext())
				returnClass = fragment.getReturnClass(returnClass, fragments,
						context, getResource(), getResource().getProject());
			else
				returnClass = fragment.getSingularReturnClass(returnClass,
						fragments, context, getResource(), getResource()
								.getProject());
		}
		return returnClass;
	}
}