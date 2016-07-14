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
package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.jboss.ide.eclipse.freemarker.Plugin;

/**
 * When pressing enter, indents the new line with the indentation of the current
 * line.
 */
public class SimpleAutoIndentingAutoEditStrategy implements IAutoEditStrategy {

	public static final SimpleAutoIndentingAutoEditStrategy INSTANCE = new SimpleAutoIndentingAutoEditStrategy();
	
	private SimpleAutoIndentingAutoEditStrategy() {
		//
	}
	
	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		try {
			if (isLineDelimiter(command.text, document)) {
				command.text = command.text + getIndentationOfLine(document.getLineOfOffset(command.offset), document);
			}
		} catch (BadLocationException e) {
			Plugin.log(e);
		}
	}

	protected String getIndentationOfLine(int line, IDocument document) throws BadLocationException {
		int offset = document.getLineOffset(line);
		int docLength = document.getLength();
		int indentationStartOffset = offset;
		while (offset < docLength && isIndentationCharacter(document.getChar(offset))) {
			offset++;
		}
		return document.get(indentationStartOffset, offset - indentationStartOffset);
	}

	protected boolean isIndentationCharacter(char c) {
		return c == ' ' || c == '\t';
	}

	protected boolean isLineDelimiter(String text, IDocument document) {
		if (text == null) {
			return false;
		}

		for (String lineDelimiter : document.getLegalLineDelimiters()) {
			if (text.equals(lineDelimiter)) {
				return true;
			}
		}
		return false;
	}

}
