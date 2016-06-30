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

import java.util.HashSet;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

/**
 * {@link RuleBasedPartitionScanner} that recognizes {@link NonResumablePrediaceRule}-s. 
 */
public class RuleBasedPartitionScanner2 extends RuleBasedPartitionScanner {

	private HashSet<String> nonResumableContentTypes = new HashSet<>();
	
	@Override
	public void setPredicateRules(IPredicateRule[] rules) {
		super.setPredicateRules(rules);
		if (rules != null) {
			for (IPredicateRule rule : rules) {
				if (rule instanceof NonResumablePrediaceRule) {
					nonResumableContentTypes.add((String) rule.getSuccessToken().getData());
				}
			}
		}
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		if (contentType != null && nonResumableContentTypes.contains(contentType)) {
			super.setRange(document, partitionOffset, offset - partitionOffset + length);
		} else {
			super.setPartialRange(document, offset, length, contentType, partitionOffset);
		}
	}
	
}
