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

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class AttemptDirective extends AbstractDirective {

    private RecoverDirective recoverDirective;
	private AttemptEndDirective endDirective;

	public AttemptDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	    // Nothing to do
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof RecoverDirective) {
			recoverDirective = (RecoverDirective) directive;
		} else if (directive instanceof AttemptEndDirective) {
			endDirective = (AttemptEndDirective) directive;
		}
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return directive instanceof AttemptDirective
				|| directive instanceof RecoverDirective
				|| directive instanceof AttemptEndDirective;
	}

	@Override
	public boolean isNestable() {
		return true;
	}

	public RecoverDirective getRecoverDirective() {
		return recoverDirective;
	}

	public AttemptEndDirective getEndDirective() {
		return endDirective;
	}

	@Override
	public Item[] getRelatedItems() {
		if (relatedItems == null) {
			ArrayList<Item> items = new ArrayList<Item>(3);
			items.add(this);
			if (getRecoverDirective() != null) {
				items.add(getRecoverDirective());
			}
			if (getEndDirective() != null) {
				items.add(getEndDirective());
			}
			relatedItems = items.toArray(new Item[items.size()]);
		}
		return relatedItems;
	}
	
	private Item[] relatedItems;

	@Override
	public Item getEndItem() {
		return endDirective;
	}

}