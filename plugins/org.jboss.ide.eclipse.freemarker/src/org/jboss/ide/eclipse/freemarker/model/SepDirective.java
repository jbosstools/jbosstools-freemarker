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

public class SepDirective extends AbstractDirective {

	private SepEndDirective endDirective;

	public SepDirective (ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	    // Nothing to do
	}

	@Override
	public Item getStartItem() {
		return this;
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
    public boolean isAutoClosed() {
	    return true;
	};
	
	@Override
	public boolean relatesToItem(Item directive) {
		return directive instanceof SepEndDirective;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof SepEndDirective) {
			endDirective = (SepEndDirective) directive;
		}
	}

	@Override
	public Item getEndItem() {
		return endDirective;
	}

	@Override
	public Item getRelatedItem() {
	    return getEndItem();
	}
	
}