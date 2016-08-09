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
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;

public class FTLTagCloserAutoEditStrategy extends AbstractSingeTypedCharacterAutoEditStrategy {
	
	public final static FTLTagCloserAutoEditStrategy INSTANCE = new FTLTagCloserAutoEditStrategy();

	private FTLTagCloserAutoEditStrategy() {
		//
	}
	
	@Override
	protected void customizeDocumentCommand(IDocument document, DocumentCommand command, char typedC) throws Exception {
		final int cmdOffset = command.offset; 
		
		final int tagStartOffset;
		final char tagStartC; // '<' or '['
		final boolean isEndTag; // like </#
		{
			if (cmdOffset < 1) {
				return;
			}
			char c = document.getChar(cmdOffset - 1);
			isEndTag = c == LexicalConstants.SLASH;
			if (isEndTag) {
				if (cmdOffset < 2) {
					return;
				}
				tagStartOffset = cmdOffset - 2;
				tagStartC = document.getChar(tagStartOffset);
			} else {
				tagStartOffset = cmdOffset - 1;
				tagStartC = c;
			}

			if (tagStartC != LexicalConstants.LEFT_ANGLE_BRACKET
					&& tagStartC != LexicalConstants.LEFT_SQUARE_BRACKET) {
				return;
			}
		}

		char tagEndC = ParserUtils.getMatchingRightBracket(tagStartC);
		
		// When typing the "#" or "@" into the middle of a "<>", don't make a "<#>>" or "<@>>" out of it.
		// This happens often when someone types "<#", then backspace, then "@" (or the other way around).
		if (tagStartOffset + 1 < document.getLength() && document.getChar(tagStartOffset + 1) == tagEndC) {
			return;
		}
		
		// If the tag syntax is already established, only one of '<' and '[' will count as tag starter,
		// otherwise both does.
		int ftlTagOffset = AutoEditUtils.getNextFTLTagOffset(document, 0);
		if (ftlTagOffset != -1 && document.getChar(ftlTagOffset) != tagStartC) {
			return;
		}
		
		replacedTypedCharWithString(command, "" + typedC + tagEndC); //$NON-NLS-1$
	}

	@Override
	protected boolean prefilterTypedCharacter(char typedC) {
		return typedC == LexicalConstants.HASH || typedC == LexicalConstants.AT;
	}

}
