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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class ItemsEndDirective extends AbstractDirective {

	private ItemsDirective itemsDirective;

	public ItemsEndDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	}

	@Override
	public boolean isEndItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof ItemsDirective)
			itemsDirective = (ItemsDirective) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return directive instanceof ItemsDirective;
	}

	public ItemsDirective getItemsDirective() {
		return itemsDirective;
	}

	@Override
	public Item getRelatedItem() {
		return getItemsDirective();
	}

	@Override
	public Item getStartItem () {
		return getItemsDirective();
	}
}