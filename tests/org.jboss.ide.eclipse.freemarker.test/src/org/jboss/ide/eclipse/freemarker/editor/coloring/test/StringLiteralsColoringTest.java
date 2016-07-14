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
package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import org.eclipse.swt.custom.StyleRange;

public class StringLiteralsColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestTemplateName() {
		return "string-literals.ftl";
	}

	public void test() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.interpolation(2) // ${
				.string(6) // "a\"b"
				.otherExpPart(3) // +
				.string(5) // "a'b"
				.otherExpPart(3) // +
				.string(5) // 'a"b'
				.otherExpPart(3) // +
				.string(6) // 'a\'b'
				.otherExpPart(3) // +
				.string(5) // r'a\'
				.otherExpPart(3) // +
				.string(5) // r"a\"
				.interpolation(1) // }
				.build();
		validateColoring(expected);
	}

}
