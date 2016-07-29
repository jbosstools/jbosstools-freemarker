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
package org.jboss.ide.eclipse.freemarker.target.txt;

import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ui.IEditorInput;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.target.RuleBasedTargetPartitionScanner;
import org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport;
import org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner;

/**
 * A catch all {@link TargetLanguageSupport}.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class TxtSupport implements TargetLanguageSupport {

	@Override
	public TargetPartitionScanner createPartitionScanner() {
		RuleBasedTargetPartitionScanner result = new RuleBasedTargetPartitionScanner();
		result.setDefaultReturnToken(new Token(PartitionType.TEXT.getContentType()));
		return result;
	}

	/**
	 * Always returns {@code true}.
	 *
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport#isDocumentSupported(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public boolean isDocumentSupported(IEditorInput input) {
		return true;
	}

	/**
	 * Colors all using {@link PreferenceKey#COLOR_TEXT}.
	 *
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport#createColoringScanner(java.lang.String)
	 */
	@Override
	public ITokenScanner createColoringScanner(String partitionType) {
		RuleBasedPartitionScanner result = new RuleBasedPartitionScanner();
		result.setDefaultReturnToken(PartitionType
				.createColoringToken(PreferenceKey.COLOR_TEXT));
		return result;
	}

}
