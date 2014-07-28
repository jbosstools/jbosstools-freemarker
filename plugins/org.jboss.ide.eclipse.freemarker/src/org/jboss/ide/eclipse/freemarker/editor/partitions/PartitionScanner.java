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
package org.jboss.ide.eclipse.freemarker.editor.partitions;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.DocumentProvider;
import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PartitionScanner implements IPartitionTokenScanner, SyntaxModeListener {

	private static class ScannerState {
		private final int offset;
		private int length;
		private final IToken token;
		public ScannerState(int offset, int length, IToken token) {
			super();
			this.offset = offset;
			this.length = length;
			this.token = token;
		}
		public int getOffset() {
			return offset;
		}
		public int getLength() {
			return length;
		}
		public IToken getToken() {
			return token;
		}
	}

	private SyntaxMode syntaxMode = SyntaxMode.getDefault();
	private ScannerState nextState;
	private ScannerState currentState;
	private final RuleBasedPartitionScanner delegate;
	private final IToken defaultReturnToken;
	private IPredicateRule[] predicateRules;

	/**
	 * Creates a new partition scanner.
	 */
	public PartitionScanner() {

		this.delegate = new RuleBasedPartitionScanner();
		PartitionType[] partitionTypes = PartitionType.values();
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>(partitionTypes.length);
		for (PartitionType partitionType : partitionTypes) {
			IPredicateRule rule = partitionType.createPartitioningRule();
			if (rule != null) {
				/* this should be the case only for FTL text */
				rules.add(rule);
			}
		}
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
		this.defaultReturnToken = new Token(PartitionType.TEXT.name());
		this.delegate.setDefaultReturnToken(defaultReturnToken);

	}

	private void setPredicateRules(IPredicateRule[] rules) {
		this.predicateRules = rules;
		delegate.setPredicateRules(rules);
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		if (offset == 0) {
			/* let us believe that offset == 0 is exactly "the beginning" of the document
			 * where the [#ftl] or <#ftl> directive can only be located */
			SyntaxMode newMode = DocumentProvider.findMode(document);
			if (newMode != this.syntaxMode) {
				syntaxModeChanged(newMode);
				/* if the syntaxMode has really changed we should somehow trigger
				 * the reparsing of the whole document. Not sure if expanding
				 * offset and length to the whole document is safe enough */
			}
		}
		delegate.setPartialRange(document, offset, length, contentType, partitionOffset);
	}

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		if (predicateRules != null) {
			for (IRule rule : predicateRules) {
				if (rule instanceof SyntaxModeListener) {
					((SyntaxModeListener) rule).syntaxModeChanged(syntaxMode);
				}
			}
		}
		this.syntaxMode = syntaxMode;
	}

	@Override
	public IToken nextToken() {
		/* first shift next to current */
		this.currentState = this.nextState;

		/* Then populate the next, but try to merge with current as far as possible */
		IToken nextToken = delegate.nextToken();
		while (this.currentState.token == this.defaultReturnToken && nextToken == this.defaultReturnToken) {
			int newOffset = delegate.getTokenOffset();
			int newLength = delegate.getTokenLength();
			this.currentState.length = newOffset - this.currentState.offset + newLength;
			nextToken = delegate.nextToken();
		}
		this.nextState = new ScannerState(delegate.getTokenOffset(), delegate.getTokenLength(), nextToken);

		/* and return current */
		return currentState.getToken();
	}

	@Override
	public int getTokenOffset() {
		return currentState.getOffset();
	}

	@Override
	public int getTokenLength() {
		return currentState.getLength();
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		delegate.setRange(document, offset, length);
		IToken nextToken = delegate.nextToken();
		this.nextState = new ScannerState(delegate.getTokenOffset(), delegate.getTokenLength(), nextToken );
	}

}