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
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;

public class FTLCommentCloserAutoEditStrategy extends AbstractSingeTypedCharacterAutoEditStrategy {
	
	public final static FTLCommentCloserAutoEditStrategy INSTANCE = new FTLCommentCloserAutoEditStrategy();
	
	private FTLCommentCloserAutoEditStrategy() {
		//
	}
	
	@Override
	protected void customizeDocumentCommand(IDocument document, DocumentCommand command, char typedC) throws Exception {
		final int cmdOffset = command.offset; 
		
		final int tagStartOffset;
		{
			if (cmdOffset < 3) {
				return;
			}
			if (document.getChar(cmdOffset - 1) != LexicalConstants.MINUS
					|| document.getChar(cmdOffset - 2) != LexicalConstants.HASH) {
				return;
			}

			tagStartOffset = cmdOffset - 3;
		}
		
		ITypedRegion partition = ((IDocumentExtension3) document).getPartition(
				DocumentProvider.FTL_PARTITIONING, tagStartOffset, false);
		if (partition.getOffset() != tagStartOffset) {
			return;
		}
		
		char tagStartC = document.getChar(partition.getOffset());
		if (tagStartC != LexicalConstants.LEFT_ANGLE_BRACKET && tagStartC != LexicalConstants.LEFT_SQUARE_BRACKET) {
			// This probably can't happen
			return;
		}
		
		char tagEndC = ParserUtils.getMatchingRightBracket(tagStartC);
		replacedTypedCharWithString(command, "-  --"  //$NON-NLS-1$
				+ (document.getChar(partition.getOffset() + partition.getLength() - 1) == tagEndC
						? "" : tagEndC)); //$NON-NLS-1$
		command.caretOffset++; // Skip space after "-"
	}

	@Override
	protected boolean prefilterTypedCharacter(char typedC) {
		return typedC == LexicalConstants.MINUS;
	}

}
