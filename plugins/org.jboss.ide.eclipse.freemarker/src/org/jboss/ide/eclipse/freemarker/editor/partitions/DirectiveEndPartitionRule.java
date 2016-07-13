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

import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

/**
 * A {@link MultiLineRule} that matches an FTL directive end tags and marks them as
 * the {@link PartitionType#DIRECTIVE_END} partitions.
 */
public class DirectiveEndPartitionRule extends GenericDirectiveEndPartitionRule {

	public DirectiveEndPartitionRule() {
		super(SyntaxMode.getDefault().getDirectiveEnd(), new Token(PartitionType.DIRECTIVE_END.name()));
	}

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		setStartSequence(getStartSequence(syntaxMode));
		setEndSequence(syntaxMode.getTagEnd().toCharArray());
	}

	protected char[] getStartSequence(SyntaxMode syntaxMode) {
		return syntaxMode.getDirectiveEnd().toCharArray();
	}

}
