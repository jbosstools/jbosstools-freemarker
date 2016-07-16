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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.jboss.ide.eclipse.freemarker.editor.Configuration;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.junit.Assert;

import junit.framework.TestCase;

public class AutoEditStrategyTest extends TestCase {
	
	private static final String[] FTL_TAG_PERMUTATIONS = new String[] {null, "<[>]", "#@", "<[>]#@"};
	private static final String[] INTERPOLATION_PERMUTATIONS = new String[] {null, "$#"};
	
	private IDocument document = new Document();
	private int caretOffset;
	
	private Map<String, IAutoEditStrategy[]> autoEditStrategiesByContentType
			= new HashMap<String, IAutoEditStrategy[]>();
			
	private String translations;

	public void testBasicTyping() throws Exception {
		setContentWithCaret("|");
		type("x");
		assertContentWithCaret("x|");
		type("y");
		assertContentWithCaret("xy|");
		
		setContentWithCaret("1|2");
		type("x");
		assertContentWithCaret("1x|2");
		type("y");
		assertContentWithCaret("1xy|2");
	}
			
	public void testSimpleAutoIndent() throws Exception {
		setContentWithCaret(" \t foo|");
		type("\n");
		assertContentWithCaret(" \t foo\n \t |");

		setContentWithCaret(" |");
		type("\n");
		assertContentWithCaret(" \n |");
		
		setContentWithCaret("|");
		type("\n");
		assertContentWithCaret("\n|");
		
		setContentWithCaret("  ${|}");
		type("\n");
		assertContentWithCaret("  ${\n  |}");
		
		setContentWithCaret("  <#if|>");
		type("\n");
		assertContentWithCaret("  <#if\n  |>");
		
		setContentWithCaret("  <#-- |-->");
		type("\n");
		assertContentWithCaret("  <#-- \n  |-->");
	}

	public void testInterpolationAutoClosing() throws Exception {
		for (String translations : INTERPOLATION_PERMUTATIONS) {
			this.translations = translations;
			
			setContentWithCaret("$|");
			type("{");
			assertContentWithCaret("${|}");
		}
	}
	
	public void testInterpolationDoubleClosingPrevention() throws Exception {
		for (String translations : INTERPOLATION_PERMUTATIONS) {
			this.translations = translations;
			
			setContentWithCaret("${|}");
			type("}");
			assertContentWithCaret("${}|");
			
			setContentWithCaret("${x|}");
			type("}");
			assertContentWithCaret("${x}|");
			
			setContentWithCaret("#{x|}");
			type("}");
			assertContentWithCaret("#{x}|");
		}
	}

	public void testFTLTagAutoClosing() throws Exception {
		for (String translations : FTL_TAG_PERMUTATIONS) {
			this.translations = translations;
			
			setContentWithCaret("<|");
			type("#");
			assertContentWithCaret("<#|>");
			
			setContentWithCaret("</|");
			type("#");
			assertContentWithCaret("</#|>");
		}
		
		this.translations = null;
		
		setContentWithCaret("[#ftl]<|");
		type("#");
		assertContentWithCaret("[#ftl]<#|");

		setContentWithCaret("[#ftl][|");
		type("#");
		assertContentWithCaret("[#ftl][#|]");
		
		setContentWithCaret("<#ftl>[|");
		type("#");
		assertContentWithCaret("<#ftl>[#|");
		
		setContentWithCaret("<#ftl><|");
		type("#");
		assertContentWithCaret("<#ftl><#|>");
	}
	
	public void testFTLTagDoubleClosingPrevention() throws Exception {
		// TODO Some permutations don't work because DocumentProvider.findMode(IDocument) is broken.
		// for (String translations : FTL_TAG_PERMUTATIONS) {
		//     this.translations = translations;
		
		setContentWithCaret("<#|>");
		type(">");
		assertContentWithCaret("<#>|");
		
		setContentWithCaret("<#x|>");
		type(">");
		assertContentWithCaret("<#x>|");
		
		setContentWithCaret("<#x></#x|>");
		type(">");
		assertContentWithCaret("<#x></#x>|");
		
		// }
	}

