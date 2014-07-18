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

import org.jboss.ide.eclipse.freemarker.model.AssignmentDirective;
import org.jboss.ide.eclipse.freemarker.model.AssignmentEndDirective;
import org.jboss.ide.eclipse.freemarker.model.CaseDefaultDirective;
import org.jboss.ide.eclipse.freemarker.model.CaseDirective;
import org.jboss.ide.eclipse.freemarker.model.ElseIfDirective;
import org.jboss.ide.eclipse.freemarker.model.FtlDirective;
import org.jboss.ide.eclipse.freemarker.model.FunctionDirective;
import org.jboss.ide.eclipse.freemarker.model.FunctionEndDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericNestableDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericNestableEndDirective;
import org.jboss.ide.eclipse.freemarker.model.IfDirective;
import org.jboss.ide.eclipse.freemarker.model.IfElseDirective;
import org.jboss.ide.eclipse.freemarker.model.IfEndDirective;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;
import org.jboss.ide.eclipse.freemarker.model.ListEndDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroEndDirective;

/**
 * FTL directive tags.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public enum Directive {

	INCLUDE(Keyword.include) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "include.png"); //$NON-NLS-1$;
		}
	},
	IMPORT(Keyword.import_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "import.png"); //$NON-NLS-1$;
		}
	},
	ASSIGN(Keyword.assign) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	ASSIGN_END(Keyword.assign) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	LOCAL(Keyword.local) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	LOCAL_END(Keyword.local) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	GLOBAL(Keyword.global) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	GLOBAL_END(Keyword.global) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	BREAK(Keyword.break_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "break.png"); //$NON-NLS-1$;
		}
	},
	NESTED(Keyword.nested) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "nested.png"); //$NON-NLS-1$;
		}
	},
	RETURN(Keyword.return_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "return.png"); //$NON-NLS-1$;
		}
	},
	STOP(Keyword.stop) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "stop.png"); //$NON-NLS-1$;
		}
	},
	LIST(Keyword.list) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ListDirective(itemSet);
		}
	},
	LIST_END(Keyword.list) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ListEndDirective(itemSet);
		}
	},
	IF(Keyword.if_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new IfDirective(itemSet);
		}
	},
	ELSEIF(Keyword.else_if) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ElseIfDirective(itemSet);
		}
	},
	ELSE(Keyword.else_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new IfElseDirective(itemSet);
		}
	},
	IF_END(Keyword.if_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new IfEndDirective(itemSet);
		}
	},
	SWITCH(Keyword.switch_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableDirective(itemSet, "switch", "switch.png"); //$NON-NLS-1$ //$NON-NLS-2$;
		}
	},
	SWITCH_END(Keyword.switch_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableEndDirective(itemSet, "switch"); //$NON-NLS-1$;
		}
	},
	CASE(Keyword.case_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new CaseDirective(itemSet);
		}
	},
	DEFAULT(Keyword.default_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new CaseDefaultDirective(itemSet);
		}
	},
	MACRO(Keyword.macro) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new MacroDirective(itemSet);
		}
	},
	MACRO_END(Keyword.macro) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new MacroEndDirective(itemSet);
		}
	},
	FTL(Keyword.ftl) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FtlDirective(itemSet);
		}
	},
	FUNCTION(Keyword.function_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FunctionDirective(itemSet);
		}
	},
	FUNCTION_END(Keyword.function_) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FunctionEndDirective(itemSet);
		}
	}
	;

	private static final Map<String, Directive> FAST_LOOKUP;
	static {
		Map<String, Directive> fastLookUp = new HashMap<String, Directive>(64);
		Directive[] directives = values();
		for (Directive directive : directives) {
			fastLookUp.put(directive.name(), directive);
		}
		FAST_LOOKUP = Collections.unmodifiableMap(fastLookUp);
	}
	public static Directive fastValueOf(String str) {
		return FAST_LOOKUP.get(str);
	}

	private final Keyword keyword;

	private Directive(Keyword keyword) {
		this.keyword = keyword;
	}

	public Keyword getKeyword() {
		return keyword;
	}

	public abstract Item createModelItem(ItemSet itemSet);

}
