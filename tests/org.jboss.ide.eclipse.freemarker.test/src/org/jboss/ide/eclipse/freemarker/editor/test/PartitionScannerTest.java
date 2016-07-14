/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Daniel Dekany 
 ******************************************************************************/
package org.jboss.ide.eclipse.freemarker.editor.test;

import org.eclipse.jface.text.Document;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionScanner;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;

import junit.framework.TestCase;

public class PartitionScannerTest extends TestCase{
	
	private PartitionScanner scanner = new PartitionScanner();
	
	private void setDocumentContent(String s) {
		scanner.setRange(new Document(s), 0, s.length());
	}

	public void testBasics() {
		setDocumentContent("t<#d x y><@d x y></@d></#d>${x}#{y}<#-- -->");
		new ExpectedTokenList()
				.add(PartitionType.TEXT, 1)
				.add(PartitionType.DIRECTIVE_START, 8)
				.add(PartitionType.MACRO_INSTANCE_START, 8)
				.add(PartitionType.MACRO_INSTANCE_END, 5)
				.add(PartitionType.DIRECTIVE_END, 5)
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.add(PartitionType.HASH_INTERPOLATION, 4)
				.add(PartitionType.COMMENT, 8)
				.assertEqualsTokensFrom(scanner);
	}

	/**
	 * The inner scanner produces 1 character long default tokens, but outer scanner glues them together. 
	 */
	public void testTextPartitionsUnited() {
		setDocumentContent("123${x}123${x}123");
		new ExpectedTokenList()
				.add(PartitionType.TEXT, 3)
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.add(PartitionType.TEXT, 3)
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.add(PartitionType.TEXT, 3)
				.assertEqualsTokensFrom(scanner);
	}

	public void testInterpolationEnd() {
		setDocumentContent("${}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 3)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		setDocumentContent("${(}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		setDocumentContent("${[}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		setDocumentContent("${{}}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 5)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		setDocumentContent("${'}'}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 6)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		setDocumentContent("${\"}\"}x");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 6)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
	}
	
	public void testAngleBracketSyntaxTagEnd() {
		setDocumentContent("<#d>x");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("<#d a > b>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 7)
				.add(PartitionType.TEXT, 3)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("<#d (a > b)>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 12)
				.assertEqualsTokensFrom(scanner);

		// The '>' is stronger than the still opened []
		setDocumentContent("<#d [a > b]>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 8)
				.add(PartitionType.TEXT, 4)
				.assertEqualsTokensFrom(scanner);
		
		// The '>' is stronger than the still opened {}
		setDocumentContent("<#d {a > b}>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 8)
				.add(PartitionType.TEXT, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("<#d 'a > b'>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 12)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("<#d \"a > b\">");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 12)
				.assertEqualsTokensFrom(scanner);
	}

	public void testSquareBracketSyntaxTagEnd() {
		setDocumentContent("[#d a ] b]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 7)
				.add(PartitionType.TEXT, 3)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#d a[] b[[]]]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 14)
				.assertEqualsTokensFrom(scanner);

		setDocumentContent("[#d a > b]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 10)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#d (a ] b)]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 8)
				.add(PartitionType.TEXT, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#d {a ] b}]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 8)
				.add(PartitionType.TEXT, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#d 'a ] b']");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 12)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#d \"a ] b\"]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 12)
				.assertEqualsTokensFrom(scanner);
	}

	public void testEndTags() {
		setDocumentContent("</#>x");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);

		setDocumentContent("</@>x");
		new ExpectedTokenList()
				.add(PartitionType.MACRO_INSTANCE_END, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("</# x . y >x");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 11)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("</#x<#y>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 4)
				.add(PartitionType.DIRECTIVE_START, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("</#x${y}");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 4)
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("</#x<");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("</#");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_END, 3)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#x][/#]x");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 4)
				.add(PartitionType.DIRECTIVE_END, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#x][/@]x");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 4)
				.add(PartitionType.MACRO_INSTANCE_END, 4)
				.add(PartitionType.TEXT, 1)
				.assertEqualsTokensFrom(scanner);
	}
	
	/**
	 * Forced ends are kind of heuristics that limits the negative effect of unclosed constructs.
	 */
	public void testForcedEnds() {
		setDocumentContent("<#x<#y<#z>");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 3)
				.add(PartitionType.DIRECTIVE_START, 3)
				.add(PartitionType.DIRECTIVE_START, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#x[#y[#z]");
		new ExpectedTokenList()
				.add(PartitionType.DIRECTIVE_START, 3)
				.add(PartitionType.DIRECTIVE_START, 3)
				.add(PartitionType.DIRECTIVE_START, 4)
				.assertEqualsTokensFrom(scanner);

		setDocumentContent("${x${x${x}");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 3)
				.add(PartitionType.DOLLAR_INTERPOLATION, 3)
				.add(PartitionType.DOLLAR_INTERPOLATION, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("#{x#{x#{x}");
		new ExpectedTokenList()
				.add(PartitionType.HASH_INTERPOLATION, 3)
				.add(PartitionType.HASH_INTERPOLATION, 3)
				.add(PartitionType.HASH_INTERPOLATION, 4)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("${<##{</<@</");
		new ExpectedTokenList()
				.add(PartitionType.DOLLAR_INTERPOLATION, 2)
				.add(PartitionType.DIRECTIVE_START, 2)
				.add(PartitionType.HASH_INTERPOLATION, 2)
				.add(PartitionType.TEXT, 2)
				.add(PartitionType.MACRO_INSTANCE_START, 2)
				.add(PartitionType.TEXT, 2)
				.assertEqualsTokensFrom(scanner);
	}

	public void testComments() {
		setDocumentContent("<#-- -->[#-- --]");
		new ExpectedTokenList()
				.add(PartitionType.COMMENT, 8)
				.add(PartitionType.TEXT, 8)
				.assertEqualsTokensFrom(scanner);
		
		setDocumentContent("[#-- --]<#-- -->");
		new ExpectedTokenList()
				.add(PartitionType.COMMENT, 8)
				.add(PartitionType.TEXT, 8)
				.assertEqualsTokensFrom(scanner);
	}
	
}
