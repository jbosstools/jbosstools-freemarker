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
package org.jboss.ide.eclipse.freemarker.lang.test;

import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils.ParseException;

import junit.framework.TestCase;

public class ParserUtilsTest extends TestCase {

	public void testIsBetween() {
		assertEquals(true, ParserUtils.isBetween('0', '0', '9'));
		assertEquals(true, ParserUtils.isBetween('9', '0', '9'));
		assertEquals(true, ParserUtils.isBetween('5', '0', '9'));
		assertEquals(false, ParserUtils.isBetween('/', '0', '9'));
		assertEquals(false, ParserUtils.isBetween(':', '0', '9'));
	}

	public void testIsDigit() {
		assertEquals(true, ParserUtils.isDigit('0'));
		assertEquals(true, ParserUtils.isDigit('9'));
		assertEquals(false, ParserUtils.isDigit('a'));
	}

	public void testIsLetter() {
		assertEquals(true, ParserUtils.isLetter('a'));
		assertEquals(true, ParserUtils.isLetter('A'));
		assertEquals(false, ParserUtils.isLetter('9'));
	}

	public void testConsumeIdentifier() throws ParseException {
		ParserUtils parser = new ParserUtils("var='val'"); //$NON-NLS-1$
		assertEquals(3, parser.consumeIdentifier());
		assertEquals("var", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("var ='val'"); //$NON-NLS-1$
		assertEquals(3, parser.consumeIdentifier());
		assertEquals("var", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("v ='val'"); //$NON-NLS-1$
		assertEquals(1, parser.consumeIdentifier());
		assertEquals("v", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("v0 ='val'"); //$NON-NLS-1$
		assertEquals(2, parser.consumeIdentifier());
		assertEquals("v0", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("v0"); //$NON-NLS-1$
		assertEquals(2, parser.consumeIdentifier());
		assertEquals("v0", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.consumeIdentifier();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("0 ='val'"); //$NON-NLS-1$
		try {
			parser.consumeIdentifier();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils(" var ='val'"); //$NON-NLS-1$
		try {
			parser.consumeIdentifier();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}
	}

	public void testConsumeStringLiteral() throws ParseException {
		ParserUtils parser = new ParserUtils("'val'"); //$NON-NLS-1$
		assertEquals(5, parser.consumeStringLiteral());
		assertEquals("'val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("'val' "); //$NON-NLS-1$
		assertEquals(5, parser.consumeStringLiteral());
		assertEquals("'val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("'val'>"); //$NON-NLS-1$
		assertEquals(5, parser.consumeStringLiteral());
		assertEquals("'val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("''"); //$NON-NLS-1$
		assertEquals(2, parser.consumeStringLiteral());
		assertEquals("''", parser.getLastToken()); //$NON-NLS-1$

		/* newline and other chars */
		parser = new ParserUtils("'\n\t\r'"); //$NON-NLS-1$
		assertEquals(5, parser.consumeStringLiteral());
		assertEquals("'\n\t\r'", parser.getLastToken()); //$NON-NLS-1$

		/* quot in apos */
		parser = new ParserUtils("'\"'"); //$NON-NLS-1$
		assertEquals(3, parser.consumeStringLiteral());
		assertEquals("'\"'", parser.getLastToken()); //$NON-NLS-1$

		/* apos in apos */
		parser = new ParserUtils("'\\''"); //$NON-NLS-1$
		assertEquals(4, parser.consumeStringLiteral());
		assertEquals("'\\''", parser.getLastToken()); //$NON-NLS-1$

		/* simple escapes */
		parser = new ParserUtils("'\\n\\t\\r\\\\'"); //$NON-NLS-1$
		assertEquals(10, parser.consumeStringLiteral());
		assertEquals("'\\n\\t\\r\\\\'", parser.getLastToken()); //$NON-NLS-1$

		/* hex escapes */
		parser = new ParserUtils("'\\x00ab\\xab\\xff\\xAB\\xFF'"); //$NON-NLS-1$
		assertEquals(24, parser.consumeStringLiteral());
		assertEquals("'\\x00ab\\xab\\xff\\xAB\\xFF'", parser.getLastToken()); //$NON-NLS-1$

		/* with double quotes */
		parser = new ParserUtils("\"val\""); //$NON-NLS-1$
		assertEquals(5, parser.consumeStringLiteral());
		assertEquals("\"val\"", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("\"\""); //$NON-NLS-1$
		assertEquals(2, parser.consumeStringLiteral());
		assertEquals("\"\"", parser.getLastToken()); //$NON-NLS-1$

		/* quot in quot */
		parser = new ParserUtils("\"\\\"\""); //$NON-NLS-1$
		assertEquals(4, parser.consumeStringLiteral());
		assertEquals("\"\\\"\"", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("val"); //$NON-NLS-1$
		try {
			parser.consumeStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.consumeStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		/* unfinished apos */
		parser = new ParserUtils("'"); //$NON-NLS-1$
		try {
			parser.consumeStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		/* unfinished quot */
		parser = new ParserUtils("\""); //$NON-NLS-1$
		try {
			parser.consumeStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}
	}

	public void testConsumeRawString() throws ParseException {
		ParserUtils parser = new ParserUtils("r'val'"); //$NON-NLS-1$
		assertEquals(6, parser.consumeRawString());
		assertEquals("r'val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("r''"); //$NON-NLS-1$
		assertEquals(3, parser.consumeRawString());
		assertEquals("r''", parser.getLastToken()); //$NON-NLS-1$

		/* escaping apos does not work in the same way as in string literal */
		parser = new ParserUtils("r' \\' '"); //$NON-NLS-1$
		assertEquals(5, parser.consumeRawString());
		assertEquals("r' \\'", parser.getLastToken()); //$NON-NLS-1$

		/* escaping quot does not work in the same way as in string literal */
		parser = new ParserUtils("r\" \\\" \""); //$NON-NLS-1$
		assertEquals(5, parser.consumeRawString());
		assertEquals("r\" \\\"", parser.getLastToken()); //$NON-NLS-1$


		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.consumeRawString();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("r"); //$NON-NLS-1$
		try {
			parser.consumeRawString();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		/* unfinished apos */
		parser = new ParserUtils("r'"); //$NON-NLS-1$
		try {
			parser.consumeRawString();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		/* unfinished quot */
		parser = new ParserUtils("r\""); //$NON-NLS-1$
		try {
			parser.consumeRawString();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}
	}

	public void testConsumeIdentifierOrStringLiteral() throws ParseException {
		ParserUtils parser = new ParserUtils("'val'"); //$NON-NLS-1$
		assertEquals(5, parser.consumeIdentifierOrStringLiteral());
		assertEquals("'val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("'val val'"); //$NON-NLS-1$
		assertEquals(9, parser.consumeIdentifierOrStringLiteral());
		assertEquals("'val val'", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("''"); //$NON-NLS-1$
		assertEquals(2, parser.consumeIdentifierOrStringLiteral());
		assertEquals("''", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("var"); //$NON-NLS-1$
		assertEquals(3, parser.consumeIdentifierOrStringLiteral());
		assertEquals("var", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.consumeIdentifierOrStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("123"); //$NON-NLS-1$
		try {
			parser.consumeIdentifierOrStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils(" "); //$NON-NLS-1$
		try {
			parser.consumeIdentifierOrStringLiteral();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

	}

	public void testConsumeEscapedChar() throws ParseException {
		ParserUtils parser = new ParserUtils("\\n"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\r"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\t"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\r"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\f"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\b"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\l"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\a"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\\\"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\'"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\\""); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\$"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\{"); //$NON-NLS-1$
		assertEquals(2, parser.consumeEscapedChar());

		parser = new ParserUtils("\\x0"); //$NON-NLS-1$
		assertEquals(3, parser.consumeEscapedChar());

		parser = new ParserUtils("\\xa"); //$NON-NLS-1$
		assertEquals(3, parser.consumeEscapedChar());

		parser = new ParserUtils("\\xA"); //$NON-NLS-1$
		assertEquals(3, parser.consumeEscapedChar());

		parser = new ParserUtils("\\xf"); //$NON-NLS-1$
		assertEquals(3, parser.consumeEscapedChar());

		parser = new ParserUtils("\\xF"); //$NON-NLS-1$
		assertEquals(3, parser.consumeEscapedChar());

		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("123"); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils(" "); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("\\"); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("\\ "); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("\\x"); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

		parser = new ParserUtils("\\x "); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

	}

	public void testConsumeWhiteSpace() {
		ParserUtils parser = new ParserUtils(" \t\n\r"); //$NON-NLS-1$
		assertEquals(4, parser.consumeWhiteSpace());
		assertEquals(" \t\n\r", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils(""); //$NON-NLS-1$
		assertEquals(0, parser.consumeWhiteSpace());
		assertEquals("", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("x"); //$NON-NLS-1$
		assertEquals(0, parser.consumeWhiteSpace());
		assertEquals("", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils(" x"); //$NON-NLS-1$
		assertEquals(1, parser.consumeWhiteSpace());
		assertEquals(" ", parser.getLastToken()); //$NON-NLS-1$

	}

	public void testIsAtEndOfInput() {
		ParserUtils parser = new ParserUtils(" \t\n\r"); //$NON-NLS-1$
		assertEquals(false, parser.isAtEndOfInput());

		parser = new ParserUtils("x"); //$NON-NLS-1$
		assertEquals(false, parser.isAtEndOfInput());

		parser = new ParserUtils(""); //$NON-NLS-1$
		assertEquals(true, parser.isAtEndOfInput());
	}

	public void testMatch() throws ParseException {
		ParserUtils parser = new ParserUtils("assign"); //$NON-NLS-1$
		assertEquals(6, parser.match("assign")); //$NON-NLS-1$
		assertEquals("assign", parser.getLastToken()); //$NON-NLS-1$

		parser = new ParserUtils("\\x "); //$NON-NLS-1$
		try {
			parser.consumeEscapedChar();
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}
	}

	public void testMatches() throws ParseException {
		ParserUtils parser = new ParserUtils("assign"); //$NON-NLS-1$
		assertEquals(true, parser.matches('a', true));

		parser = new ParserUtils("assign"); //$NON-NLS-1$
		assertEquals(false, parser.matches('b', true));

		parser = new ParserUtils(""); //$NON-NLS-1$
		try {
			parser.matches('a', true);
			fail("ParseException expected"); //$NON-NLS-1$
		} catch (ParseException expected) {
		}

	}


}
