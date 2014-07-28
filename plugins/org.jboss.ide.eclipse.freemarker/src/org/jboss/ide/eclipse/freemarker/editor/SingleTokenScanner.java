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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * An {@link ITokenScanner} that always returns only the single
 * {@link #defaultToken} for the whole range set through
 * {@link #setRange(IDocument, int, int)}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class SingleTokenScanner implements ITokenScanner {

	private static final int INVALID_OFFSET = -1;
	private int offset = INVALID_OFFSET;
	private int length = INVALID_OFFSET;
	private final IToken defaultToken;
	private int tokenOffset = INVALID_OFFSET;
	private int rangeOffset = INVALID_OFFSET;

	public SingleTokenScanner(IToken defaultToken) {
		super();
		this.defaultToken = defaultToken;
	}

	@Override
	public int getTokenLength() {
		return tokenOffset == INVALID_OFFSET ? INVALID_OFFSET : offset
				- tokenOffset;
	}

	@Override
	public int getTokenOffset() {
		return tokenOffset;
	}

	@Override
	public IToken nextToken() {
		if (offset < rangeOffset + length) {
			tokenOffset = offset;
			offset += length;
			return defaultToken;
		}
		return Token.EOF;
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.offset = offset;
		this.rangeOffset = offset;
		this.length = length;
		this.tokenOffset = INVALID_OFFSET;
	}

}
