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
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;

/**
 * A {@link MultiLineRule} that matches a particular FTL directive start and marks the
 * region as the given {@link Directive}. Used for building an {@link ItemSet}.
 *
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class DirectiveRule extends MultiLineRule implements SyntaxModeListener {

	protected final String name;
	protected final boolean nameOnly;
	protected final char identifierChar;
	protected SyntaxMode syntaxMode = SyntaxMode.getDefault();

	public DirectiveRule(Directive directive) {
		this(directive, false);
	}

	public DirectiveRule(Directive directive, boolean nameOnly) {
		this(directive, nameOnly, LexicalConstants.HASH);
	}

	public DirectiveRule(Directive directive, boolean nameOnly,
			char identifierChar) {
		this(directive.getKeyword().toString(), directive.name(), nameOnly,
				identifierChar);
	}

	public DirectiveRule(String name, String tokenData, boolean nameOnly,
			char identifierChar) {
		super("!", "!", new Token(tokenData)); //$NON-NLS-1$ //$NON-NLS-2$
		this.name = name;
		this.nameOnly = nameOnly;
		this.identifierChar = identifierChar;
	}

	protected boolean sequenceDetected(ICharacterScanner scanner,
			int startChar, boolean eofAllowed) {
		for (int i = 0; i < name.length(); i++) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != name.charAt(i)) {
				// Non-matching character detected, rewind the scanner back to
				// the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j = i; j > 0; j--) {
					scanner.unread();
				}
				return false;
			}
		}

		return true;
	}

	protected boolean endSequenceDetected(ICharacterScanner scanner,
			int startChar) {
		char endChar = ParserUtils.getMatchingRightBracket(startChar);
		int c;
		char[][] delimiters = scanner.getLegalLineDelimiters();
		boolean previousWasEscapeCharacter = false;
		Stack<Character> keyStack = new Stack<Character>();
		int charsRead = 0;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			charsRead++;
			char cCheck = (char) c;
			if (nameOnly) {
				if (c != endChar) {
					scanner.unread();
					return false;
				} else {
					return true;
				}
			} else if (c == startChar) {
				int cNext = scanner.read();
				if (cNext == ICharacterScanner.EOF)
					break;
				if (cNext == LexicalConstants.HASH
						|| cNext == LexicalConstants.AT) {
					if (keyStack.size() == 0) {
						break;
					}
				} else {
					keyStack.push(Character.valueOf(cCheck));
					scanner.unread();
				}
			} else if (c == LexicalConstants.QUOT) {
				if (keyStack.size() > 0
						&& keyStack.peek().charValue() == LexicalConstants.QUOT) {
					keyStack.pop();
				} else {
					keyStack.push(Character.valueOf(cCheck));
				}
			} else if (c == LexicalConstants.LEFT_PARENTHESIS) {
				if (keyStack.size() > 0
						&& keyStack.peek().charValue() == LexicalConstants.QUOT) {
					// string... don't add to stack
				} else {
					keyStack.push(Character.valueOf(cCheck));
				}
			} else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
				if (keyStack.size() > 0
						&& keyStack.peek().charValue() == LexicalConstants.QUOT) {
					// string... don't add to stack
				} else if (keyStack.size() > 0
						&& keyStack.peek().charValue() == LexicalConstants.LEFT_PARENTHESIS) {
					keyStack.pop();
				}
			} else if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
				charsRead++;
			} else if (c == endChar) {
				if (keyStack.size() == 0) {
					return true;
				} else if (keyStack.peek().charValue() == startChar) {
					keyStack.pop();
				}
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the
				// pattern.
				for (int i = 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0]
							&& sequenceDetected(scanner, delimiters[i], true)) {
						if (!fEscapeContinuesLine
								|| !previousWasEscapeCharacter) {
							return true;
						}
					}
				}
			}
			previousWasEscapeCharacter = (c == fEscapeCharacter);
		}
		if (fBreaksOnEOF)
			return true;
		for (int i = 0; i < charsRead; i++) {
			scanner.unread();
		}
		return false;
	}

	/**
	 * Evaluates this rules without considering any column constraints. Resumes
	 * detection, i.e. look sonly for the end sequence required by this rule if
	 * the <code>resume</code> flag is set.
	 *
	 * @param scanner
	 *            the character scanner to be used
	 * @param resume
	 *            <code>true</code> if detection should be resumed,
	 *            <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	@Override
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		if (resume) {
			if (endSequenceDetected(scanner))
				return fToken;
		} else {
			int c = scanner.read();
			if (c == syntaxMode.getStart()) {
				// check for the sequence identifier
				int c2 = scanner.read();
				if (c2 == identifierChar) {
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

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		this.syntaxMode = syntaxMode;
	}

}