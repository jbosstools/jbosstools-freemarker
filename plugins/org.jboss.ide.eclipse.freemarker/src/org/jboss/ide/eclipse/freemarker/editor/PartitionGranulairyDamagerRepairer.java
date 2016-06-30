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
package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;

/**
 * Modifies {@link DefaultDamagerRepairer} so that it always damages the whole
 * partition, rather than just a line in it. This is necessary if the highlighted syntax has state that crosses line
 * break (such as multiline string literals in FreeMarker expressions).
 */
public class PartitionGranulairyDamagerRepairer extends DefaultDamagerRepairer {

	public PartitionGranulairyDamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}

	/**
	 * Damages the whole partition.
	 */
	@Override
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
		return partition;
	}

}
