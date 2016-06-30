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

public class MultilineColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestTemplateName() {
		return "multiline.ftl";
	}

	public void testColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.directive(4) // <#if
				.otherExpPart(11) // 1  + 2  <
				.variable(1) // x
				.otherExpPart(1) // <whitespace>
				.directive(1) // >
				.text(1) // t
				.directive(7) // </#if >
				.text(2) // t
				.interpolation(2) // ${
				.otherExpPart(1) // <whitespace>
				.variable(1) // a
				.otherExpPart(3) // +
				.string(19) // "multi line string"
				.otherExpPart(3) // +
				.variable(1) // c
				.otherExpPart(1) // <whitespace>
				.interpolation(1) // }
				.build();
		validateColoring(expected);
	}

}
