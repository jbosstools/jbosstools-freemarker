/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Daniel Dekany 
 ******************************************************************************/
package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.jface.text.Position;

/**
 * Used for {@link Position}-s in the {@link DocumentProvider#RELATED_ITEM_POSITION_CATEGORY} category.  
 */
public class RelatedItemPosition extends Position {
	
	private final boolean highlighted;

	public RelatedItemPosition(int offset, int length, boolean highlighted) {
		super(offset, length);
		this.highlighted = highlighted;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

}
