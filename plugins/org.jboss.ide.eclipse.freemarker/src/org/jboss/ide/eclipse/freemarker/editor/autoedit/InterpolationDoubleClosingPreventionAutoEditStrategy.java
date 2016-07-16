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

/**
 * Suppresses the "}" typed, when it's going to be inserted before the identical character at the end of
 * the partition. This can happen because the editor automatically adds the closing "}", but some may still
 * instinctively types it.
 */
public class InterpolationDoubleClosingPreventionAutoEditStrategy
		extends AbstractSingeTypedCharacterAutoEditStrategy {

	public static final IAutoEditStrategy INSTANCE = new InterpolationDoubleClosingPreventionAutoEditStrategy();
	
	private InterpolationDoubleClosingPreventionAutoEditStrategy() {
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
		
		if (command.offset == lastCharOffset && document.getChar(lastCharOffset) == LexicalConstants.RIGHT_BRACE) {
			command.text = ""; //$NON-NLS-1$
			command.shiftsCaret = false;
			command.caretOffset = command.offset + 1;
		}
	}

	@Override
	protected boolean prefilterTypedCharacter(char typedC) {
		return typedC == LexicalConstants.RIGHT_BRACE;
	}

}
