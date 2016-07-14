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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

import freemarker.template.utility.StringUtil;

/**
 * A {@link MultiLineRule} that matches an FTL directive end tags and marks them as
 * the {@link PartitionType#DIRECTIVE_END} partitions.
 */
public abstract class GenericDirectiveEndPartitionRule implements IPredicateRule, SyntaxModeListener {

	private final IToken successToken;
	private char[] startSequence;
	private char[] endSequence;

	protected GenericDirectiveEndPartitionRule(String startSequence, IToken successToken) {
		this.startSequence = startSequence.toCharArray();
		endSequence = SyntaxMode.getDefault().getTagEnd().toCharArray();
		this.successToken = successToken;
	}

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		startSequence = getStartSequence(syntaxMode);
		endSequence = syntaxMode.getTagEnd().toCharArray();
	}

	protected abstract char[] getStartSequence(SyntaxMode syntaxMode);

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (!resume) {
			if (!readSequence(scanner, startSequence, false)) {
				return Token.UNDEFINED;
			}
		}
		
		int lastC = 0;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == endSequence[0] && readSequence(scanner, endSequence, true)) {
				// endSequence fully consumed
				return successToken;
			}
			
			if ((!StringUtil.isFTLIdentifierPart((char) c)) && !Character.isWhitespace(c)
					&& c != LexicalConstants.PERIOD && c != LexicalConstants.BACKSLASH) {
				// The unexpected character is not part of this partition
				scanner.unread();
				
				// The previous '$' is not part of it either, of we are at '{':
				if (lastC == LexicalConstants.DOLLAR_INTERPOLATION_START[0]
						&& c == LexicalConstants.DOLLAR_INTERPOLATION_START[1]) {
					scanner.unread();
				}
				return successToken;
			}
			
			lastC = c;
		}
		
		return successToken; 
	}

	@Override
	public IToken getSuccessToken() {
		return successToken;
	}

	public char[] getStartSequence() {
		return startSequence;
	}

	public void setStartSequence(char[] startSequence) {
		this.startSequence = startSequence;
	}

	public char[] getEndSequence() {
		return endSequence;
	}

	public void setEndSequence(char[] endSequence) {
		this.endSequence = endSequence;
	}

	private boolean readSequence(
			ICharacterScanner scanner, char[] sequence, boolean firstCharAlreadyMatched) {
		int i = firstCharAlreadyMatched ? 1 : 0;
		while (i < sequence.length) {
			int c = scanner.read();
			if (c != sequence[i]) {
				for (int j = firstCharAlreadyMatched ? 1 : 0; j <= i; j++) {
					scanner.unread();
				}
				return false;
			}
			i++;
		}
		return true;
	}	
	
}
