/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionScanner;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class DocumentProvider extends FileDocumentProvider {

	public static final String FTL_PARTITIONING = "org.jboss.ide.eclipse.freemarker.partitioning";


	public DocumentProvider() {
		super();
	}

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PartitionScanner(),
					PartitionType.PARTITION_TYPES) {
				public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
					ITypedRegion region= getPartition(offset);
					if (preferOpenPartitions) {
						if (region.getOffset() == offset && !region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
							if (offset > 0) {
								region= getPartition(offset - 1);
								if (region.getType().equals(PartitionType.DIRECTIVE_START.name()))
									return region;
							}
							return new TypedRegion(offset, 0, IDocument.DEFAULT_CONTENT_TYPE);
						}
					}
			        return region;
				}
				
			};
			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 docExt3 = (IDocumentExtension3) document;
				docExt3.setDocumentPartitioner(FTL_PARTITIONING, partitioner);
				partitioner.connect(document);
			} else {
				document.setDocumentPartitioner(partitioner);
				partitioner.connect(document);
			}
		}
		return document;
	}

	public static SyntaxMode findMode(IDocument document) {
		int docLength = document.getLength();
		if (docLength >= 2 ) { 
			try {
				// s represents a state machine that recognize '[#' or '<#', whatever comes first.
				// 0 -> nothing found
				// 1 -> < found
				// 2 -> [ found
				// 3 -> # found after 1 (angle mode found)
				// 4 -> # found after 2 (square mode found)
				int s = 0;
				
				for (int i = 0; i < docLength && s < 3 ; i++) {
					char ch = document.getChar(i);
					switch(ch){
						case LexicalConstants.LEFT_ANGLE_BRACKET:
							s = 1;
							break;
						case LexicalConstants.LEFT_SQUARE_BRACKET:
							s = 2;
							break;
						case LexicalConstants.HASH:
							if(s == 1) 
								s = 3;
							else if(s == 2) 
								s = 4;
							else 
								s = 0;
							break;
						default:
							s = 0;
							break;
					}
				}
				if( s == 3) 
					return SyntaxMode.ANGLE;
				else if( s == 4) 
					return SyntaxMode.SQUARE;
				else 
					return SyntaxMode.getDefault();
			} catch (BadLocationException ignore) {
				return SyntaxMode.getDefault();
			}
		}
		return SyntaxMode.getDefault();
	}

}
