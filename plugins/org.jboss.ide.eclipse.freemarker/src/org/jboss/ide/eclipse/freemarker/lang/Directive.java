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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.Token;

/**
 * FTL directive tags.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public enum Directive {

	__ftl_include(Keyword.include),
	__ftl_import(Keyword.import_),
	__ftl_assign(Keyword.assign),
	__ftl_assign_end(Keyword.assign),
	__ftl_local(Keyword.local),
	__ftl_local_end(Keyword.local),
	__ftl_global(Keyword.global),
	__ftl_global_end(Keyword.global),
	__ftl_break(Keyword.break_),
	__ftl_nested(Keyword.nested),
	__ftl_return(Keyword.return_),
	__ftl_stop(Keyword.stop),
	__ftl_list_directive_start(Keyword.list),
	__ftl_list_directive_end(Keyword.list),
	__ftl_if_directive_start(Keyword.if_),
	__ftl_else_if_directive(Keyword.else_if),
	__ftl_if_else_directive(Keyword.else_),
	__ftl_if_directive_end(Keyword.if_),
	__ftl_switch_directive_start(Keyword.switch_),
	__ftl_switch_directive_end(Keyword.switch_),
	__ftl_case_directive_start(Keyword.case_),
	__ftl_case_default_start(Keyword.default_),
	__ftl_macro_directive_start(Keyword.macro),
	__ftl_macro_directive_end(Keyword.macro),
	__ftl_macro_instance_start(Keyword.macro_instance),
	__ftl_macro_instance_end(Keyword.macro_instance),
	__ftl_ftl_directive(Keyword.ftl),
	__ftl_function_directive_start(Keyword.function_),
	__ftl_function_directive_end(Keyword.function_),

	__ftl_directive(Keyword.directive),
	__ftl_directive_end(Keyword.directive),
	__ftl_interpolation(Keyword.interpolation)
	;

	private static final Map<String, Directive> FAST_LOOKUP;
	private final Keyword keyword;

	private Directive(Keyword keyword) {
		this.keyword = keyword;
	}

	static {

		Map<String, Directive> fastLookUp = new HashMap<String, Directive>(64);
		fastLookUp.put(__ftl_include.name(), __ftl_include);
		fastLookUp.put(__ftl_import.name(), __ftl_import);
		fastLookUp.put(__ftl_assign.name(), __ftl_assign);
		fastLookUp.put(__ftl_assign_end.name(), __ftl_assign_end);
		fastLookUp.put(__ftl_local.name(), __ftl_local);
		fastLookUp.put(__ftl_local_end.name(), __ftl_local_end);
		fastLookUp.put(__ftl_global.name(), __ftl_global);
		fastLookUp.put(__ftl_global_end.name(), __ftl_global_end);
		fastLookUp.put(__ftl_break.name(), __ftl_break);
		fastLookUp.put(__ftl_nested.name(), __ftl_nested);
		fastLookUp.put(__ftl_return.name(), __ftl_return);
		fastLookUp.put(__ftl_stop.name(), __ftl_stop);
		fastLookUp.put(__ftl_list_directive_start.name(), __ftl_list_directive_start);
		fastLookUp.put(__ftl_list_directive_end.name(), __ftl_list_directive_end);
		fastLookUp.put(__ftl_if_directive_start.name(), __ftl_if_directive_start);
		fastLookUp.put(__ftl_else_if_directive.name(), __ftl_else_if_directive);
		fastLookUp.put(__ftl_if_else_directive.name(), __ftl_if_else_directive);
		fastLookUp.put(__ftl_if_directive_end.name(), __ftl_if_directive_end);
		fastLookUp.put(__ftl_switch_directive_start.name(), __ftl_switch_directive_start);
		fastLookUp.put(__ftl_switch_directive_end.name(), __ftl_switch_directive_end);
		fastLookUp.put(__ftl_case_directive_start.name(), __ftl_case_directive_start);
		fastLookUp.put(__ftl_case_default_start.name(), __ftl_case_default_start);
		fastLookUp.put(__ftl_macro_directive_start.name(), __ftl_macro_directive_start);
		fastLookUp.put(__ftl_macro_directive_end.name(), __ftl_macro_directive_end);
		fastLookUp.put(__ftl_macro_instance_start.name(), __ftl_macro_instance_start);
		fastLookUp.put(__ftl_macro_instance_end.name(), __ftl_macro_instance_end);
		fastLookUp.put(__ftl_ftl_directive.name(), __ftl_ftl_directive);
		fastLookUp.put(__ftl_function_directive_start.name(), __ftl_function_directive_start);
		fastLookUp.put(__ftl_function_directive_end.name(), __ftl_function_directive_end);

		fastLookUp.put(__ftl_directive.name(), __ftl_directive);
		fastLookUp.put(__ftl_directive_end.name(), __ftl_directive_end);
		fastLookUp.put(__ftl_interpolation.name(), __ftl_interpolation);

		FAST_LOOKUP = Collections.unmodifiableMap(fastLookUp);
	}

	public Token newToken() {
		return new Token(this.name());
	}

	public static Directive fastValueOf(String str) {
		return FAST_LOOKUP.get(str);
	}

	public Keyword getKeyword() {
		return keyword;
	}
}
