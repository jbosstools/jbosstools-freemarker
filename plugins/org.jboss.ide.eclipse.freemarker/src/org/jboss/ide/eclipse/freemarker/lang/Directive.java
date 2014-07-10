package org.jboss.ide.eclipse.freemarker.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.Token;

public enum Directive {

	__ftl_include,
	__ftl_import,
	__ftl_assign,
	__ftl_assign_end,
	__ftl_local,
	__ftl_local_end,
	__ftl_global,
	__ftl_global_end,
	__ftl_break,
	__ftl_nested,
	__ftl_return,
	__ftl_stop,
	__ftl_list_directive_start,
	__ftl_list_directive_end,
	__ftl_if_directive_start,
	__ftl_else_if_directive,
	__ftl_if_else_directive,
	__ftl_if_directive_end,
	__ftl_switch_directive_start,
	__ftl_switch_directive_end,
	__ftl_case_directive_start,
	__ftl_case_default_start,
	__ftl_macro_directive_start,
	__ftl_macro_directive_end,
	__ftl_macro_instance_start,
	__ftl_macro_instance_end,
	__ftl_ftl_directive,
	__ftl_function_directive_start,
	__ftl_function_directive_end,

	__ftl_directive,
	__ftl_directive_end,
	__ftl_interpolation
	;

	private static final Map<String, Directive> FAST_LOOKUP;

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
}
