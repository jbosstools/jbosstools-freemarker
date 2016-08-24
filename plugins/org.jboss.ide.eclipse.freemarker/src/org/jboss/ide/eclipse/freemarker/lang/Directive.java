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
import org.jboss.ide.eclipse.freemarker.model.AttemptDirective;
import org.jboss.ide.eclipse.freemarker.model.AttemptEndDirective;
import org.jboss.ide.eclipse.freemarker.model.CaseDefaultDirective;
import org.jboss.ide.eclipse.freemarker.model.CaseDirective;
import org.jboss.ide.eclipse.freemarker.model.ElseDirective;
import org.jboss.ide.eclipse.freemarker.model.ElseIfDirective;
import org.jboss.ide.eclipse.freemarker.model.FtlDirective;
import org.jboss.ide.eclipse.freemarker.model.FunctionDirective;
import org.jboss.ide.eclipse.freemarker.model.FunctionEndDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericNestableDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericNestableEndDirective;
import org.jboss.ide.eclipse.freemarker.model.IfDirective;
import org.jboss.ide.eclipse.freemarker.model.IfEndDirective;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.model.ItemsDirective;
import org.jboss.ide.eclipse.freemarker.model.ItemsEndDirective;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;
import org.jboss.ide.eclipse.freemarker.model.ListEndDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroEndDirective;
import org.jboss.ide.eclipse.freemarker.model.RecoverDirective;
import org.jboss.ide.eclipse.freemarker.model.SepDirective;
import org.jboss.ide.eclipse.freemarker.model.SepEndDirective;

/**
 * FTL directive tags.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 *
 */
public enum Directive {

	INCLUDE(Keyword.INCLUDE, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "include.png"); //$NON-NLS-1$;
		}
	},
	IMPORT(Keyword.IMPORT, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "import.png"); //$NON-NLS-1$;
		}
	},
	ASSIGN(Keyword.ASSIGN, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	ASSIGN_END(Keyword.ASSIGN, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	LOCAL(Keyword.LOCAL, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	LOCAL_END(Keyword.LOCAL, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	GLOBAL(Keyword.GLOBAL, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentDirective(itemSet, this);
		}
	},
	GLOBAL_END(Keyword.GLOBAL, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new AssignmentEndDirective(itemSet, this.name());
		}
	},
	BREAK(Keyword.BREAK, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "break.png"); //$NON-NLS-1$;
		}
	},
	NESTED(Keyword.NESTED, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "nested.png"); //$NON-NLS-1$;
		}
	},
	RETURN(Keyword.RETURN, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "return.png"); //$NON-NLS-1$;
		}
	},
	STOP(Keyword.STOP, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericDirective(itemSet, "stop.png"); //$NON-NLS-1$;
		}
	},
	LIST(Keyword.LIST, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ListDirective(itemSet);
		}
	},
	LIST_END(Keyword.LIST, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ListEndDirective(itemSet);
		}
	},
	IF(Keyword.IF, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new IfDirective(itemSet);
		}
	},
	ELSEIF(Keyword.ELSEIF, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ElseIfDirective(itemSet);
		}
	},
	ELSE(Keyword.ELSE, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new ElseDirective(itemSet);
		}
	},
	IF_END(Keyword.IF, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new IfEndDirective(itemSet);
		}
	},
	SWITCH(Keyword.SWITCH, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableDirective(itemSet, getKeyword().toString());
		}
	},
	SWITCH_END(Keyword.SWITCH, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableEndDirective(itemSet, getKeyword().toString());
		}
	},
	CASE(Keyword.CASE, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new CaseDirective(itemSet);
		}
	},
	DEFAULT(Keyword.DEFAULT, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new CaseDefaultDirective(itemSet);
		}
	},
	MACRO(Keyword.MACRO, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new MacroDirective(itemSet);
		}
	},
	MACRO_END(Keyword.MACRO, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new MacroEndDirective(itemSet);
		}
	},
	FTL(Keyword.FTL, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FtlDirective(itemSet);
		}
	},
	FUNCTION(Keyword.FUNCTION, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FunctionDirective(itemSet);
		}
	},
	FUNCTION_END(Keyword.FUNCTION, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new FunctionEndDirective(itemSet);
		}
	},
	FOREACH(Keyword.FOREACH, false) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableDirective(itemSet, getKeyword().toString(), "foreach.png"); //$NON-NLS-1$;
		}
	},
	FOREACH_END(Keyword.FOREACH, true) {
		@Override
		public Item createModelItem(ItemSet itemSet) {
			return new GenericNestableEndDirective(itemSet, getKeyword().toString());
		}
	},
    ATTEMPT(Keyword.ATTEMPT, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new AttemptDirective(itemSet);
        }
    },
    ATTEMPT_END(Keyword.ATTEMPT, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new AttemptEndDirective(itemSet);
        }
    },
    RECOVER(Keyword.RECOVER, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new RecoverDirective(itemSet);
        }
    },
    VISIT(Keyword.VISIT, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    RECURSE(Keyword.RECURSE, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    FALLBACK(Keyword.FALLBACK, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    AUTOESC(Keyword.AUTOESC, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    AUTOESC_END(Keyword.AUTOESC, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
    NOAUTOESC(Keyword.NOAUTOESC, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    NOAUTOESC_END(Keyword.NOAUTOESC, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
    ESCAPE(Keyword.ESCAPE, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    ESCAPE_END(Keyword.ESCAPE, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
    NOESCAPE(Keyword.NOESCAPE, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    NOESCAPE_END(Keyword.NOESCAPE, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
    NOPARSE(Keyword.NOPARSE, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    NOPARSE_END(Keyword.NOPARSE, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
    ITEMS(Keyword.ITEMS, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new ItemsDirective(itemSet);
        }
    },
    ITEMS_END(Keyword.ITEMS, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new ItemsEndDirective(itemSet);
        }
    },
    SEP(Keyword.SEP, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new SepDirective(itemSet);
        }
    },
    SEP_END(Keyword.SEP, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new SepEndDirective(itemSet);
        }
    },
    FLUSH(Keyword.FLUSH, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    T(Keyword.T, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    LT(Keyword.LT_DIRECTIVE, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    RT(Keyword.RT, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    NT(Keyword.NT, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    SETTING(Keyword.SETTING, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericDirective(itemSet);
        }
    },
    COMPRESS(Keyword.COMPRESS, false) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableDirective(itemSet, getKeyword().toString());
        }
    },
    COMPRESS_END(Keyword.COMPRESS, true) {
        @Override
        public Item createModelItem(ItemSet itemSet) {
            return new GenericNestableEndDirective(itemSet, getKeyword().toString());
        }
    },
	; //
    
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
	private final boolean endDirective;

	private Directive(Keyword keyword, boolean endTag) {
		this.keyword = keyword;
		this.endDirective = endTag;
	}

	public Keyword getKeyword() {
		return keyword;
	}
	
	public boolean isEndDirective() {
	    return endDirective;
	}

	public abstract Item createModelItem(ItemSet itemSet);

}
