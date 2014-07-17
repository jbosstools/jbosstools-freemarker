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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class DirectiveScanner extends RuleBasedScanner {

	public DirectiveScanner() {
		IToken string =
			new Token(
				new TextAttribute(
						Preferences.getInstance().getColor(PreferenceKey.COLOR_STRING)));

		IRule[] result = new IRule[3];
		int i = 0;

		// Add rule for double quotes
		result[i++] = new SingleLineRule("\"", "\"", string,'\\'); //$NON-NLS-1$ //$NON-NLS-2$
	    // Add rule for single quotes
		result[i++] = new SingleLineRule("'", "'", string,'\\'); //$NON-NLS-1$ //$NON-NLS-2$
		// Add generic whitespace rule.
		result[i++] = new WhitespaceRule(new WhitespaceDetector());

		setRules(result);
	}
}
