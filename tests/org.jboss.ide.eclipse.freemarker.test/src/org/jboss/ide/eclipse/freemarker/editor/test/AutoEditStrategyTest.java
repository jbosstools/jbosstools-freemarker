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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.editor.SimpleAutoIndentingAutoEditStrategy;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionScanner;
import org.junit.Assert;

import junit.framework.TestCase;

public class AutoEditStrategyTest extends TestCase {
	
	private IDocument document = new Document();
	private int caretOffset;
	private IAutoEditStrategy autoEditStrategy;
	
	public void setUp() {
		DocumentProvider.setupDocumentPartitioner(document);
	}
	
	public void testSimpleAutoIndent() throws BadLocationException {
		autoEditStrategy = SimpleAutoIndentingAutoEditStrategy.INSTANCE;
		
		document.set(" \t foo");
		moveCaretToEnd();
		type("\nx");
		Assert.assertEquals(" \t foo\n \t x", document.get());

		document.set(" ");
		moveCaretToEnd();
		type("\nx");
		Assert.assertEquals(" \n x", document.get());
		
		document.set("");
		moveCaretToEnd();
		type("\nx");
		Assert.assertEquals("\nx", document.get());
		
		document.set("");
		moveCaretToEnd();
		type("x");
		Assert.assertEquals("x", document.get());
	}

	private void moveCaretToEnd() {
		caretOffset = document.getLength();
	}

	private void type(String s) throws BadLocationException {
		for (int i = 0; i < s.length(); i++) {
			type(s.charAt(i));
		}
	}
	
	private void type(char c) throws BadLocationException {
		if (autoEditStrategy == null) {
			throw new IllegalStateException("The JUnit test didn't set the autoEditStrategy field!");
		}
		
		DocumentCommand cmd = createTypingCommand(String.valueOf(c));
		autoEditStrategy.customizeDocumentCommand(document, cmd);
		document.replace(cmd.offset, cmd.length, cmd.text);
		if (cmd.shiftsCaret) {
			caretOffset += cmd.text.length();
		} else {
			caretOffset = cmd.offset; // Should I do this? 
		}
	}
	
	private DocumentCommand createTypingCommand(String text) {
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

}
