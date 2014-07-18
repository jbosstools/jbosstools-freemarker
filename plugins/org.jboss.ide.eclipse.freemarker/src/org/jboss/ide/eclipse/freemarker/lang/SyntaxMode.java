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
 * An enumeration containing the two Freemarker syntax modes, i.e. the angle
 * bracket {@code <} one and the square bracket {@code [} one.
 * See <a href="http://freemarker.org/docs/dgui_misc_alternativesyntax.html">Alternative
 * (square bracket) syntax</a> of the FreeMarker Template Author's Guide.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public enum SyntaxMode {
	SQUARE(LexicalConstants.LEFT_SQUARE_BRACKET,
			LexicalConstants.RIGHT_SQUARE_BRACKET), ANGLE(
			LexicalConstants.LEFT_ANGLE_BRACKET,
			LexicalConstants.RIGHT_ANGLE_BRACKET);

	private final char start;
	private final char end;
	private final String commentStart;
	private final String commentEnd;
	private final String directiveStart;
	private final String directiveEnd;
	private final String macroInstanceStart;
	private final String macroInstanceEnd;
	private final String tagEnd;

	private SyntaxMode(char start, char end) {
		this.start = start;
		this.end = end;
		this.commentStart = new StringBuilder(4).append(start)
				.append(LexicalConstants.HASH).append(LexicalConstants.MINUS)
				.append(LexicalConstants.MINUS).toString();
		this.commentEnd = new StringBuilder(3).append(LexicalConstants.MINUS)
				.append(LexicalConstants.MINUS).append(end).toString();
		this.directiveStart = new StringBuilder(2).append(start)
				.append(LexicalConstants.HASH).toString();
		this.directiveEnd = new StringBuilder(3).append(start)
				.append(LexicalConstants.SLASH).append(LexicalConstants.HASH)
				.toString();
		this.macroInstanceStart = new StringBuilder(2).append(start)
				.append(LexicalConstants.AT).toString();
		this.macroInstanceEnd = new StringBuilder(3).append(start)
				.append(LexicalConstants.SLASH).append(LexicalConstants.AT)
				.toString();
		this.tagEnd = String.valueOf(end);
	}

	public char getStart() {
		return start;
	}

	public char getEnd() {
		return end;
	}

	public String getCommentStart() {
		return commentStart;
	}

	public String getCommentEnd() {
		return commentEnd;
	}

	public String getDirectiveStart() {
		return directiveStart;
	}

	public String getDirectiveEnd() {
		return directiveEnd;
	}

	public String getMacroInstanceStart() {
		return macroInstanceStart;
	}

	public String getMacroInstanceEnd() {
		return macroInstanceEnd;
	}

	public String getTagEnd() {
		return tagEnd;
	}

	public static SyntaxMode getDefault() {
		return ANGLE;
	}

}
