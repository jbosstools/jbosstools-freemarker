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
package org.jboss.ide.eclipse.freemarker.editor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.GenericDirectiveRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.GenericDirectiveRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.InterpolationRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.MacroInstanceRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.MacroInstanceRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.XmlRule;
import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PartitionScanner extends RuleBasedPartitionScanner {

	public final static String FTL_COMMENT = "__ftl_comment"; //$NON-NLS-1$
	public final static String XML_TAG = "__xml_tag"; //$NON-NLS-1$
	public final static String XML_COMMENT = "__xml_comment"; //$NON-NLS-1$
	public final static String STRING = "__string"; //$NON-NLS-1$


	public static final int INVALID_DIRECTIVE_ID = -1;

	/**
	 * The array of partitions used.
	 */
	public static final String[] PARTITIONS;

	static {

		String[] initialPartitions = {
				IDocument.DEFAULT_CONTENT_TYPE,
				FTL_COMMENT,
				XML_TAG,
				XML_COMMENT,
				STRING
			};


		Directive[] directives = Directive.values();
		String[] pSub = new String[initialPartitions.length + directives.length];
		int i = 0;
		for (Directive directive : directives) {
			pSub[i++] = directive.name();
		}
		for (int j=0; j<initialPartitions.length; j++) {
			pSub[i++] = initialPartitions[j];
		}
		PARTITIONS = pSub;
	}

	/**
	 * Creates a new partition scanner.
	 */
	public PartitionScanner() {
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		IToken ftlComment = new Token(FTL_COMMENT);

		rules.add(new MultiLineRule("<!--", "-->", new Token(XML_COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("<#--", "-->", ftlComment)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("[#--", "--]", ftlComment)); //$NON-NLS-1$ //$NON-NLS-2$

		rules.add(new DirectiveRule(Directive.__ftl_ftl_directive));
		rules.add(new DirectiveRule(Directive.__ftl_if_directive_start));
		rules.add(new DirectiveRule(Directive.__ftl_else_if_directive));
		rules.add(new DirectiveRule(Directive.__ftl_if_else_directive, true));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_if_directive_end));

		rules.add(new DirectiveRule(Directive.__ftl_function_directive_start));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_function_directive_end));

		rules.add(new DirectiveRule(Directive.__ftl_list_directive_start));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_list_directive_end));

		rules.add(new DirectiveRule(Directive.__ftl_macro_directive_start));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_macro_directive_end));
		rules.add(new MacroInstanceRule(Directive.__ftl_macro_instance_start));
		rules.add(new MacroInstanceRuleEnd(Directive.__ftl_macro_instance_end));

		rules.add(new DirectiveRule(Directive.__ftl_switch_directive_start));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_switch_directive_end));
		rules.add(new DirectiveRule(Directive.__ftl_case_directive_start));
		rules.add(new DirectiveRule(Directive.__ftl_case_default_start));

		rules.add(new DirectiveRule(Directive.__ftl_assign));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_assign_end));
		rules.add(new DirectiveRule(Directive.__ftl_local));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_local_end));
		rules.add(new DirectiveRule(Directive.__ftl_global));
		rules.add(new DirectiveRuleEnd(Directive.__ftl_global_end));

		rules.add(new DirectiveRule(Directive.__ftl_include));
		rules.add(new DirectiveRule(Directive.__ftl_import));
		rules.add(new DirectiveRule(Directive.__ftl_break));
		rules.add(new DirectiveRule(Directive.__ftl_stop));
		rules.add(new DirectiveRule(Directive.__ftl_nested));
		rules.add(new DirectiveRule(Directive.__ftl_return));

		rules.add(new GenericDirectiveRule(new Token(Directive.__ftl_directive.name())));
		rules.add(new GenericDirectiveRuleEnd(new Token(Directive.__ftl_directive_end.name())));

		rules.add(new InterpolationRule(LexicalConstants.DOLLAR, new Token(Directive.__ftl_interpolation.name())));
		rules.add(new InterpolationRule(LexicalConstants.HASH, new Token(Directive.__ftl_interpolation.name())));

		rules.add(new XmlRule(new Token(XML_TAG)));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
}