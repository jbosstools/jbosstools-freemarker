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

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

/**
 * A {@link PredicateRule} that matches an FTL macro instance start tags, such as {@code <@myMacro ...>} and marks them
 * as the {@link PartitionType#MACRO_INSTANCE_START} partitions.
 */
public class MacroInstanceStartPartitionRule extends GenericDirectiveStartPartitionRule {
	
	private static final Token SUCCESS_TOKEN = new Token(PartitionType.MACRO_INSTANCE_START.getContentType());

	public MacroInstanceStartPartitionRule() {
		super(getStartSequence(SyntaxMode.getDefault()));
	}
	
	@Override
	public IToken getSuccessToken() {
		return SUCCESS_TOKEN;
	}
	
	@Override
	protected char[] getSyntaxModeTagStart(SyntaxMode syntaxMode) {
		return getStartSequence(syntaxMode);
	}

	private static char[] getStartSequence(SyntaxMode syntaxMode) {
		return syntaxMode.getMacroInstanceStart().toCharArray();
	}
	
}
