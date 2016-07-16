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
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;

/**
 * Suppresses the ">" or "]" typed, when it's going to be inserted before the identical character at the end of
 * the partition. This can happen because the editor automatically adds the closing ">" or "]", but some may still
 * instinctively types it.
 */
public class FTLTagDoubleClosingPreventionAutoEditStrategy extends AbstractSingeTypedCharacterAutoEditStrategy {

	public static final IAutoEditStrategy INSTANCE = new FTLTagDoubleClosingPreventionAutoEditStrategy();
	
	private FTLTagDoubleClosingPreventionAutoEditStrategy() {
		//
	}

	@Override
	protected void customizeDocumentCommand(IDocument document, DocumentCommand command, char typedC) throws Exception {
		ITypedRegion partition = ((IDocumentExtension3) document)
				.getPartition(DocumentProvider.FTL_PARTITIONING, command.offset, false);
		
		if (partition.getLength() < 2) {
			return;
		}

		int lastCharOffset = partition.getOffset() + partition.getLength() - 1;
		if (command.offset != lastCharOffset) {
			return;
		}
		
		char tagClosingC;
		try {
			tagClosingC = ParserUtils.getMatchingRightBracket(document.getChar(partition.getOffset()));
		} catch (IllegalArgumentException e) {
			// Partition didn't start with "<" or "["
			tagClosingC = 0;
		}
		if (typedC == tagClosingC) {
			command.text = ""; //$NON-NLS-1$
			command.shiftsCaret = false;
			command.caretOffset = command.offset + 1;
		}
	}

	@Override
	protected boolean prefilterTypedCharacter(char typedC) {
		return typedC == LexicalConstants.RIGHT_ANGLE_BRACKET || typedC == LexicalConstants.RIGHT_SQUARE_BRACKET;
	}

}
