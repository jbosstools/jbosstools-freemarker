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

import java.util.Stack;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class InterpolationRule extends SingleLineRule {

    public InterpolationRule(char startChar, IToken token) {
        super(""+ startChar + LexicalConstants.LEFT_BRACE, String.valueOf(LexicalConstants.RIGHT_BRACE), token); //$NON-NLS-1$
    }

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		Stack<Character> keyStack = new Stack<Character>();
		int charsRead = 0;
		while ((c= scanner.read()) != ICharacterScanner.EOF) {
			charsRead ++;
			char cCheck = (char) c;
			if (c == LexicalConstants.LEFT_BRACE) {
				if (keyStack.size() == 0) {
					break;
				}
			}
			else if (c == LexicalConstants.QUOT) {
				if (keyStack.size() > 0 && keyStack.peek().charValue() == LexicalConstants.QUOT) {
					keyStack.pop();
				}
				else {
					keyStack.push(Character.valueOf(cCheck));
				}
			}
			else if (c == LexicalConstants.LEFT_PARENTHESIS) {
				if (keyStack.size() > 0 && keyStack.peek().charValue() == LexicalConstants.QUOT) {
					// string... don't add to stack
				}
				else {
					keyStack.push(Character.valueOf(cCheck));
				}
			}
			else if (c == LexicalConstants.RIGHT_PARENTHESIS) {
				if (keyStack.size() > 0 && keyStack.peek().charValue() == LexicalConstants.QUOT) {
					// string... don't add to stack
				}
				else if (keyStack.size() > 0 && keyStack.peek().charValue() == LexicalConstants.LEFT_PARENTHESIS) {
					keyStack.pop();
				}
			}
			else if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
				charsRead ++;
			}
			else if (c == LexicalConstants.RIGHT_BRACE) {
				if (keyStack.size() == 0) {
					return true;
				}
			}
			else if (c == LexicalConstants.LF) {
				break;
			}
		}
		if (fBreaksOnEOF) return true;
		for (int i=0; i<charsRead; i++)
			scanner.unread();
		return false;
	}

}