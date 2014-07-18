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

/**
 * Character constants used in FTL parser.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public interface LexicalConstants {
	char SPACE = ' ';
	char TAB = '\t';
	char CR = '\r';
	char LF = '\n';
	char QUOT = '"';
	char APOS = '\'';
	char r = 'r';
	char EQUALS = '=';
	char BACKSLASH = '\\';
	char DIGIT_0 = '0';
	char DIGIT_9 = '9';
	char DIGIT_a = 'a';
	char DIGIT_A = 'A';
	char DIGIT_f = 'f';
	char DIGIT_F = 'F';
	char n = 'n';
	char t = 't';
	char f = 'f';
	char b = 'b';
	char g = 'g';
	char l = 'l';
	char a = 'a';
	char DOLLAR = '$';
	char LEFT_BRACE = '{';
	char RIGHT_BRACE = '}';
	char x = 'x';
	char PERIOD = '.';
	char HASH = '#';
	char LEFT_ANGLE_BRACKET = '<';
	char LEFT_SQUARE_BRACKET = '[';
	char RIGHT_ANGLE_BRACKET = '>';
	char RIGHT_SQUARE_BRACKET = ']';
	char LEFT_PARENTHESIS = '(';
	char RIGHT_PARENTHESIS = ')';
	char AT = '@';
	char SLASH = '/';
	char QUESTION_MARK = '?';
	char EXCLAMATION_MARK = '!';
	char MINUS = '-';
	char COMMA = ',';
	String QUOT_STRING = String.valueOf(QUOT);
	String APOS_STRING = String.valueOf(APOS);
	String SQUARE_SYNTAX_MARKER = new StringBuilder(6).append(LEFT_SQUARE_BRACKET).append(HASH).append(Keyword.ftl).toString();
}
