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

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

public class HashInterpolationRule extends GenericInterpolationRule {

	private static final Token SUCCESS_TOKEN = new Token(PartitionType.HASH_INTERPOLATION.getContentType());

	@Override
	public IToken getSuccessToken() {
		return SUCCESS_TOKEN;
	}

	public HashInterpolationRule() {
		super(LexicalConstants.HASH);
	}

}
