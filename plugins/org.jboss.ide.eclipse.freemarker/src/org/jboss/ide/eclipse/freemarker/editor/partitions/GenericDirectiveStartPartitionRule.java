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

import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

public abstract class GenericDirectiveStartPartitionRule extends ExpressionContainingPartitionRule
implements SyntaxModeListener {

	protected GenericDirectiveStartPartitionRule(char[] startSequence) {
		super(startSequence,
				getEndSequence(SyntaxMode.getDefault()),
				getAltEndSequence(SyntaxMode.getDefault()),
				getBraceAndBracketCanNotInhibitEndSequence(SyntaxMode.getDefault()));
	}

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		startSequence = getSyntaxModeTagStart(syntaxMode);
		endSequence = getEndSequence(syntaxMode);
		altEndSequence = getAltEndSequence(syntaxMode);
		braceAndBracketCanNotInhibitEndSequence = getBraceAndBracketCanNotInhibitEndSequence(syntaxMode);
	}

	private static boolean getBraceAndBracketCanNotInhibitEndSequence(SyntaxMode syntaxMode) {
		return syntaxMode.getStart() == LexicalConstants.LEFT_ANGLE_BRACKET;
	}

	private static char[] getEndSequence(SyntaxMode syntaxMode) {
		return syntaxMode.getTagEnd().toCharArray();
	}

	private static char[] getAltEndSequence(SyntaxMode syntaxMode) {
		return syntaxMode.getEmptyTagEnd().toCharArray();
	}
	
	protected abstract char[] getSyntaxModeTagStart(SyntaxMode syntaxMode);

}
