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
import org.eclipse.jface.text.IDocumentPartitioner;
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
					PartitionType.PARTITION_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}

	public static SyntaxMode findMode(IDocument document) {
		int docLength = document.getLength();
		if (docLength >= LexicalConstants.SQUARE_SYNTAX_MARKER.length()) {
			try {
				int i = 0;
				for (; i < docLength && ParserUtils.isWhitespace(document.getChar(i)); i++) {
					/* skip whitespace */
				}
				for (; i < docLength; i++) {
					char ch = document.getChar(i);
					if (ch != LexicalConstants.SQUARE_SYNTAX_MARKER.charAt(i)) {
						return SyntaxMode.ANGLE;
					}
				}
				return SyntaxMode.SQUARE;
			} catch (BadLocationException ignore) {
				return SyntaxMode.getDefault();
			}
		}
		return SyntaxMode.getDefault();
	}

}
