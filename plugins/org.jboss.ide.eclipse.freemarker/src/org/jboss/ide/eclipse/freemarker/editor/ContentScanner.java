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
package org.jboss.ide.eclipse.freemarker.editor;

import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

public class ContentScanner implements ITokenScanner {
	enum TokenType {
		TYPE_UNKNOWN, TYPE_INTERPOLATION, TYPE_DIRECTIVE, TYPE_STRING, TYPE_BRACKET_EXPRESSION
	}

	private static final IToken STRING_TOKEN = createColoredToken(PreferenceKey.COLOR_STRING);
	private static final IToken INTERPOLATION_TOKEN = createColoredToken(PreferenceKey.COLOR_INTERPOLATION);
	private static final IToken DIRECTIVE_TOKEN = createColoredToken(PreferenceKey.COLOR_DIRECTIVE);

	public static IToken createColoredToken(PreferenceKey preferenceKey) {
		return new Token(new TextAttribute(Preferences.getInstance().getColor(
				preferenceKey)));
	}

	private IDocument document;
	private int endOffset;
	private Stack<TokenType> stack = new Stack<TokenType>();
	private IToken defaultToken;
	private int tokenOffset;
	private int tokenLength;
	private Stack<Character> stringTypes = new Stack<Character>();

	private int currentOffset;

	public ContentScanner(IToken defaultToken) {
		this.defaultToken = defaultToken;
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.document = document;
		this.currentOffset = offset;
		this.endOffset = offset + length;
		this.stack.clear();
		this.stringTypes.clear();
	}

	@Override
	public IToken nextToken() {
		int offsetStart = currentOffset;
		int i = currentOffset;
		char directiveTypeChar = Character.MIN_VALUE;
		boolean escape = false;
		boolean doEscape = false;
		try {
			char c = document.getChar(i);
			char cNext = Character.MIN_VALUE;
			if (document.getLength() > i + 2) {
				cNext = document.getChar(i + 1);
			}
			if (i >= endOffset) {
				return Token.EOF;
			}
			while (i < endOffset) {
				doEscape = false;
				if (!escape) {
					TokenType type = peek();
					if (c == LexicalConstants.BACKSLASH) {
						if (type.equals(TokenType.TYPE_STRING)) {
							doEscape = true;
						}
					} else if (c == LexicalConstants.QUOT || c == LexicalConstants.APOS) {
						if (type.equals(TokenType.TYPE_STRING)) {
							if (stringTypes.size() > 0
									&& c == stringTypes.peek().charValue()) {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart + 1;
								this.currentOffset = i + 1;
								pop();
								return STRING_TOKEN;
							}
						} else {
							if (i == offsetStart) {
								push(TokenType.TYPE_STRING);
								stringTypes.push(Character.valueOf(c));
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart;
								this.currentOffset = i;
								return getToken(type);
							}
						}
					} else if (c == LexicalConstants.DOLLAR) {
						if (cNext == LexicalConstants.LEFT_BRACE) {
							// interpolation
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart;
							this.currentOffset = i;
							if (i == offsetStart) {
								push(TokenType.TYPE_INTERPOLATION);
							} else {
								return getToken(type);
							}
						}
					} else if (c == LexicalConstants.RIGHT_BRACE) {
						if (type.equals(TokenType.TYPE_INTERPOLATION)) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							pop();
							return INTERPOLATION_TOKEN;
						}
					} else if (c == LexicalConstants.LEFT_PARENTHESIS) {
						if (type.equals(TokenType.TYPE_INTERPOLATION)) {
							push(TokenType.TYPE_BRACKET_EXPRESSION);
						}
					} else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
						if (type.equals(TokenType.TYPE_BRACKET_EXPRESSION)) {
							pop();
						}
					} else if ((c == LexicalConstants.LEFT_ANGLE_BRACKET || c == LexicalConstants.LEFT_SQUARE_BRACKET)
							&& !((stack.contains(TokenType.TYPE_DIRECTIVE) || stack
									.contains(TokenType.TYPE_INTERPOLATION)) && stack
									.contains(TokenType.TYPE_STRING))) {
						if (cNext == LexicalConstants.HASH) {
							// directive
							if (i == offsetStart) {
								directiveTypeChar = c;
								push(TokenType.TYPE_DIRECTIVE);
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return getToken(type);
							}
						} else if (cNext == LexicalConstants.AT) {
							// macro
							if (i == offsetStart) {
								directiveTypeChar = c;
								push(TokenType.TYPE_DIRECTIVE);
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return getToken(type);
							}
						}
					} else if ((c == LexicalConstants.RIGHT_SQUARE_BRACKET || c == LexicalConstants.RIGHT_ANGLE_BRACKET)
							&& !((stack.contains(TokenType.TYPE_DIRECTIVE) || stack
									.contains(TokenType.TYPE_INTERPOLATION)) && stack
									.contains(TokenType.TYPE_STRING))) {
						if ((c == LexicalConstants.RIGHT_SQUARE_BRACKET && directiveTypeChar == LexicalConstants.LEFT_SQUARE_BRACKET)
								|| (c == LexicalConstants.RIGHT_ANGLE_BRACKET && directiveTypeChar == LexicalConstants.LEFT_ANGLE_BRACKET)
								|| directiveTypeChar == Character.MIN_VALUE) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							if (directiveTypeChar != Character.MIN_VALUE) {
								pop();
								return DIRECTIVE_TOKEN;
							} else {
								return defaultToken;
							}
						}
					}
				}
				c = document.getChar(++i);
				cNext = Character.MIN_VALUE;
				if (document.getLength() > i + 2) {
					cNext = document.getChar(i + 1);
				}
				escape = doEscape;
			}
		} catch (BadLocationException e) {
			this.currentOffset = i;
			this.tokenOffset = offsetStart;
			this.tokenLength = endOffset - tokenOffset;
			if (tokenLength > 0) {
				// last token
				return defaultToken;
			} else {
				return Token.EOF;
			}

		}
		this.currentOffset = i + 1;
		this.tokenOffset = offsetStart;
		this.tokenLength = endOffset - tokenOffset;
		return getToken(peek());
	}

	private TokenType peek() {
		if (stack.size() > 0) {
			return stack.peek();
		}
		else {
			return TokenType.TYPE_UNKNOWN;
		}
	}

	private void push(TokenType s) {
		stack.push(s);
	}

	private TokenType pop() {
		if (stack.size() > 0)
			return stack.pop();
		else
			return TokenType.TYPE_UNKNOWN;
	}

	private IToken getToken(TokenType type) {
		switch (type) {
		case TYPE_DIRECTIVE:
			return DIRECTIVE_TOKEN;
		case TYPE_INTERPOLATION:
		case TYPE_BRACKET_EXPRESSION:
			return INTERPOLATION_TOKEN;
		case TYPE_STRING:
			return STRING_TOKEN;
		case TYPE_UNKNOWN:
		default:
			return defaultToken;
		}
		// if (type.equals(TokenType.TYPE_DIRECTIVE)) return DIRECTIVE_TOKEN;
		//		else if (type.equals(TokenType.TYPE_INTERPOLATION) || type.equals("(")) return INTERPOLATION_TOKEN; //$NON-NLS-1$
		// else if (type.equals(TokenType.TYPE_STRING)) return STRING_TOKEN;
		// else return defaultToken;
	}

	@Override
	public int getTokenOffset() {
		return tokenOffset;
	}

	@Override
	public int getTokenLength() {
		return tokenLength;
	}

}
