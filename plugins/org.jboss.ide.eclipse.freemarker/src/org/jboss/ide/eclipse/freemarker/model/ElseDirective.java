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

public class ElseDirective extends AbstractDirective {
	private IfDirective ifDirective;
	private ListDirective listDirective;

	public ElseDirective(ItemSet itemSet) {
		super(itemSet);
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof IfDirective) {
			ifDirective = (IfDirective) directive;
		} else if (directive instanceof ListDirective) {
            listDirective = (ListDirective) directive;  
        } else if (ifDirective == null && listDirective == null) {
            // TODO: Do we need these?
			if (directive instanceof ElseIfDirective) ifDirective = ((ElseIfDirective) directive).getIfDirective();
			else if (directive instanceof ElseDirective) ifDirective = ((ElseDirective) directive).getIfDirective();
			else if (directive instanceof IfEndDirective) ifDirective = ((IfEndDirective) directive).getIfDirective();
            else if (directive instanceof ListEndDirective) listDirective = ((ListEndDirective) directive).getListDirective();
		}
		
		// TODO: Do we need these?
		if (directive instanceof IfEndDirective && ifDirective != null) {
			ifDirective.relateItem(directive);
		} else  if (directive instanceof ListEndDirective && listDirective != null) {
		    listDirective.relateItem(directive);
        }
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return directive instanceof IfDirective
				|| directive instanceof ElseDirective
				|| directive instanceof ElseIfDirective
				|| directive instanceof IfEndDirective
				|| directive instanceof ListDirective
                || directive instanceof ListEndDirective;
	}

	@Override
	public boolean isNestable() {
		return true;
	}

	public IfDirective getIfDirective() {
		return ifDirective;
	}

	public ListDirective getListDirective() {
        return listDirective;
    }

    @Override
	public Item[] getRelatedItems() {
        return getIfDirective() != null ? getIfDirective().getRelatedItems()
                : getListDirective() != null ? getListDirective().getRelatedItems()
                : null;
	}

	@Override
	public Item getStartItem () {
		return getIfDirective() != null ? getIfDirective()
		        : getListDirective() != null ? getListDirective()
                : null;
	}

	@Override
	public String getTreeImage() {
		return "else.png"; //$NON-NLS-1$
	}

	@Override
	public boolean isStartAndEndItem() {
		return true;
	}
}