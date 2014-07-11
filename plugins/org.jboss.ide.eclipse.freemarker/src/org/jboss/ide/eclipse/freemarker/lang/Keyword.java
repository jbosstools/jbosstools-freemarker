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
	include("include"), //$NON-NLS-1$
	import_("import"), //$NON-NLS-1$
	assign("assign"), //$NON-NLS-1$
	local("local"), //$NON-NLS-1$
	global("global"), //$NON-NLS-1$
	break_("break"), //$NON-NLS-1$
	nested("nested"), //$NON-NLS-1$
	return_("return"), //$NON-NLS-1$
	stop("stop"), //$NON-NLS-1$
	list("list"), //$NON-NLS-1$
	if_("if"), //$NON-NLS-1$
	else_("else"), //$NON-NLS-1$
	else_if("else if"), //$NON-NLS-1$
	switch_("switch"), //$NON-NLS-1$
	case_("case"), //$NON-NLS-1$
	default_("default"), //$NON-NLS-1$
	macro("macro"), //$NON-NLS-1$
	ftl("ftl"), //$NON-NLS-1$
	function_("function"), //$NON-NLS-1$

	directive("directive"), //$NON-NLS-1$
	interpolation("interpolation") //$NON-NLS-1$
	;

	private final String keyword;

	private Keyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return keyword;
	}
}
