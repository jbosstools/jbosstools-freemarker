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

public class EscapedIdentifiersColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestTemplateName() {
		return "escaped-identifiers.ftl";
	}

	public void test() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.interpolation(2) // ${
				.variable(6) // \.\:\-
				.otherExpPart(3) // +
				.variable(4) // a\-b
				.interpolation(1) // }
				.text(1) // <whitespace>
				.directive(6) // <@a\-b
				.otherExpPart(1) // <whitespace>
				.variable(4) // c\:d
				.otherExpPart(1) // =
				.variable(1) // e
				.otherExpPart(1) // <whitespace>
				.directive(2) // />
				.text(1) // <whitespace>
				.directive(5) // <@\.a
				.otherExpPart(3) // 1
				.directive(2) // />
				.text(1) // <whitespace>
				.directive(5) // <@a.b
				.otherExpPart(3) // 1
				.directive(2) // />
				.text(1) // <whitespace>
				.directive(3) // <@a
				.otherExpPart(1) // -
				.variable(1) // b
				.otherExpPart(3) // 1
				.directive(2) // />
				.build();
		validateColoring(expected);
	}

}
