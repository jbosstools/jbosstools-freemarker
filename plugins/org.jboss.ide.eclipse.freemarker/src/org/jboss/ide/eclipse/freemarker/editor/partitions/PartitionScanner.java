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
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PartitionScanner extends RuleBasedPartitionScanner implements SyntaxModeListener {

	private SyntaxMode syntaxMode = SyntaxMode.getDefault();

	/**
	 * Creates a new partition scanner.
	 */
	public PartitionScanner() {

		PartitionType[] partitionTypes = PartitionType.values();
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>(partitionTypes.length);
		for (PartitionType partitionType : partitionTypes) {
			IPredicateRule rule = partitionType.createPartitioningRule();
			if (rule != null) {
				/* this should be the case only for FTL text */
				rules.add(rule);
			}
		}
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));

		/* because there is no explicit rule for FTL text, we will return TEXT
		 * as default from the partition scanner */
		setDefaultReturnToken(new Token(PartitionType.TEXT.name()));

//		IToken ftlComment = new Token(FTL_COMMENT);
//
//		rules.add(new MultiLineRule("<!--", "-->", new Token(XML_COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$
//		rules.add(); //$NON-NLS-1$ //$NON-NLS-2$
//		rules.add(new MultiLineRule("[#--", "--]", ftlComment)); //$NON-NLS-1$ //$NON-NLS-2$
//
//		rules.add(new DirectiveRule(Directive.__ftl_ftl_directive));
//		rules.add(new DirectiveRule(Directive.__ftl_if_directive_start));
//		rules.add(new DirectiveRule(Directive.__ftl_else_if_directive));
//		rules.add(new DirectiveRule(Directive.__ftl_if_else_directive, true));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_if_directive_end));
//
//		rules.add(new DirectiveRule(Directive.__ftl_function_directive_start));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_function_directive_end));
//
//		rules.add(new DirectiveRule(Directive.__ftl_list_directive_start));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_list_directive_end));
//
//		rules.add(new DirectiveRule(Directive.__ftl_macro_directive_start));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_macro_directive_end));
//		rules.add(new MacroInstanceRule(Directive.__ftl_macro_instance_start));
//		rules.add(new MacroInstanceRuleEnd(Directive.__ftl_macro_instance_end));
//
//		rules.add(new DirectiveRule(Directive.__ftl_switch_directive_start));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_switch_directive_end));
//		rules.add(new DirectiveRule(Directive.__ftl_case_directive_start));
//		rules.add(new DirectiveRule(Directive.__ftl_case_default_start));
//
//		rules.add(new DirectiveRule(Directive.__ftl_assign));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_assign_end));
//		rules.add(new DirectiveRule(Directive.__ftl_local));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_local_end));
//		rules.add(new DirectiveRule(Directive.__ftl_global));
//		rules.add(new DirectiveRuleEnd(Directive.__ftl_global_end));
//
//		rules.add(new DirectiveRule(Directive.__ftl_include));
//		rules.add(new DirectiveRule(Directive.__ftl_import));
//		rules.add(new DirectiveRule(Directive.__ftl_break));
//		rules.add(new DirectiveRule(Directive.__ftl_stop));
//		rules.add(new DirectiveRule(Directive.__ftl_nested));
//		rules.add(new DirectiveRule(Directive.__ftl_return));
//
//		rules.add(new GenericDirectiveRule(new Token(Directive.__ftl_directive.name())));
//		rules.add(new GenericDirectiveRuleEnd(new Token(Directive.__ftl_directive_end.name())));
//
//		rules.add(new InterpolationRule(LexicalConstants.DOLLAR, new Token(Directive.__ftl_interpolation.name())));
//		rules.add(new InterpolationRule(LexicalConstants.HASH, new Token(Directive.__ftl_interpolation.name())));
//
//		rules.add(new XmlRule(new Token(XML_TAG)));

	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		if (offset == 0) {
			/* let us believe that offset == 0 is exactly "the beginning" of the document
			 * where the [#ftl] or <#ftl> directive can only be located */
			SyntaxMode newMode = DocumentProvider.findMode(document);
			if (newMode != this.syntaxMode) {
				syntaxModeChanged(newMode);
				/* if the syntaxMode has really changed we should somehow trigger
				 * the reparsing of the whole document. Not sure if expanding
				 * offset and length to the whole document is safe enough */
			}
			super.setPartialRange(document, offset, length, contentType, partitionOffset);
		}
	}

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		if (fRules != null) {
			for (IRule rule : fRules) {
				if (rule instanceof SyntaxModeListener) {
					((SyntaxModeListener) rule).syntaxModeChanged(syntaxMode);
				}
			}
		}
		this.syntaxMode = syntaxMode;
	}
}