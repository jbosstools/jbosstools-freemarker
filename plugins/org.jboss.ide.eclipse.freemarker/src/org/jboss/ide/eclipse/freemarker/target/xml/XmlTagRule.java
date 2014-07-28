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
package org.jboss.ide.eclipse.freemarker.target.xml;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.jboss.ide.eclipse.freemarker.target.TargetMultiLineRule;
import org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner;

/**
 * Matches XML/HTML tags. As a subclass of {@link TargetMultiLineRule} rule,
 * it is supposed to be used within a {@link TargetPartitionScanner}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class XmlTagRule extends TargetMultiLineRule {

	public XmlTagRule(IToken token) {
		super(String.valueOf(XmlLexicalConstants.LEFT_ANGLE_BRACKET), String
				.valueOf(XmlLexicalConstants.RIGHT_ANGLE_BRACKET), token,
				(char) 0, true);
	}

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetMultiLineRule#sequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner, char[], boolean)
	 */
	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner,
			char[] sequence, boolean eofAllowed) {
		int c = scanner.read();
		if (sequence[0] == XmlLexicalConstants.LEFT_ANGLE_BRACKET) {
			if (c == XmlLexicalConstants.QUESTION_MARK) {
				// processing instruction - abort
				scanner.unread();
				return false;
			}
			if (c == XmlLexicalConstants.EXCLAMATION_MARK) {
				scanner.unread();
				// comment - abort
				return false;
			}
		} else if (sequence[0] == XmlLexicalConstants.RIGHT_ANGLE_BRACKET) {
			scanner.unread();
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}

}
