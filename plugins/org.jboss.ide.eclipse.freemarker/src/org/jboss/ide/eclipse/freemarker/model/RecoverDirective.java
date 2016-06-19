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

public class RecoverDirective extends AbstractDirective {
    
	private AttemptDirective attemptDirective;

	public RecoverDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof AttemptDirective) {
		    attemptDirective = (AttemptDirective) directive;
		} else if (attemptDirective == null) {
			if (directive instanceof RecoverDirective) {
			    attemptDirective = ((RecoverDirective) directive).getAttemptDirective();
			} else if (directive instanceof AttemptEndDirective) {
			    attemptDirective = ((AttemptEndDirective) directive).getAttemptDirective();
			}
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

	public AttemptDirective getAttemptDirective() {
		return attemptDirective;
	}

	@Override
	public Item[] getRelatedItems() {
		return getAttemptDirective() != null ? getAttemptDirective().getRelatedItems() : null;
	}

	@Override
	public Item getStartItem () {
		return getAttemptDirective();
	}

	@Override
	public boolean isStartAndEndItem() {
		return true;
	}
}