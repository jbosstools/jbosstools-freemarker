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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.util.RobustStack;

public class ContentScanner implements ITokenScanner {
	enum ColoringStyle {
		UNKNOWN(PartitionType.createColoringToken(PreferenceKey.COLOR_TEXT)),
		INTERPOLATION(PartitionType.createColoringToken(PreferenceKey.COLOR_INTERPOLATION)),
		DIRECTIVE(PartitionType.createColoringToken(PreferenceKey.COLOR_DIRECTIVE)),
		STRING(PartitionType.createColoringToken(PreferenceKey.COLOR_STRING)),
		BRACKET_EXPRESSION(PartitionType.createColoringToken(PreferenceKey.COLOR_INTERPOLATION));

		private ColoringStyle(IToken token) {
			this.token = token;
		}
		private final IToken token;

		public IToken getToken() {
			return token;
		}
	}

	public static IToken createColoredToken(PreferenceKey preferenceKey) {
		return new Token(new TextAttribute(Preferences.getInstance().getColor(
				preferenceKey)));
	}

	private IDocument document;
	private int endOffset;
	private RobustStack<ColoringStyle> styleStack = new RobustStack<ColoringStyle>(ColoringStyle.UNKNOWN);
	private IToken defaultToken;
	private int tokenOffset;
	private int tokenLength;
	private RobustStack<Character> expressionStack = new RobustStack<Character>(Character.valueOf(Character.MIN_VALUE));

	private int currentOffset;

	public ContentScanner(IToken defaultToken) {
		this.defaultToken = defaultToken;
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.document = document;
		this.currentOffset = offset;
		this.endOffset = offset + length;
		this.styleStack.clear();
		this.expressionStack.clear();
	}

	@Override
	public IToken nextToken() {
		int offsetStart = currentOffset;
		int i = currentOffset;
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
					ColoringStyle type = styleStack.peek();
					if (c == LexicalConstants.BACKSLASH) {
						if (type.equals(ColoringStyle.STRING)) {
							doEscape = true;
						}
					} else if (c == LexicalConstants.QUOT || c == LexicalConstants.APOS) {
						if (type == ColoringStyle.STRING) {
							if (c == expressionStack.peek().charValue()) {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart + 1;
								this.currentOffset = i + 1;
								styleStack.pop();
								return type.getToken();
							}
						} else {
							if (i == offsetStart) {
								styleStack.push(ColoringStyle.STRING);
								expressionStack.push(Character.valueOf(c));
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart;
								this.currentOffset = i;
								return type.getToken();
							}
						}
					} else if (c == LexicalConstants.DOLLAR) {
						if (cNext == LexicalConstants.LEFT_BRACE) {
							// interpolation
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart;
							this.currentOffset = i;
							if (i == offsetStart) {
								styleStack.push(ColoringStyle.INTERPOLATION);
							} else {
								return type.getToken();
							}
						}
					} else if (c == LexicalConstants.RIGHT_BRACE) {
						if (type == ColoringStyle.INTERPOLATION) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							styleStack.pop();
							return type.getToken();
						}
					} else if (c == LexicalConstants.LEFT_PARENTHESIS) {
						if (type == ColoringStyle.INTERPOLATION) {
							styleStack.push(ColoringStyle.BRACKET_EXPRESSION);
						}
					} else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
						if (type == ColoringStyle.BRACKET_EXPRESSION) {
							styleStack.pop();
						}
					} else if ((c == LexicalConstants.LEFT_ANGLE_BRACKET || c == LexicalConstants.LEFT_SQUARE_BRACKET)
							&& !((styleStack.contains(ColoringStyle.DIRECTIVE) || styleStack
									.contains(ColoringStyle.INTERPOLATION)) && styleStack
									.contains(ColoringStyle.STRING))) {
						if (cNext == LexicalConstants.HASH) {
							// directive
							if (i == offsetStart) {
								expressionStack.push(Character.valueOf(c));
								styleStack.push(ColoringStyle.DIRECTIVE);
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return type.getToken();
							}
						} else if (cNext == LexicalConstants.AT) {
							// macro
							if (i == offsetStart) {
								expressionStack.push(Character.valueOf(c));
								styleStack.push(ColoringStyle.DIRECTIVE);
							} else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return type.getToken();
							}
						}
					} else if (ParserUtils.isClosingDirectiveBracket(c)
							&& !((styleStack.contains(ColoringStyle.DIRECTIVE)
									|| styleStack.contains(ColoringStyle.INTERPOLATION))
								&& styleStack.contains(ColoringStyle.STRING))) {
						char expressionStackPeek = expressionStack.peek().charValue();
						if (expressionStackPeek == ParserUtils.getMatchingLeftBracket(c)
								|| expressionStackPeek == Character.MIN_VALUE) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							if (expressionStackPeek != Character.MIN_VALUE) {
								styleStack.pop();
								return ColoringStyle.DIRECTIVE.getToken();
							} else {
								return ColoringStyle.UNKNOWN.getToken();
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
		return styleStack.peek().getToken();
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
