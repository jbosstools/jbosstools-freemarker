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
package org.jboss.ide.eclipse.freemarker.lang;

import freemarker.core.FMParser;

/**
 * Freemarker parser utilities. We are forced to implement these methods because
 * {@link FMParser} restricts access to what we'd need here. We do not call it
 * Parser, because it is not a full blown parser. It covers just things we need in the plugin.
 *
 * See <a href="https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj">FTL.jj</a>
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class ParserUtils implements LexicalConstants {
	/**
	 * An Exception thrown on parse errors.
	 *
	 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
	 *
	 */
	public static class ParseException extends Exception {
		private static final long serialVersionUID = 1718455070531564155L;
		private final int offset;

		public ParseException(String message, int offset) {
			super(message);
			this.offset = offset;
		}

		public int getOffset() {
			return offset;
		}

	}

	public static final int INVALID_OFFSET = -1;

	/**
	 * A shorthand for {@code ch >= lower && ch <= upper}
	 *
	 * @param ch
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static boolean isBetween(char ch, char lower, char upper) {
		return ch >= lower && ch <= upper;
	}

	/**
	 * Returns {@code true} if and only if the given character is a digit as
	 * defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 *   < #DIGIT:
	 *       [
	 *        "\u0030"-"\u0039",
	 *        "\u0660"-"\u0669",
	 *        "\u06f0"-"\u06f9",
	 *        "\u0966"-"\u096f",
	 *        "\u09e6"-"\u09ef",
	 *        "\u0a66"-"\u0a6f",
	 *        "\u0ae6"-"\u0aef",
	 *        "\u0b66"-"\u0b6f",
	 *        "\u0be7"-"\u0bef",
	 *        "\u0c66"-"\u0c6f",
	 *        "\u0ce6"-"\u0cef",
	 *        "\u0d66"-"\u0d6f",
	 *        "\u0e50"-"\u0e59",
	 *        "\u0ed0"-"\u0ed9",
	 *        "\u1040"-"\u1049"
	 *       ]
	 *   >
	 * </pre>
	 *
	 * @param ch
	 * @return
	 */
	public static boolean isDigit(char ch) {
		return isBetween(ch, '\u0030', '\u0039')
				|| isBetween(ch, '\u0660', '\u0669')
				|| isBetween(ch, '\u06f0', '\u06f9')
				|| isBetween(ch, '\u0966', '\u096f')
				|| isBetween(ch, '\u09e6', '\u09ef')
				|| isBetween(ch, '\u0a66', '\u0a6f')
				|| isBetween(ch, '\u0ae6', '\u0aef')
				|| isBetween(ch, '\u0b66', '\u0b6f')
				|| isBetween(ch, '\u0be7', '\u0bef')
				|| isBetween(ch, '\u0c66', '\u0c6f')
				|| isBetween(ch, '\u0ce6', '\u0cef')
				|| isBetween(ch, '\u0d66', '\u0d6f')
				|| isBetween(ch, '\u0e50', '\u0e59')
				|| isBetween(ch, '\u0ed0', '\u0ed9')
				|| isBetween(ch, '\u1040', '\u1049');
	}

	/**
	 * Returns {@code true} if and only if the given character is a letter as
	 * defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 *   < #LETTER:
	 *       [
	 *        "\u0024",
	 *        "\u0040"-"\u005a",
	 *        "\u005f",
	 *        "\u0061"-"\u007a",
	 *        "\u00c0"-"\u00d6",
	 *        "\u00d8"-"\u00f6",
	 *        "\u00f8"-"\u00ff",
	 *        "\u0100"-"\u1fff",
	 *        "\u3040"-"\u318f",
	 *        "\u3300"-"\u337f",
	 *        "\u3400"-"\u3d2d",
	 *        "\u4e00"-"\u9fff",
	 *        "\uf900"-"\ufaff"
	 *       ]
	 *   >
	 * </pre>
	 *
	 * @param ch
	 * @return
	 */
	public static boolean isLetter(char ch) {
		return ch == '\u0024' || isBetween(ch, '\u0040', '\u005a')
				|| ch == '\u005f' || isBetween(ch, '\u0061', '\u007a')
				|| isBetween(ch, '\u00c0', '\u00d6')
				|| isBetween(ch, '\u00d8', '\u00f6')
				|| isBetween(ch, '\u00f8', '\u00ff')
				|| isBetween(ch, '\u0100', '\u1fff')
				|| isBetween(ch, '\u3040', '\u318f')
				|| isBetween(ch, '\u3300', '\u337f')
				|| isBetween(ch, '\u3400', '\u3d2d')
				|| isBetween(ch, '\u4e00', '\u9fff')
				|| isBetween(ch, '\uf900', '\ufaff');
	}

	public static char getMatchingRightBracket(int leftBracket) {
		switch (leftBracket) {
		case LexicalConstants.LEFT_ANGLE_BRACKET:
			return LexicalConstants.RIGHT_ANGLE_BRACKET;
		case LexicalConstants.LEFT_SQUARE_BRACKET:
			return LexicalConstants.RIGHT_SQUARE_BRACKET;
		default:
			throw new IllegalArgumentException("getMatchingEndCharacter() supported only for startCharacter '"+ LexicalConstants.LEFT_ANGLE_BRACKET +"' or '"+ LexicalConstants.LEFT_SQUARE_BRACKET +"'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	public static char getMatchingLeftBracket(int rightBracket) {
		switch (rightBracket) {
		case LexicalConstants.RIGHT_ANGLE_BRACKET:
			return LexicalConstants.LEFT_ANGLE_BRACKET;
		case LexicalConstants.RIGHT_SQUARE_BRACKET:
			return LexicalConstants.LEFT_SQUARE_BRACKET;
		default:
			throw new IllegalArgumentException("getMatchingEndCharacter() supported only for startCharacter '"+ LexicalConstants.LEFT_ANGLE_BRACKET +"' or '"+ LexicalConstants.LEFT_SQUARE_BRACKET +"'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	public static boolean isWhitespace(char c) {
		return (c == LexicalConstants.SPACE || c == LexicalConstants.TAB || c == LexicalConstants.LF || c == LexicalConstants.CR);
	}

	public static boolean isClosingDirectiveBracket(char c) {
		return c  == LexicalConstants.RIGHT_SQUARE_BRACKET || c == LexicalConstants.RIGHT_ANGLE_BRACKET;
	}

	/** The Freemarker document to parse. */
	private final CharSequence input;

	/** The length of {@link #input}. */
	private final int inputLength;

	/** See {@link #getLastToken()}. */
	private int lastTokenStart;

	/** The current position in {@link #input}. */
	private int offset;

	public ParserUtils(CharSequence input) {
		this(input, 0);
	}

	public ParserUtils(CharSequence input, int offset) {
		super();
		this.input = input;
		this.inputLength = input.length();
		this.offset = offset;
	}

	/**
	 * Throws a {@link ParseException} if {@code offset >= inputLength}
	 *
	 * @param expected
	 * @throws ParseException
	 */
	private void checkEndOfInput(String expected) throws ParseException {
		if (offset >= inputLength) {
			throw new ParseException(
					"Unexpected end of input. " + expected + " expected.", offset); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Consumes an FTL indentifier as defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 *   < ID: <LETTER> (<LETTER>|<DIGIT>)* >
	 * </pre>
	 *
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 */
	public int consumeIdentifier() throws ParseException {
		checkEndOfInput("identifier"); //$NON-NLS-1$
		this.lastTokenStart = offset;
		if (!isLetter(input.charAt(offset))) {
			throw new ParseException(
					"Letter expected at position " + offset, offset); //$NON-NLS-1$
		}
		offset++;
		while (offset < inputLength) {
			char ch = input.charAt(offset);
			if (!isLetter(ch) && !isDigit(ch)) {
				break;
			}
			offset++;
		}
		return offset;
	}

	/**
	 * Consumes an FTL string literal as defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 * <STRING_LITERAL :
	 *       ("\""
	 *         ((~["\"","\\"]) | <ESCAPED_CHAR>)*
	 *       "\"")
	 *       |
	 *       ("'"
	 *         ((~["'","\\"]) | <ESCAPED_CHAR>)*
	 *       "'"
	 *       )
	 * >
	 * </pre>
	 *
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 */
	public int consumeStringLiteral() throws ParseException {
		checkEndOfInput("string literal"); //$NON-NLS-1$
		this.lastTokenStart = offset;
		char quot = input.charAt(offset);
		switch (quot) {
		case LexicalConstants.APOS:
		case LexicalConstants.QUOT:
			offset++;
			while (true) {
				checkEndOfInput(String.valueOf(quot));
				char ch = input.charAt(offset);
				if (ch == quot) {
					offset++;
					break;
				} else if (ch != LexicalConstants.BACKSLASH) {
					offset++;
				} else {
					consumeEscapedChar();
				}
			}
			break;
		default:
			throw new ParseException(
					"Quotation mark or apostroph expected at position "+ offset +".", offset); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return offset;
	}

	/**
	 * Consumes an FTL raw string as defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 *   <RAW_STRING : "r" (("\"" (~["\""])* "\"") | ("'" (~["'"])* "'"))>
	 * </pre>
	 *
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 */
	public int consumeRawString() throws ParseException {
		checkEndOfInput("raw string"); //$NON-NLS-1$
		this.lastTokenStart = offset;
		char r = input.charAt(offset);
		switch (r) {
		case LexicalConstants.r:
			offset++;
			checkEndOfInput("' or \""); //$NON-NLS-1$
			char quot = input.charAt(offset);
			switch (quot) {
			case LexicalConstants.APOS:
			case LexicalConstants.QUOT:
				offset++;
				while (true) {
					checkEndOfInput(String.valueOf(quot));
					char ch = input.charAt(offset);
					if (ch == quot) {
						offset++;
						break;
					} else {
						offset++;
					}
				}
				break;
			default:
				throw new ParseException(
						"Quotation mark or apostroph expected.", offset); //$NON-NLS-1$
			}
			break;
		default:
			throw new ParseException("Character 'r' expected.", offset); //$NON-NLS-1$
		}

		return offset;
	}

	/**
	 * Consumes a variable name, e.g. in {@code assign} directive.
	 *
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 */
	public int consumeIdentifierOrStringLiteral() throws ParseException {
		checkEndOfInput("identifier or string literal"); //$NON-NLS-1$
		this.lastTokenStart = offset;

		switch (input.charAt(offset)) {
		case LexicalConstants.QUOT:
		case LexicalConstants.APOS:
			return consumeStringLiteral();
		case LexicalConstants.r:
			if (offset + 1 < inputLength) {
				switch (input.charAt(offset)) {
				case LexicalConstants.QUOT:
				case LexicalConstants.APOS:
					return consumeRawString();
				default:
					/* nothing to do */
					break;
				}
			}
		default:
			return consumeIdentifier();
		}
	}

	/**
	 * Consumes an FTL string literal as defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 * <#ESCAPED_CHAR :  "\\"
	 *            (
	 *              ["n","t","r","f","b","g","l","a","\\","'","\"","$","{"]
	 *              |
	 *              ("x" ["0"-"9","A"-"F","a"-"f"])
	 *            )
	 * >
	 * </pre>
	 *
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 */
	public int consumeEscapedChar() throws ParseException {
		checkEndOfInput("escaped character"); //$NON-NLS-1$
		switch (input.charAt(offset)) {
		case LexicalConstants.BACKSLASH:
			offset++;
			checkEndOfInput("escaped character"); //$NON-NLS-1$
			switch (input.charAt(offset)) {
			case LexicalConstants.n:
			case LexicalConstants.t:
			case LexicalConstants.r:
			case LexicalConstants.f:
			case LexicalConstants.b:
			case LexicalConstants.g:
			case LexicalConstants.l:
			case LexicalConstants.a:
			case LexicalConstants.BACKSLASH:
			case LexicalConstants.APOS:
			case LexicalConstants.QUOT:
			case LexicalConstants.DOLLAR:
			case LexicalConstants.LEFT_BRACE:
				offset++;
				break;
			case LexicalConstants.x:
				offset++;
				checkEndOfInput("escaped character"); //$NON-NLS-1$
				char ch = input.charAt(offset);
				if (isBetween(ch, LexicalConstants.DIGIT_0, LexicalConstants.DIGIT_9)
						|| isBetween(ch, LexicalConstants.DIGIT_a, LexicalConstants.DIGIT_f)
						|| isBetween(ch, LexicalConstants.DIGIT_A, LexicalConstants.DIGIT_F)) {
					offset++;
				}
				else {
					throw new ParseException(
							"0-9 or a-f or A-F expected at position " + offset + ".", offset); //$NON-NLS-1$ //$NON-NLS-2$
				}
				break;
			default:
				throw new ParseException(
						"A continuation of an escape sequence expected at position " + offset + ".", offset); //$NON-NLS-1$ //$NON-NLS-2$
			}

			break;
		default:
			throw new ParseException(
					"Backslash expected at position " + offset + ".", offset); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return offset;
	}

	/**
	 * Consumes an optional whitespace as defined by <a href=
	 * "https://github.com/freemarker/freemarker/blob/2.3-gae/src/main/javacc/FTL.jj"
	 * >FTL.jj</a>.
	 *
	 * <pre>
	 * <WHITESPACE : (["\n", "\r", "\t", " "])+>
	 * </pre>
	 *
	 * @return the position in {@link #input} next after the consumed token
	 */
	public int consumeWhiteSpace() {
		this.lastTokenStart = offset;
		while (offset < inputLength) {
			switch (input.charAt(offset)) {
			case LexicalConstants.SPACE:
			case LexicalConstants.TAB:
			case LexicalConstants.CR:
			case LexicalConstants.LF:
				offset++;
				break;
			default:
				return offset;
			}
		}
		return offset;
	}

	/**
	 * Returns the token consumed by a previously called {@code consume*()}
	 * method.
	 *
	 * @return see above
	 * @throws IllegalStateException
	 *             if none of the {@code consume*()} methods was called before.
	 */
	public CharSequence getLastToken() {
		if (lastTokenStart < 0) {
			throw new IllegalStateException();
		}
		return input.subSequence(lastTokenStart, offset);
	}

	/**
	 * A shorthand for {@code offset >= inputLength}.
	 *
	 * @return
	 */
	public boolean isAtEndOfInput() {
		return offset >= inputLength;
	}

	/**
	 * Attempts to match the given {@code token} at the current possition of
	 * {@link #input}. Throws a {@link ParseException} if the given token does
	 * not match.
	 *
	 * @param token
	 *            the token to match
	 * @return the position in {@link #input} next after the consumed token
	 * @throws ParseException
	 *             if the given token did not match
	 */
	public int match(String token) throws ParseException {
		this.lastTokenStart = offset;
		for (int i = 0; i < token.length(); i++) {
			checkEndOfInput(token);
			char ch = input.charAt(offset);
			if (ch != token.charAt(i)) {
				throw new ParseException(
						"Could not match token '" + token + "' at position " + offset + ".", offset); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			offset++;
		}
		return offset;
	}

	/**
	 * A shorthand for {@code input.charAt(offset) == ch}.
	 * <p>
	 * When called with
	 * {@code checkEndOfInput == true}, then {@link #checkEndOfInput(String)} is
	 * called first and eventually a {@link ParseException} is thrown if we are
	 * at end of input.
	 * <p>
	 * Should be called {@code checkEndOfInput == false}, only
	 * if you are sure that {@link #offset} is not at end of input, e.g. by
	 * calling {@link #isAtEndOfInput()} before {@link #matches(char, boolean)}.
	 *
	 * @param ch
	 * @param checkEndOfInput
	 *            see above
	 * @throws ParseException
	 */
	public boolean matches(char ch, boolean checkEndOfInput)
			throws ParseException {
		if (checkEndOfInput) {
			checkEndOfInput(String.valueOf(ch));
		}
		return input.charAt(offset) == ch;
	}

}
