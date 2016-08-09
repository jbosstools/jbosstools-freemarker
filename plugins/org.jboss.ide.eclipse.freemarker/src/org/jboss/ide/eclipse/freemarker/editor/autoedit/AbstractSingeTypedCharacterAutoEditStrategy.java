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
package org.jboss.ide.eclipse.freemarker.editor.autoedit;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.jboss.ide.eclipse.freemarker.Plugin;

public abstract class AbstractSingeTypedCharacterAutoEditStrategy implements IAutoEditStrategy {

	protected void replacedTypedCharWithString(DocumentCommand command, String replacement) {
		command.text = replacement;
		command.caretOffset = command.offset + 1;
		command.shiftsCaret = false;
	}

	@Override
	public final void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if (command.length == 0 && command.text != null && command.text.length() == 1) {
			try {
				char typedC = command.text.charAt(0);
				if (prefilterTypedCharacter(typedC)) {
					customizeDocumentCommand(document, command, typedC);
				}
			} catch (Exception e) {
				Plugin.log(e);
			}
		}
	}
	
	protected abstract void customizeDocumentCommand(IDocument document, DocumentCommand command, char typedC)
			throws Exception;
	
	/**
	 * Can be used for a rough, first round filtering. Returning {@code true} isn't binding, as you can still left
	 * the command unmodified.
	 */
	protected abstract boolean prefilterTypedCharacter(char typedC); 

}
