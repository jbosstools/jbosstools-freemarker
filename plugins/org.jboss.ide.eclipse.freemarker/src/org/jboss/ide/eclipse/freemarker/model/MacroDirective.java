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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

public class MacroDirective extends AbstractDirective implements
		Comparable<MacroDirective> {

	private MacroEndDirective endDirective;
	private String name;

	public MacroDirective(ItemSet itemSet) {
		super(itemSet);
	}

	public MacroDirective(ItemSet itemSet, String contents) {
		super(itemSet);
		this.contents = contents;
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer,
			IResource resource) {
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof MacroEndDirective)
			endDirective = (MacroEndDirective) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return (directive instanceof MacroEndDirective);
	}

	@Override
	public boolean isNestable() {
		return true;
	}

	public MacroEndDirective getEndDirective() {
		return endDirective;
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
		return "macro.png"; //$NON-NLS-1$
	}

	@Override
	public String getTreeDisplay() {
		return getSplitValue(1);
	}

	private String[] attributes;

	public String[] getAttributes() {
		if (null == attributes) {
			List<String> l = new ArrayList<String>();
			String[] contents = splitContents();
			int i = 2;
			while (i < contents.length) {
				String att = contents[i];
				int index = att.indexOf(LexicalConstants.EQUALS);
				if (index < 0) {
					att = att.trim();
					if (att.length() > 0) {
						switch (att.charAt(att.length() - 1)) {
						case LexicalConstants.RIGHT_ANGLE_BRACKET:
						case LexicalConstants.RIGHT_SQUARE_BRACKET:
							att = att.substring(0, att.length() - 1);
							break;
						default:
							break;
						}
					}
					l.add(att);
					i++;
				} else {
					i += 2;
				}
			}
			attributes = l.toArray(new String[l.size()]);
		}
		return attributes;
	}

	@Override
	public String getName() {
		if (null == name) {
			name = getSplitValue(1);
		}
		return name;
	}

	@Override
	public Item getEndItem() {
		return endDirective;
	}

	private static final char[] descriptorTokens = new char[] {
			LexicalConstants.SLASH, LexicalConstants.HASH, LexicalConstants.AT,
			LexicalConstants.LEFT_ANGLE_BRACKET,
			LexicalConstants.RIGHT_ANGLE_BRACKET };

	@Override
	public char[] getDescriptors() {
		return descriptorTokens;
	}

	@Override
	public int compareTo(MacroDirective arg0) {
		return nullToEmpty(getName()).compareTo(nullToEmpty(arg0.getName()));
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s; //$NON-NLS-1$
	}

	@Override
	public void addToContext(Map<String, Class<?>> context) {
		for (int i = 0; i < getAttributes().length; i++) {
			if (null == context.get(getAttributes()[i]))
				context.put(getAttributes()[i], Object.class);
		}
	}

	@Override
	public void removeFromContext(Map<String, Class<?>> context) {
		for (int i = 0; i < getAttributes().length; i++) {
			Object obj = context.get(getAttributes()[i]);
			if (null != obj && obj.equals(Object.class))
				context.remove(getAttributes()[i]);
		}
	}
}