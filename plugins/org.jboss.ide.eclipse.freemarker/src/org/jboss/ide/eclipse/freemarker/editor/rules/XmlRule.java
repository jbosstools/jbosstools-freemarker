/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor.rules;

import java.util.Stack;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class XmlRule extends MultiLineRule {

	public XmlRule(IToken token) {
		super(String.valueOf(LexicalConstants.LEFT_ANGLE_BRACKET), String.valueOf(LexicalConstants.RIGHT_ANGLE_BRACKET), token);
	}

	@Override
	protected boolean sequenceDetected(
		ICharacterScanner scanner,
		char[] sequence,
		boolean eofAllowed) {
		int c = scanner.read();
		if (sequence[0] == LexicalConstants.LEFT_ANGLE_BRACKET) {
			if (c == LexicalConstants.QUESTION_MARK) {
				// processing instruction - abort
				scanner.unread();
				return false;
			}
			if (c == LexicalConstants.EXCLAMATION_MARK) {
				scanner.unread();
				// comment - abort
				return false;
			}
			if (c == LexicalConstants.HASH) {
				scanner.unread();
				// directive - abort
				return false;
			}
		} else if (sequence[0] == LexicalConstants.RIGHT_ANGLE_BRACKET) {
			scanner.unread();
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		char[][] delimiters = scanner.getLegalLineDelimiters();
		boolean previousWasEscapeCharacter = false;
		Stack<Character> stack = new Stack<Character>();
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true)) {
					return true;
				}
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i = 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0] && sequenceDetected(scanner, delimiters[i], true)) {
						if (!fEscapeContinuesLine || !previousWasEscapeCharacter) {
							return true;
						}
					}
				}
			}
			else if (c == LexicalConstants.QUOT) {
				if (stack.size() > 0 && stack.peek().charValue() == LexicalConstants.QUOT) {
					stack.pop();
				}
			}
			else if (c == LexicalConstants.LEFT_ANGLE_BRACKET || c == LexicalConstants.LEFT_SQUARE_BRACKET) {
				break;
			}
			else if (c == LexicalConstants.DOLLAR) {
				int cNext = scanner.read();
				if (cNext == ICharacterScanner.EOF) {
					break;
				}
				else if (cNext == LexicalConstants.LEFT_BRACE) {
					stack.push(Character.valueOf((char) c));
					scanner.unread();
				}
				if (stack.size() == 0) {
					break;
				}
			}
			else if (c == LexicalConstants.RIGHT_BRACE) {
				if (stack.size() > 0 && stack.peek().charValue() == LexicalConstants.LEFT_BRACE) {
					stack.pop();
				}
			}
			previousWasEscapeCharacter = (c == fEscapeCharacter);
		}
		if (fBreaksOnEOF) {
			return true;
		}
		scanner.unread();
		return false;
	}
}
