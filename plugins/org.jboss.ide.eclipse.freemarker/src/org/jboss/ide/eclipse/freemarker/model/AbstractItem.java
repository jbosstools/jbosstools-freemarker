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
import java.util.Stack;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

public abstract class AbstractItem implements Item {

	private ITypedRegion region;
	private ISourceViewer viewer;
	private IResource resource;
	private List<Item> subDirectives;
	private Item parentItem;
	private final ItemSet itemSet;

	protected AbstractItem(ItemSet itemSet) {
		this.itemSet = itemSet;
	}

	@Override
	public final void load(ITypedRegion region, ISourceViewer viewer, IResource resource) {
		this.region = region;
		this.viewer = viewer;
		this.resource = resource;
		try {
			init(region, viewer, resource);
		}
		catch (Exception e) {
			Plugin.log(e);
		}
	}

	protected abstract void init (ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception;

	@Override
	public boolean isStartItem() {
		return false;
	}

	@Override
	public boolean isEndItem() {
		return false;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return false;
	}

	@Override
	public void relateItem(Item directive) {
		if (null == relatedItemsArr)
			relatedItemsArr = new ArrayList<Item>();
		relatedItemsArr.add(directive);
	}

	@Override
	public boolean isNestable() {
		return (null != getContents() && !getContents().endsWith("/")); //$NON-NLS-1$
	}

	@Override
	public ITypedRegion getRegion() {
		return region;
	}

	@Override
	public List<Item> getChildItems() {
		if (null == subDirectives) {
			subDirectives = new ArrayList<Item>(0);
		}
		return subDirectives;
	}

	@Override
	public void addSubDirective(Item directive) {
		getChildItems().add(directive);
		directive.setParentItem(this);
	}

	public ISourceViewer getViewer() {
		return viewer;
	}

	protected Item getRelatedItem() {
		return null;
	}

	protected Item[] relatedItems;
	protected List<Item> relatedItemsArr;
	@Override
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			if (null != relatedItemsArr) {
				relatedItems = relatedItemsArr.toArray(new Item[relatedItemsArr.size()]);
			}
			else if (null == getRelatedItem()) {
				relatedItems = new Item[0];
			}
			else {
				relatedItems = new Item[] {getRelatedItem()};
			}
		}
		return relatedItems;
	}

	private String contents;
	@Override
	public String getContents () {
		if (null == contents) {
			contents = getFullContents();
			if (null != contents) contents = contents.trim();
		}
		return contents;
	}

	private ContentWithOffset standardSplit;
	public ContentWithOffset splitContents (int offset) {
		if (offset == -1 && null != standardSplit) return standardSplit;
		String s = getFullContents();
		if (null == s) {
			return new ContentWithOffset(new String[0], -1, -1, -1, -1, -1, -1, false, false);
		}
		int actualIndex = 0;
		int actualIndexOffset = 0;
		int actualOffset = 0;
		int indexOffset = 0;
		int offsetCount = 0;
		int totalOffsetCount = 0;
		int spacesEncountered = 0;
		int totalSpacesEncountered = 0;
		int cursorPos = getCursorPosition(offset);
		List<String> arr = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		Stack<Character> currentStack = new Stack<Character>();
		boolean escape = false;
		boolean doEscape = false;
		boolean doAppend = true;
		boolean encounteredSpace = false;
		boolean nextCharSpace = false;
		for (int i=0; i<s.length(); i++) {
			encounteredSpace = false;
			char c = s.charAt(i);
			if (totalOffsetCount == cursorPos) {
				actualIndex = arr.size();
				actualOffset = totalOffsetCount;
				indexOffset = offsetCount;
				actualIndexOffset = offset - cursorPos - indexOffset;
				if (c == LexicalConstants.SPACE) nextCharSpace = true;
			}
			totalOffsetCount++;
			if (c == LexicalConstants.SPACE || c == LexicalConstants.EQUALS || c == LexicalConstants.CR || c == LexicalConstants.LF) {
				// we're probably going to split here
				if (current.length() != 0) {
					if (currentStack.size() == 0) {
						arr.add(current.toString());
						current = new StringBuilder();
						offsetCount = 0;
						if (c == LexicalConstants.EQUALS) {
							arr.add("="); //$NON-NLS-1$
							current = new StringBuilder();
						}
						else {
							encounteredSpace = true;
							spacesEncountered ++;
							totalSpacesEncountered ++;
						}
					}
					doAppend = false;
				}
				else {
					// just continue
				}
			}
			if (!escape) {
				if (c == LexicalConstants.QUOT) {
					if (currentStack.size() > 0) {
						if (currentStack.peek().charValue() == LexicalConstants.QUOT)
							currentStack.pop();
						else
							currentStack.push(Character.valueOf(c));
					}
					else
						currentStack.push(Character.valueOf(c));

				}
				else if (c == LexicalConstants.LEFT_PARENTHESIS) {
					currentStack.push(Character.valueOf(c));
				}
				else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
					if (currentStack.size() > 0 && currentStack.peek().charValue() == LexicalConstants.RIGHT_PARENTHESIS)
						currentStack.pop();
				}
				else if (c == LexicalConstants.LEFT_BRACE) {
					currentStack.push(Character.valueOf(c));
				}
				else if (c == LexicalConstants.RIGHT_BRACE) {
					if (currentStack.size() > 0 && currentStack.peek().charValue() == LexicalConstants.LEFT_BRACE)
						currentStack.pop();
				}
				else if (c == LexicalConstants.BACKSLASH) {
					doEscape = true;
				}
				else {
					for (int j=0; j<getDescriptors().length; j++) {
						if (c == getDescriptors()[j]) {
							doAppend = false;
							break;
						}
					}
				}
			}
			if (doAppend) {
				current.append(c);
				offsetCount++;
			}
			escape = doEscape;
			doEscape = false;
			doAppend = true;
		}
		if (current.length() > 0) {
			arr.add(current.toString());
			if (totalOffsetCount == cursorPos) {
				actualOffset = totalOffsetCount;
				indexOffset = offsetCount;
				actualIndexOffset = offset - cursorPos - indexOffset;
			}
		}
		else if (arr.size() == 0) {
			arr.add(""); //$NON-NLS-1$
		}
		if (totalOffsetCount == cursorPos) {
			actualIndex = arr.size()-1;
			actualOffset = totalOffsetCount;
			indexOffset = offsetCount;
			actualIndexOffset = offset - cursorPos - indexOffset;
		}
		ContentWithOffset contentWithOffset = new ContentWithOffset(
				arr.toArray(new String[arr.size()]),
				actualIndex, actualIndexOffset, indexOffset, actualOffset, spacesEncountered,
				totalSpacesEncountered, encounteredSpace, nextCharSpace);
		if (offset == -1) standardSplit = contentWithOffset;
		return contentWithOffset;
	}

	protected int getCursorPosition (int offset) {
		return offset - getOffset();
	}

	public String[] splitContents () {
		ContentWithOffset rtn = splitContents(-1);
		return rtn.getContents();
	}

	public class ContentWithOffset {
		private String[] contents;
		private int index;
		private int indexOffset;
		private int offsetInIndex;
		private int offset;
		private int spacesEncountered;
		private int totalSpacesEncountered;
		private boolean wasLastCharSpace;
		private boolean isNextCharSpace;

		public ContentWithOffset (String[] contents, int index, int indexOffset, int offsetInIndex,
				int offset, int spacesEncountered, int totalSpacesEncountered,
				boolean wasLastCharSpace, boolean isNextCharSpace) {
			this.contents = contents;
			this.index = index;
			this.offsetInIndex = offsetInIndex;
			this.indexOffset = indexOffset;
			this.offset = offset;
			this.spacesEncountered = spacesEncountered;
			this.totalSpacesEncountered = totalSpacesEncountered;
			this.wasLastCharSpace = wasLastCharSpace;
			this.isNextCharSpace = isNextCharSpace;
		}

		public String[] getContents() {
			return contents;
		}
		public void setContents(String[] contents) {
			this.contents = contents;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getOffsetInIndex() {
			return offsetInIndex;
		}

		public int getSpacesEncountered() {
			return spacesEncountered;
		}

		public int getTotalSpacesEncountered() {
			return totalSpacesEncountered;
		}

		public boolean wasLastCharSpace() {
			return wasLastCharSpace;
		}

		public boolean isNextCharSpace() {
			return isNextCharSpace;
		}

		public int getIndexOffset() {
			return indexOffset;
		}
	}

	@Override
	public Item getParentItem() {
		return parentItem;
	}

	@Override
	public void setParentItem(Item parentItem) {
		this.parentItem = parentItem;
	}

	@Override
	public Item getStartItem () {
		return this;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Item) {
			return ((Item) arg0).getRegion().equals(getRegion());
		}
		else return false;
	}

	@Override
	public int hashCode() {
		return getRegion().hashCode();
	}

	private String treeDisplay;
	@Override
	public String getTreeDisplay() {
		if (null == treeDisplay) {
			treeDisplay = getContents();
			if (null != treeDisplay
					&& treeDisplay.length() > 0
					&& treeDisplay.charAt(treeDisplay.length() - 1) == LexicalConstants.SLASH) {
				treeDisplay = treeDisplay.substring(0, treeDisplay.length()-1);
			}
			treeDisplay = treeDisplay.trim();
		}
		return treeDisplay;
	}

	@Override
	public String getTreeImage() {
		return null;
	}

	@Override
	public boolean isStartAndEndItem() {
		return false;
	}

	public String getSplitValue (int index) {
		String[] values = splitContents();
		if (null != values && values.length > index)
			return values[index];
		else return null;
	}

	@Override
	public ICompletionProposal[] getCompletionProposals(int offset, Map<String, Class<?>> context) {
		return null;
	}

	private static final char[] descriptorTokens = new char[]{LexicalConstants.SLASH,LexicalConstants.HASH,LexicalConstants.AT,LexicalConstants.LEFT_SQUARE_BRACKET,LexicalConstants.RIGHT_SQUARE_BRACKET,LexicalConstants.LEFT_ANGLE_BRACKET,LexicalConstants.RIGHT_ANGLE_BRACKET};
	public char[] getDescriptors () {
		return descriptorTokens;
	}

	public ItemSet getItemSet() {
		return itemSet;
	}

	public String getFullContents () {
		try {
			return viewer.getDocument().get(
					region.getOffset(), region.getLength());
		}
		catch (BadLocationException e) {
			return null;
		}
	}

	public int getOffset () {
		return getRegion().getOffset();
	}

	public int getLength () {
		return getRegion().getLength();
	}

	String firstToken = null;
	@Override
	public String getFirstToken() {
		if (null == firstToken) {
			StringBuilder sb = new StringBuilder();
			String content = getContents();
			for (int i=0; i<content.length(); i++) {
				char c = content.charAt(i);
				if (c == LexicalConstants.QUOT) {
					return null;
				}
				else if (c == LexicalConstants.QUESTION_MARK) {
					firstToken = sb.toString();
					break;
				}
				else if (c == LexicalConstants.SPACE || c == LexicalConstants.LEFT_PARENTHESIS || c == LexicalConstants.RIGHT_PARENTHESIS && sb.length() > 0) {
					firstToken = sb.toString();
					break;
				}
				else {
					sb.append(c);
				}
			}
		}
		return firstToken;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	@Override
	public void addToContext(Map<String, Class<?>> context) {
	}

	@Override
	public void removeFromContext(Map<String, Class<?>> context) {
	}

	@Override
	public Item getEndItem() {
		return null;
	}

	@Override
	public String getName() {
		return getFirstToken();
	}
}