	public void testFTLCommentClosing() throws Exception {
		for (String translations : new String[] { null, "<[>]" }) {
			this.translations = translations;
			
			setContentWithCaret("|");
			type("<#");
			assertContentWithCaret("<#|>");
			type("--");
			assertContentWithCaret("<#-- | -->");
			
			setContentWithCaret("<#-|");
			type("-");
			assertContentWithCaret("<#-- | -->");
		}
	}

	public void testCombined() throws Exception {
		setContentWithCaret("|");
		type("<#if x");
		caretOffset = document.getLength();
		type("\n  ${y");
		caretOffset = document.getLength();
		type("\n<#--foo");
		caretOffset = document.getLength();
		type("\n</#if");
		assertContentWithCaret("<#if x>\n  ${y}\n  <#-- foo -->\n  </#if|>");
	}
	
	public void setUp() {
		DocumentProvider.setupDocumentPartitioner(document);
		
		// We try to mimic what a real Editor does (getting things from the TextSourceViewerConfiguration):
		for (String contentType : Configuration.getConfiguredContentTypes()) {
			autoEditStrategiesByContentType.put(contentType, Configuration.getAutoEditStrategies(contentType, true));
		}
	}
	
	private void setContentWithCaret(String content) {
		content = translate(content);
		
		caretOffset = content.indexOf('|');
		if (caretOffset == -1) {
			throw new IllegalArgumentException("No '|' (caret position mark) in the arugment.");
		}
		document.set(content.substring(0, caretOffset) + content.substring(caretOffset + 1));
	}
	
	/**
	 * Convenience method for doing a sequence of {@link type(char)} calls.
	 */
	private void type(String s) throws BadLocationException, BadPartitioningException {
		for (int i = 0; i < s.length(); i++) {
			type(s.charAt(i));
		}
	}

	/**
	 * Emulates typing a single character. Simply calling {@link IDocument#replace(int, int, String)} wouldn't trigger
	 * the auto edit strategies, but this does.
	 */
	private void type(char c) throws BadLocationException, BadPartitioningException {
		ITypedRegion partition = ((IDocumentExtension3) document).getPartition(
				DocumentProvider.FTL_PARTITIONING, caretOffset, false);
		// Avoid the 0-length TEXT-like partition at the end:
		if (partition.getLength() == 0 && partition.getOffset() > 0) {
			partition = ((IDocumentExtension3) document).getPartition(
					DocumentProvider.FTL_PARTITIONING, caretOffset - 1, false);
		}
		
		String contentType = partition.getType();
		IAutoEditStrategy[] autoEditStrategies = autoEditStrategiesByContentType.get(contentType);
		
		DocumentCommand cmd = createInsertAtCaretCommand(String.valueOf(c));
		for (IAutoEditStrategy autoEditStrategy : autoEditStrategies) {
			autoEditStrategy.customizeDocumentCommand(document, cmd);
		}
		
		document.replace(cmd.offset, cmd.length, cmd.text);
		caretOffset = cmd.caretOffset + (cmd.shiftsCaret ? cmd.text.length() : 0);
	}
	
	private DocumentCommand createInsertAtCaretCommand(String text) {
		text = translate(text);
		
		if (caretOffset > document.getLength()) {
			caretOffset = document.getLength();
		}
		DocumentCommand cmd = new DocumentCommand() { /* to access protected constructor */ };
		cmd.offset = caretOffset;
		cmd.length = 0;
		cmd.text = text;
		cmd.doit = true;
		cmd.shiftsCaret = true;
		cmd.caretOffset = caretOffset;		
		return cmd;
	}

	/**
	 * Replaces characters according the mapping given in the second string. The
	 * characters at an even index (such as index 0) will be replaced with the
	 * character at the next index (such as index 1). Used for generating
	 * syntactical permutations of tested FTL fragments.
	 */
	private String translate(String s) {
		if (translations == null || s == null) {
			return s;
		}
		for (int i = 0; i < translations.length(); i += 2) {
			s = s.replace(translations.charAt(i), translations.charAt(i + 1));
		}
		return s;
	}
	
	private void assertContentWithCaret(String expected) {
		String content = translate(document.get());
		Assert.assertEquals(translate(expected), content.substring(0, caretOffset) + "|" + content.substring(caretOffset));
	}
	
}
