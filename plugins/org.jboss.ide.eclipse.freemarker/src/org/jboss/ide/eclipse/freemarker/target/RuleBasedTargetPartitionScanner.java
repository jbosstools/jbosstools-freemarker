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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.util.RobustStack;

/**
 * An implementation of {@link TargetPartitionScanner}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class RuleBasedTargetPartitionScanner extends BufferedRuleBasedScanner implements TargetPartitionScanner {

	private RobustStack<String> typeStack = new RobustStack<String>(null);

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner#pushPartitionType(java.lang.String)
	 */
	@Override
	public void pushPartitionType(String type) {
		typeStack.push(type);
	}

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner#peekPartitionType()
	 */
	@Override
	public String peekPartitionType() {
		return typeStack.peek();
	}

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner#popPartitionType()
	 */
	@Override
	public String popPartitionType() {
		return typeStack.pop();
	}

	/**
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (fOffset >= fRangeEnd) {
			return Token.EOF;
		}

		String contentType = peekPartitionType();

		if (contentType == null || fRules == null) {
			return super.nextToken();
		}

		fTokenOffset= fOffset;
		fColumn= UNDEFINED;

		IToken token;
		for (IRule r : fRules) {
			IPredicateRule rule = (IPredicateRule) r;
			token= rule.getSuccessToken();
			if (token.getData().equals(contentType)) {
				token = rule.evaluate(this, true);
				if (token.isEOF() || !token.isUndefined()) {
					return token;
				}
			}
		}

		return super.nextToken();
	}

	/**
	 * @see org.eclipse.jface.text.rules.IPartitionTokenScanner#setPartialRange(org.eclipse.jface.text.IDocument, int, int, java.lang.String, int)
	 */
	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		super.setRange(document, offset, length);
	}

	/**
	 * Sets the rules.
	 * @param rules the rules
	 */
	public void setPredicateRules(IPredicateRule[] rules) {
		super.setRules(rules);
	}

	/**
	 * Always throws an {@link UnsupportedOperationException} use {@link #setPredicateRules(IPredicateRule[])} instead.
	 */
	@Override
	public void setRules(IRule[] rules) {
		throw new UnsupportedOperationException("Use setPredicateRules(IPredicateRule[]) rather than setRules(IRule[])."); //$NON-NLS-1$
	}

}
