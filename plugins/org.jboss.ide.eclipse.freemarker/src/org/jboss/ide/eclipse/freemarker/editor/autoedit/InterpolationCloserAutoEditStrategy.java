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
import org.eclipse.jface.text.IDocument;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

public class InterpolationCloserAutoEditStrategy extends AbstractSingeTypedCharacterAutoEditStrategy {
	
	public final static InterpolationCloserAutoEditStrategy INSTANCE = new InterpolationCloserAutoEditStrategy();
	
	private InterpolationCloserAutoEditStrategy() {
		//
	}
	
	@Override
	protected void customizeDocumentCommand(IDocument document, DocumentCommand command, char typedC) throws Exception {
		if (command.offset <= 0) {
			return;
		}
		
		char prevC = document.getChar(command.offset - 1);
		if (prevC == LexicalConstants.DOLLAR || prevC == LexicalConstants.HASH) {
			replacedTypedCharWithString(command, "" + typedC + LexicalConstants.RIGHT_BRACE); //$NON-NLS-1$
		}
	}

	@Override
	protected boolean prefilterTypedCharacter(char typedC) {
		return typedC == LexicalConstants.LEFT_BRACE;
	}

}
