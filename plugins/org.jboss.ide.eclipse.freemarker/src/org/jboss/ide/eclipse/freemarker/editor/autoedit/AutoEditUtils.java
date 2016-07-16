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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;

public final class AutoEditUtils {

	private AutoEditUtils() {
		//
	}

	public static int getNextFTLTagOffset(IDocument document, int offset) {
		IDocumentExtension3 docExt3 = (IDocumentExtension3) document;
		try {
			while (offset < document.getLength()) {
				ITypedRegion partition = docExt3.getPartition(DocumentProvider.FTL_PARTITIONING, offset, false);
	
				String contentType = partition.getType();
				if (contentType.equals(PartitionType.DIRECTIVE_START.getContentType())
						|| contentType.equals(PartitionType.DIRECTIVE_END.getContentType())
						|| contentType.equals(PartitionType.MACRO_INSTANCE_START.getContentType())
						|| contentType.equals(PartitionType.MACRO_INSTANCE_END.getContentType())) {
					return offset;
				}
	
				int partitionLength = partition.getLength();
				// Partition of length 0 shouldn't occur here, but don't stuck
				// in an infinite loop if it still does.
				offset += partitionLength > 0 ? partitionLength : 1;
			}
			return -1;
		} catch (BadLocationException | BadPartitioningException e) {
			Plugin.log(e);
			return -1;
		}
	}
	
}
