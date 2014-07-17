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
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class OutlineContentProvider implements ITreeContentProvider {
	public enum OutlineLevelOfDetail {
		functionAndMacroDefinitions,
		full;
		public static OutlineLevelOfDetail getDefault() {
			return functionAndMacroDefinitions;
		}
	}

	/** Before JBIDE-15168, we have always shown the full AST.
	 * Now, we normally show only macro and function definitions,
	 * but an option to show the full AST is left here
	 * so that the present plugin's devs can easily visualize the AST
	 * to see if the code producing the AST works properly. */
	private boolean fullAstShown;

	private Editor fEditor;

	public OutlineContentProvider(Editor anEditor) {
		fEditor = anEditor;
		String level = Preferences.getInstance().getString(PreferenceKey.OUTLINE_LEVEL_OF_DETAIL);
		try {
			this.fullAstShown = level != null && OutlineContentProvider.OutlineLevelOfDetail.valueOf(level) == OutlineContentProvider.OutlineLevelOfDetail.full;
		} catch (IllegalArgumentException e) {
			this.fullAstShown = false;
		}
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
		if (fullAstShown) {
			Collection<Item> items = fEditor.getItemSet().getDirectiveRegions().values();
			List<Item> result = new ArrayList<Item>(items.size());
			for (Item item : items) {
				if (item.getParentItem() == null && !item.isEndItem()) {
					result.add(item);
				}
			}
			return result.toArray();
		}
		else {
			List<Item> outlineItems = fEditor.getItemSet().getOutlineItems();
			return outlineItems.toArray();
		}
	}

	@Override
	public Object[] getChildren(Object anElement) {
		if (fullAstShown && anElement instanceof Item) {
			/* we show  children only when debugging the AST parser */
			if (anElement instanceof MacroDirective) {
				return null;
			}
			List<Item> children = ((Item) anElement).getChildItems();
			List<Item> result = new ArrayList<Item>(children.size());
			for (Item item : children) {
				if (!item.isEndItem()) {
					result.add(item);
				}
			}
			return result.toArray();
		}
		else {
			return null;
		}
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
		if (fullAstShown && anElement instanceof Item) {
			Item item = (Item) anElement;
			return !item.getChildItems().isEmpty();
		}
		else {
			return false;
		}
	}
}