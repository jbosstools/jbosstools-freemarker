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
package org.jboss.ide.eclipse.freemarker.editor.partitions;

import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

public abstract class GenericInterpolationRule extends ExpressionContainingPartitionRule {

	public GenericInterpolationRule(char firstChar) {
		super(new char[] { firstChar, LexicalConstants.LEFT_BRACE },
				new char[] { LexicalConstants.RIGHT_BRACE }, null,
				false);
	}

}