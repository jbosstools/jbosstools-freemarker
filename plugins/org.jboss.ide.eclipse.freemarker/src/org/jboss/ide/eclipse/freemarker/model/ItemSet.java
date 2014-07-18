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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;

public class ItemSet {

	private ISourceViewer viewer;
	private List<Item> directives;
	private List<Item> topLevelDirectives;
	private Map<Integer, Item> directiveRegions;
	private List<MacroDirective> macroDefinitions = new ArrayList<MacroDirective>();
	private List<Item> outlineItems = Collections.emptyList();

	public ItemSet (ISourceViewer viewer, List<ITypedRegion> regions, IResource resource) {
		this.viewer = viewer;
		List<Item> modifiableOutlineItems = new ArrayList<Item>();
		Map<Integer, Item> modifiableDirectiveRegions = new TreeMap<Integer, Item>();
		try {
			this.directives = new ArrayList<Item>();
			this.topLevelDirectives = new ArrayList<Item>();

			Stack<Item> stackDirectives = new Stack<Item>();
			for (ITypedRegion region : regions) {
				Item directive = ItemFactory.getItem(this, region, viewer, resource);
				if (null != directive) {
					if (directive instanceof MacroDirective) {
						macroDefinitions.add((MacroDirective) directive);
						modifiableOutlineItems.add(directive);
					}
					if (directive instanceof FunctionDirective) {
						modifiableOutlineItems.add(directive);
					}
					if (stackDirectives.size() == 0) {
						topLevelDirectives.add(directive);
					}
					modifiableDirectiveRegions.put(Integer.valueOf(region.getOffset()), directive);
					if (!directive.isEndItem()) {
						directives.add(directive);
					}
					if (!directive.isStartItem()) {
						Item directiveCheck = getFirstNestableItem(stackDirectives);
						if (directive.isStartAndEndItem()) {
							// not a true nestable but sub items will be nested
							if (null != directiveCheck && directiveCheck.isStartAndEndItem()) {
								if (directiveCheck.relatesToItem(directive)) {
									directiveCheck.relateItem(directive);
									directive.relateItem(directiveCheck);
								}
								stackDirectives.pop();
								directiveCheck = getFirstNestableItem(stackDirectives);
							}
							directiveCheck = getFirstNestableItem(stackDirectives);
							if (null != directiveCheck) {
								directiveCheck.addSubDirective(directive);
								directiveCheck.relateItem(directive);
								directive.relateItem(directiveCheck);
							}
							stackDirectives.push(directive);
						}
						else {
							// !directive.isStartAndEndItem()
							if (null != directiveCheck && directive.isEndItem() && directiveCheck.isStartAndEndItem()) {
								if (directiveCheck.relatesToItem(directive)) {
									directiveCheck.relateItem(directive);
									directive.relateItem(directiveCheck);
								}
								stackDirectives.pop();
								directiveCheck = getFirstNestableItem(stackDirectives);
							}
							if (null != directiveCheck && directiveCheck.relatesToItem(directive)) {
								directiveCheck.relateItem(directive);
								directive.relateItem(directiveCheck);
								if (directive.isEndItem()) {
									Item peek = stackDirectives.peek();
									while (null != peek && peek.relatesToItem(directive)) {
										if (peek.isStartItem()) {
											stackDirectives.pop();
											break;
										}
										else {
											stackDirectives.pop();
											peek = stackDirectives.size() > 0 ? stackDirectives.peek() : null;
										}
									}
								}
								else {
									directiveCheck.addSubDirective(directive);
									stackDirectives.push(directive);
								}
							}
							else if (!directive.isNestable() && !directive.isEndItem()) {
								if (null != directiveCheck) {
									directiveCheck.addSubDirective(directive);
								}
							}
							else if (directive.isNestable() && !directive.isEndItem()) {
								if (null != directiveCheck) {
									directiveCheck.addSubDirective(directive);
									stackDirectives.push(directive);
								}
							}
							else {
								// we have an invalid stack
								// FIXME come up with a better way to handle this
								return;
							}
						}
					}
					else {
						/* directive.isStartItem() == true */
						if (stackDirectives.size() > 0) {
							stackDirectives.peek().addSubDirective(directive);
						}
						if (directive.isNestable())
							stackDirectives.push(directive);
					}
				}
			}
		}
		catch (Exception e) {
			Plugin.log(e);
		}
		finally {
			this.outlineItems = Collections.unmodifiableList(modifiableOutlineItems);
			this.directiveRegions = Collections.unmodifiableMap(modifiableDirectiveRegions);
		}
		Collections.sort(macroDefinitions);
	}

