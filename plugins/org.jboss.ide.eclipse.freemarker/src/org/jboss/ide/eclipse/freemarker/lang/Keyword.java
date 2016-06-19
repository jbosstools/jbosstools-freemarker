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
 * FTL keywords.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public enum Keyword {
	INCLUDE,
	IMPORT,
	ASSIGN,
	LOCAL,
	GLOBAL,
	BREAK,
	NESTED,
	RETURN,
	STOP,
	LIST,
	IF,
	ELSE,
	ELSEIF,
	SWITCH,
	CASE,
	DEFAULT,
	MACRO,
	FTL,
	FUNCTION,
	TRUE,
	FALSE,
	GT,
	GTE,
	LT,
	LTE,
	AS,
	IN,
	USING,
	FOREACH,
	ATTEMPT,
	RECOVER,
	VISIT,
	RECURSE,
	FALLBACK,
	AUTOESC,
    NOAUTOESC,
	ESCAPE,
	NOESCAPE,
	NOPARSE,
	ITEMS,
	SEP,
    FLUSH,
	T,
	LT_DIRECTIVE("lt"), //$NON-NLS-1$
	RT,
	UNKNOWN(""); //$NON-NLS-1$

	private final String keyword;

	private Keyword(String keyword) {
		this.keyword = keyword;
	}
	
	private Keyword() {
		this.keyword = this.name().toLowerCase();
	}

	@Override
	public String toString() {
		return keyword;
	}
	
	public static Keyword keywordValueOf(String string) {
		return Enum.valueOf(Keyword.class, string.toUpperCase());
	}
}
