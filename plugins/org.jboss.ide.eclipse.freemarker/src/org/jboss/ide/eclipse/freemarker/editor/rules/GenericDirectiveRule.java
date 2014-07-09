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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class GenericDirectiveRule extends MultiLineRule {

	protected static final char[] START_SEQUENCES = {'<', '['};
	protected static Map<Character, Character> END_SEQUENCES = new HashMap<Character, Character>(START_SEQUENCES.length);
	
	static {
		END_SEQUENCES.put(new Character(START_SEQUENCES[0]), new Character('>'));
		END_SEQUENCES.put(new Character(START_SEQUENCES[1]), new Character(']'));
	}

	public GenericDirectiveRule(IToken token) {
		super("!", "!", token); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected boolean sequenceDetected(
		ICharacterScanner scanner,
		int startChar,
		boolean eofAllowed) {
		return true;
	}

	protected boolean endSequenceDetected(ICharacterScanner scanner, int startChar) {
		char endChar = END_SEQUENCES.get(new Character((char) startChar)).charValue();
		int c;
		char[][] delimiters= scanner.getLegalLineDelimiters();
		boolean previousWasEscapeCharacter = false;	
		Stack<String> keyStack = new Stack<String>();
		int charsRead = 0;
		while ((c= scanner.read()) != ICharacterScanner.EOF) {
			charsRead ++;
			@SuppressWarnings("unused")
			char cCheck = (char) c;
			if (c == startChar) {
				if (keyStack.size() == 0) {
					break;
				}
			}
			else if (c == '\"') {
				if (keyStack.size() > 0 && keyStack.peek().equals("\"")) { //$NON-NLS-1$
					keyStack.pop();
				}
				else {
					keyStack.push("\""); //$NON-NLS-1$
				}
			}
			else if (c == '(') {
				if (keyStack.size() > 0 && keyStack.peek().equals("\"")) { //$NON-NLS-1$
					// string... don't add to stack
				}
				else {
					keyStack.push("("); //$NON-NLS-1$
				}
			}
			else if (c == ')') {
				if (keyStack.size() > 0 && keyStack.peek().equals("\"")) { //$NON-NLS-1$
					// string... don't add to stack
				}
				else if (keyStack.size() > 0 && keyStack.peek().equals("(")) { //$NON-NLS-1$
					keyStack.pop();
				}
			}
			else if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
				charsRead ++;
			}
			else if (c == endChar) {
				if (keyStack.size() == 0) {
					return true;
				}
			}
			else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i= 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0] && sequenceDetected(scanner, delimiters[i], true)) {
						if (!fEscapeContinuesLine || !previousWasEscapeCharacter) {
							return true;
						}
					}
				}
			}
			previousWasEscapeCharacter = (c == fEscapeCharacter);
		}
		if (fBreaksOnEOF) return true;
		for (int i=0; i<charsRead; i++)
			scanner.unread();
		return false;
	}

	/**
	 * Evaluates this rules without considering any column constraints. Resumes
	 * detection, i.e. look sonly for the end sequence required by this rule if the
	 * <code>resume</code> flag is set.
	 *
	 * @param scanner the character scanner to be used
	 * @param resume <code>true</code> if detection should be resumed, <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	@Override
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		if (resume) {
			if (endSequenceDetected(scanner))
				return fToken;
		} else {
			int c= scanner.read();
			@SuppressWarnings("unused")
			char cCheck = (char) c;
			if (c == START_SEQUENCES[0] || c == START_SEQUENCES[1]) {
				// check for the sequence identifier
				int c2 = scanner.read();
				if (c2 == getIdentifierChar()) {
					if (sequenceDetected(scanner, c, false)) {
						if (endSequenceDetected(scanner, c))
							return fToken;
					}
				}
				scanner.unread();
			}
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	protected char getIdentifierChar() {
		return '#';
	}
}