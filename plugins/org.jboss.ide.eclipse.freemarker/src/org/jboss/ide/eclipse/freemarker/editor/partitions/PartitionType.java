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
package org.jboss.ide.eclipse.freemarker.editor.partitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.ContentScanner;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.SingleTokenScanner;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRuleEnd;
import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.target.TargetColoringScanner;

/**
 * The partition types that we want to distinguish in FTL documents. Partitions
 * are disjoint regions of a document that have to be handled separately for the
 * sake of syntax coloring or document model building. See <a
 * href="http://wiki.eclipse.org/FAQ_What_is_a_document_partition%3F">What is a
 * document partition</a> in Eclipse FAQ.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public enum PartitionType {
	COMMENT(PreferenceKey.COLOR_COMMENT) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new CommentPartitionRule();
		}
	},
	DOLLAR_INTERPOLATION(PreferenceKey.COLOR_INTERPOLATION) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new InterpolationRule(LexicalConstants.DOLLAR, new Token(
					this.name()));
		}
	},
	HASH_INTERPOLATION(PreferenceKey.COLOR_INTERPOLATION) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new InterpolationRule(LexicalConstants.HASH, new Token(
					this.name()));
		}
	},
	DIRECTIVE_START(PreferenceKey.COLOR_DIRECTIVE) {
		@Override
		public RuleBasedScanner createItemParser() {
    		List<IRule> rules = new ArrayList<IRule>();
    		for (Directive directive : Directive.values() ) {
    		    if (!directive.isEndDirective()) {
    		        rules.add(new DirectiveRule(directive));
    		    }
    		}

			RuleBasedScanner result = new RuleBasedScanner();
			result.setRules(rules.toArray(new IRule[rules.size()]));
			return result;
		}

		@Override
		public IPredicateRule createPartitioningRule() {
			return new DirectiveStartPartitionRule();
		}

		@Override
		public ITokenScanner createColoringTokenizer(Editor editor) {
			return new ContentScanner(createColoringToken(foregroundPreferenceKey));
		}

	},
	DIRECTIVE_END(PreferenceKey.COLOR_DIRECTIVE) {
		@Override
		public RuleBasedScanner createItemParser() {

			List<IRule> rules = new ArrayList<IRule>();
			for (Directive directive : Directive.values()) {
			    if (directive.isEndDirective()) {
	                rules.add(new DirectiveRuleEnd(directive));
			    }
			}

			RuleBasedScanner result = new RuleBasedScanner();
			result.setRules(rules.toArray(new IRule[rules.size()]));
			return result;
		}

		@Override
		public IPredicateRule createPartitioningRule() {
			return new DirectiveEndPartitionRule();
		}
	},
	MACRO_INSTANCE_START(PreferenceKey.COLOR_DIRECTIVE) {

		@Override
		public IPredicateRule createPartitioningRule() {
			return new MacroInstanceStartPartitionRule();
		}

		@Override
		public ITokenScanner createColoringTokenizer(Editor editor) {
			return new ContentScanner(createColoringToken(foregroundPreferenceKey));
		}
	},
	MACRO_INSTANCE_END(PreferenceKey.COLOR_DIRECTIVE) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new MacroInstanceEndPartitionRule();
		}
	},
	TEXT(PreferenceKey.COLOR_TEXT) {
		@Override
		public IPredicateRule createPartitioningRule() {
			/* there is no explicit rule for FTL text */
			return null;
		}

		@Override
		public ITokenScanner createColoringTokenizer(Editor editor) {
			return new TargetColoringScanner(editor);
		}

	};

	/** Partitions as a static array for convenience. */
	public static final String[] PARTITION_TYPES;

	/** Used in {@link #fastValueOf(String)} */
	private static final Map<String, PartitionType> FAST_LOOKUP;

	static {
		PartitionType[] partitionTypes = PartitionType.values();
		PARTITION_TYPES = new String[partitionTypes.length];
		Map<String, PartitionType> fastLookUp = new HashMap<String, PartitionType>();
		for (int i = 0; i < partitionTypes.length; i++) {
			PartitionType partitionType = partitionTypes[i];
			PARTITION_TYPES[i] = partitionType.name();
			fastLookUp.put(partitionType.name(), partitionType);
		}
		FAST_LOOKUP = Collections.unmodifiableMap(fastLookUp);
	}

	public static IToken createColoringToken(PreferenceKey foregroundPreferenceKey) {
		return new Token(new TextAttribute(Preferences.getInstance().getColor(
				foregroundPreferenceKey)));
	}

	/**
	 * A {@link HashMap}-backed and {@code null}-tolerant alternative of
	 * {@link #valueOf(String)}.
	 *
	 * @param name
	 * @return the {@link PartitionType} that corresponds to the given {@code name}
	 *         or {@code null} of there is no such.
	 */
	public static PartitionType fastValueOf(String name) {
		return FAST_LOOKUP.get(name);
	}

	/** The preference key for syntax coloring. */
	final PreferenceKey foregroundPreferenceKey;

	private PartitionType(PreferenceKey foregroundPreferenceKey) {
		this.foregroundPreferenceKey = foregroundPreferenceKey;
	}

	/**
	 * Creates a new {@link ITokenScanner} that retuns {@link IToken}s with
	 * {@link TextAttribute} set to color defined in {@link Preferences}.
	 *
	 * @return never {@code null}.
	 */
	public ITokenScanner createColoringTokenizer(Editor editor) {
		return new SingleTokenScanner(createColoringToken(foregroundPreferenceKey));
	}

	/**
	 * Creates a new {@link IPredicateRule} that matches the partitions in a
	 * document that correspond to this {@link PartitionType}.
	 *
	 * @return
	 */
	public abstract IPredicateRule createPartitioningRule();

	/**
	 * Creates a new {@link ITokenScanner} that can be used to build an
	 * {@link ItemSet} or {@code null} if the given partition is not supposed to
	 * contain any sub-items.
	 *
	 * @return see above.
	 */
	public ITokenScanner createItemParser() {
		return null;
	}

}