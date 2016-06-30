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

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.jboss.ide.eclipse.freemarker.editor.SyntaxModeListener;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;

public class SyntaxModeListenerRuleBasedScanner extends RuleBasedScanner implements SyntaxModeListener {

	@Override
	public void syntaxModeChanged(SyntaxMode syntaxMode) {
		if (fRules == null) {
			return;
		}
		
		for (IRule rule : fRules) {
			if (rule instanceof SyntaxModeListener) {
				((SyntaxModeListener) rule).syntaxModeChanged(syntaxMode);
			}
		}
	}
	
}
