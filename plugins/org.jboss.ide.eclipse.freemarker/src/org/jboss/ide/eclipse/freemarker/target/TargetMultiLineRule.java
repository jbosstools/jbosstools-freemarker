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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * A {@link MultiLineRule} rule supposed to be used within a
 * {@link TargetPartitionScanner}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class TargetMultiLineRule extends MultiLineRule {

	private Boolean endMatched;

	public TargetMultiLineRule(String startSequence, String endSequence,
			IToken token, char escapeCharacter, boolean breaksOnEOF) {
		super(startSequence, endSequence, token, escapeCharacter, breaksOnEOF);
	}

	public TargetMultiLineRule(String startSequence, String endSequence,
			IToken token, char escapeCharacter) {
		super(startSequence, endSequence, token, escapeCharacter);
	}

	public TargetMultiLineRule(String startSequence, String endSequence,
			IToken token) {
		super(startSequence, endSequence, token);
	}

	/**
	 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner, char[], boolean)
	 */
	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner,
			char[] sequence, boolean eofAllowed) {
		if (sequence == fStartSequence
				&& scanner instanceof TargetPartitionScanner) {
			this.endMatched = null;
			TargetPartitionScanner targetPartitionScanner = (TargetPartitionScanner) scanner;
			String peekType = targetPartitionScanner.peekPartitionType();
			String ownType = (String) getSuccessToken().getData();
			if (ownType.equals(peekType)) {
				return true;
			} else {
				boolean result = super.sequenceDetected(scanner, sequence,
						eofAllowed);
				if (result) {
					targetPartitionScanner.pushPartitionType(ownType);
				}
				return result;
			}
		}
		boolean result = super.sequenceDetected(scanner, sequence, eofAllowed);
		if (sequence == fEndSequence) {
			this.endMatched = Boolean.valueOf(result);
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		boolean endSequenceDetected = super.endSequenceDetected(scanner);
		if (endSequenceDetected && scanner instanceof TargetPartitionScanner) {
			TargetPartitionScanner targetPartitionScanner = (TargetPartitionScanner) scanner;
			String peekType = targetPartitionScanner.peekPartitionType();

			String ownType = (String) getSuccessToken().getData();
			if (!ownType.equals(peekType)) {
				throw new IllegalStateException(
						"peekType expected '" + ownType + "' actual '" + peekType + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (Boolean.TRUE.equals(this.endMatched)) {
				targetPartitionScanner.popPartitionType();
				this.endMatched = null;
			}
		}
		return endSequenceDetected;
	}
}
