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
package org.jboss.ide.eclipse.freemarker.outline;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class OutlineContentProvider implements ITreeContentProvider {
	private Editor fEditor;

	public OutlineContentProvider(Editor anEditor) {
		fEditor = anEditor;
	}

	@Override
	public void inputChanged(
		Viewer aViewer,
		Object anOldInput,
		Object aNewInput) {
	}

	public boolean isDeleted(Object anElement) {
		return false;
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Item> rootItems = new ArrayList<Item>();

		rootItems.addAll(fEditor.getItemSet().getMacroDefinitions());
		Item[] items = fEditor.getItemSet().getRootItems();
		for (int i=0; i<items.length; i++) {
			if (!(items[i] instanceof MacroDirective))
				rootItems.add(items[i]);
		}
		return rootItems.toArray();
	}

	@Override
	public Object[] getChildren(Object anElement) {
		if (anElement instanceof Item) {
			if (anElement instanceof MacroDirective) {
				return null;
			}
			List<Item> children = ((Item) anElement).getChildItems();
			Item[] items = children.toArray(new Item[children.size()]);
			List<Item> l = new ArrayList<Item>(items.length);
			for (int i=0; i<items.length; i++) {
				if (!(items[i] instanceof MacroDirective))
					l.add(items[i]);
			}
			return l.toArray();
		}
		else
			return null;
	}

	@Override
	public Object getParent(Object anElement) {
		if (anElement instanceof Item)
			return ((Item) anElement).getParentItem();
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object anElement) {
		if (anElement instanceof Item)
			if (anElement instanceof MacroDirective) return false;
			else return ((Item) anElement).getChildItems().size() > 0;
		else
			return false;
	}
}