	private static Item getFirstNestableItem(Stack<Item> stack) {
		if (stack.size() == 0) {
			return null;
		}
		else {
			for (ListIterator<Item> i = stack.listIterator(stack.size()); i.hasPrevious();){
				Item directiveCheck = i.previous();
				if (directiveCheck.isNestable()) {
					return directiveCheck;
				}
			}
			return null;
		}
	}

	public Item[] getRootItems () {
		return topLevelDirectives.toArray(
				new Item[topLevelDirectives.size()]);
	}

	/**
	 * Returns an unmodifiable {@link List} of directives to be used as an outline of the underlying Freemarker document.
	 * @return see above
	 */
	public List<Item> getOutlineItems() {
		return outlineItems;
	}


	/**
	 * Returns an unmodifiable {@link Map} from initial directive offset to directives.
	 * @return see above
	 */
	public Map<Integer, Item> getDirectiveRegions() {
		return directiveRegions;
	}

	public Item getSelectedItem (int offset) {
		ITypedRegion region = getRegion(offset);
		if (null == region) return null;
		else return directiveRegions.get(Integer.valueOf(region.getOffset()));
	}

	public Item getContextItem (int offset) {
		Item directive = getSelectedItem(offset);
		if (null == directive && null != directives) {
			Item dt = null;
			for (Iterator<Item> i=directives.iterator(); i.hasNext(); ) {
				Item t = i.next();
				if (t.isNestable() && t.getRegion().getOffset() < offset)
					dt = t;
				else if (t.isEndItem() && t.getRegion().getOffset() < offset)
					dt = null;
			}
			return dt;
		}
		else return directive;
	}

	private ITypedRegion getRegion (int offset) {
		try {
			return viewer.getDocument().getPartition(offset);
		}
		catch (BadLocationException e) {
			return null;
		}
	}

	public List<MacroDirective> getMacroDefinitions() {
		return macroDefinitions;
	}

	public Item getPreviousItem (int offset) {
		Item item = getContextItem(offset);
		if (null == item) {
			for (Iterator<Item> i=directives.iterator(); i.hasNext(); ) {
				Item itemSub = i.next();
				if (itemSub.getRegion().getOffset() + itemSub.getRegion().getOffset() < offset)
					item = itemSub;
				else
					break;
			}
		}
		return item;
	}

	public Item getPreviousStartItem (int offset) {
		Item item = null;
		for (Iterator<Item> i=directives.iterator(); i.hasNext(); ) {
			Item itemSub = i.next();
			if (itemSub.getRegion().getOffset() > offset) break;
			if (itemSub.isStartItem()) {
				Item itemSub2 = itemSub.getEndItem();
				if (null == itemSub2 || itemSub2.getRegion().getOffset() > offset)
					item = itemSub;
			}
		}
		return item;
	}

	public Item getItem (IRegion region) {
		return null == directiveRegions ? null : directiveRegions.get(region);
	}

	public Item getItem (int offset) {
		for (Item directive : directives) {
			ITypedRegion region = directive.getRegion();
			if (region.getOffset() <= offset && region.getOffset() + region.getLength() >= offset) {
				return directive;
			}
		}
		return null;
	}

}