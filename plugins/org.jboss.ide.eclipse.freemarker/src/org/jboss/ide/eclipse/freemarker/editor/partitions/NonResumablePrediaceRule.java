/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.ide.eclipse.freemarker.editor.partitions;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

/**
 * A {@link IPredicateRule} whose
 * {@link IPredicateRule#evaluate(ICharacterScanner, boolean)} method must not
 * be called with {@code true} {@code resume} parameter. This is used for
 * partitions with more complex syntactical rules, where therefore starting the
 * parsing in the middle of a partition is not possible. This is still a
 * predicate rule in that it have a {@link #getSuccessToken()} method. Rules of
 * this class should only be used with {@link RuleBasedPartitionScanner2}, which
 * recognizes this class.
 */
public abstract class NonResumablePrediaceRule implements IPredicateRule {

	@Override
	public final IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (resume == true) {
			throw new IllegalStateException("This " //$NON-NLS-1$
					+ IPredicateRule.class.getSimpleName()
					+ " (" + this.getClass().getName() + ") doesn't support  resuming. " //$NON-NLS-1$ //$NON-NLS-2$
					+ "Tip: You can only use this rule with a partition scanner that recognizes the " //$NON-NLS-1$
					+ NonResumablePrediaceRule.class.getName() + " class."); //$NON-NLS-1$
		}
		return evaluate(scanner);
	}

	
	
}
