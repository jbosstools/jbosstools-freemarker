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
package org.jboss.ide.eclipse.freemarker.editor.rules;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.partitions.GenericDirectiveStartPartitionRule;
import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;

/**
 * Matches a particular FTL directive start and marks the region with as the
 * given {@link Directive#name()}. Used for building an {@link ItemSet}.
 */
public class DirectiveRule extends GenericDirectiveStartPartitionRule {

	private final IToken successToken;
	private final String directiveName; 
	
	public DirectiveRule(Directive directive) {
		super(getSyntaxModeTagStart(directive.getKeyword().toString(), SyntaxMode.getDefault()));
		directiveName = directive.getKeyword().toString();
		successToken = new Token(directive.name() /* enum name */);
	}

	@Override
	public IToken getSuccessToken() {
		return successToken;
	}

	private static char[] getSyntaxModeTagStart(String directiveName, SyntaxMode syntaxMode) {
		String tagStart = syntaxMode.getDirectiveStart();
		char[] tagStartWithName = new char[tagStart.length() + directiveName.length()];
		int dst = 0;
		for (int i = 0; i < tagStart.length(); i++) {
			tagStartWithName[dst++] = tagStart.charAt(i);
		}
		for (int i = 0; i < directiveName.length(); i++) {
			tagStartWithName[dst++] = directiveName.charAt(i);
		}
		return tagStartWithName;
	}

	@Override
	protected char[] getSyntaxModeTagStart(SyntaxMode syntaxMode) {
		return getSyntaxModeTagStart(directiveName, syntaxMode);
	}

}