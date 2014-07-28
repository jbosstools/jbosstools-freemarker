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
package org.jboss.ide.eclipse.freemarker.target;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;

/**
 * An {@link ITokenScanner} for syntax coloring if an FTL target language usable
 * in an {@link IPresentationReconciler}.
 * <p>
 * The input for this scanner are supposed to be the sections of an FTL document
 * that are in the target language, such as HTML or plain text. Those sections
 * have the type {@link PartitionType#TEXT} set by editor's partition scanner
 * and are passed trough {@link #setRange(IDocument, int, int)} to this scanner.
 * <p>
 * Let us assume in the following that the target language is HTML.
 * <p>
 * Internally in this {@link TargetColoringScanner}, each of those
 * {@link PartitionType#TEXT} sections is first divided into target language
 * partitions, such as HTML tags and HTML comments by a scanner delivered by
 * {@link TargetLanguageSupport#createPartitionScanner()}. These target language
 * partitions are then submitted to a coloring scanner responsible for the given
 * tagret language partition got from
 * {@link TargetLanguageSupport#createColoringScanner(String)}. This scanner is
 * able to assign colored tokens to parts of target language partitions. E.g.
 * for HTML tag partitions the scanner can assign distinct colors to tag names
 * attribute names, attribute values, etc. The tokens returned by these internal
 * coloring scanners are returned from {@link #nextToken()}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class TargetColoringScanner implements ITokenScanner {
	private TargetPartitionScanner partitionScanner;
	private TargetLanguageSupport targetLanguageSupport;
	private Map<String, ITokenScanner> coloringScanners;
	private ITokenScanner currentColoringScanner;
	private IDocument document;
	private final Editor editor;

	public TargetColoringScanner(Editor editor) {
		super();
		this.editor = editor;
		this.coloringScanners = new HashMap<String, ITokenScanner>();
	}

	/**
	 * Returns a coloring scanner responsible for the given
	 * {@code partitionType} of the target language.
	 *
	 * @param partitionType the target language partition type
	 * @return see above
	 */
	private ITokenScanner getColoringScanner(String partitionType) {
		ITokenScanner result = coloringScanners.get(partitionType);
		if (result == null) {
			ensureInitialized();
			result = targetLanguageSupport.createColoringScanner(partitionType);
			coloringScanners.put(partitionType, result);
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
	 */
	@Override
	public int getTokenLength() {
		return currentColoringScanner.getTokenLength();
	}

	/**
	 * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
	 */
	@Override
	public int getTokenOffset() {
		return currentColoringScanner.getTokenOffset();
	}

	/**
	 * Returns the coloring scanner for the next partition returned by
	 * {@link #partitionScanner}. Returns {@link #partitionScanner} if the
	 * {@link #partitionScanner} is at EOF.
	 *
	 * @return a coloring scanner or {@link #partitionScanner}
	 */
	private ITokenScanner nextColoringScanner() {
		ensureInitialized();
		IToken token = partitionScanner.nextToken();
		if (token.isEOF()) {
			return partitionScanner;
		}
		ITokenScanner result = TargetColoringScanner.this
				.getColoringScanner((String) token.getData());
		result.setRange(TargetColoringScanner.this.document,
				partitionScanner.getTokenOffset(),
				partitionScanner.getTokenLength());
		return result;
	}

	/**
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (currentColoringScanner == partitionScanner) {
			/*
			 * currentColoringScanner == partitionScanner means that
			 * partitionScanner is at EOF
			 */
			return Token.EOF;
		}

		IToken coloringToken;
		while ((coloringToken = currentColoringScanner.nextToken()).isEOF()) {
			currentColoringScanner = nextColoringScanner();
			if (currentColoringScanner == partitionScanner) {
				/*
				 * currentColoringScanner == partitionScanner means that
				 * partitionScanner is at EOF
				 */
				return Token.EOF;
			}
		}
		return coloringToken;
	}

	/**
	 * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument,
	 *      int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length) {
		ensureInitialized();
		this.document = document;
		this.partitionScanner.setPartialRange(document, offset, length, null,
				offset);
		this.currentColoringScanner = nextColoringScanner();
	}

	/**
	 * Initializes {@link #targetLanguageSupport} (by calling
	 * {@link Editor#getTargetLanguageSupport()} ) and {@link #partitionScanner}
	 * if necessary.
	 */
	private void ensureInitialized() {
		if (this.targetLanguageSupport == null) {
			this.targetLanguageSupport = editor.getTargetLanguageSupport();
		}
		if (this.partitionScanner == null) {
			this.partitionScanner = this.targetLanguageSupport
					.createPartitionScanner();
		}
	}

}
