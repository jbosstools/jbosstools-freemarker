/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.freemarker.model;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;


public class ItemsDirective extends AbstractDirective {

	private ItemsEndDirective endDirective;

	public ItemsDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof ItemsEndDirective)
			endDirective = (ItemsEndDirective) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return (directive instanceof ItemsEndDirective);
	}

	@Override
	public boolean isNestable() {
		return true;
	}

	public ItemsEndDirective getEndDirective() {
		return endDirective;
	}

	@Override
	public Item getRelatedItem() {
		return getEndDirective();
	}

	@Override
	public String getTreeImage() {
		return "list.png"; //$NON-NLS-1$
	}

	@Override
	public void addToContext(Map<String, Class<?>> context) {
		String[] contents = splitContents();
		if (contents.length == 3) {
			String key = contents[2];
			CompletionInterpolation completionInterpolation = new CompletionInterpolation(getItemSet(), "${" + contents[1], 0, getResource()); //$NON-NLS-1$
			context.put(key, completionInterpolation.getSingularReturnClass(context));
		}
	}

	@Override
	public void removeFromContext(Map<String, Class<?>> context) {
		String[] contents = splitContents();
		if (contents.length == 3) {
			String key = contents[2];
			context.remove(key);
		}
	}

	@Override
	public Item getEndItem() {
		return endDirective;
	}
}