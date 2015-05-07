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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;

public class MacroEndInstance extends AbstractDirective {

	private MacroInstance macroInstance;
	private String name;

	public MacroEndInstance(ItemSet itemSet) {
		super(itemSet);
	}


	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
		name = getSplitValue(0);
	}


	@Override
	public boolean isEndItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof MacroInstance)
			macroInstance = (MacroInstance) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		if (directive instanceof MacroInstance) {
			MacroInstance macroInstance = (MacroInstance) directive;
			return macroInstance.relatesToItem(this);
		}
		else return false;
	}

	public MacroInstance getMacroDirective() {
		return macroInstance;
	}

	@Override
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			ArrayList<Item> l = new ArrayList<Item>();
			if (null != getMacroDirective()) {
				l.add(getMacroDirective());
			}
			relatedItems = l.toArray(new Item[l.size()]);
		}
		return relatedItems;
	}
	private Item[] relatedItems;

	@Override
	public Item getStartItem () {
		return getMacroDirective();
	}

	@Override
	public String getName() {
		return name;
	}

	private String contents;
	@Override
	public String getContents() {
		if (null == contents) {
			try {
				contents = getViewer().getDocument().get(
						getRegion().getOffset(), getRegion().getLength());
				contents = contents.trim();
				contents = contents.substring(3, contents.length()-1);

			}
			catch (BadLocationException e) {
				Plugin.log(e);
			}
		}
		return contents;
	}
}