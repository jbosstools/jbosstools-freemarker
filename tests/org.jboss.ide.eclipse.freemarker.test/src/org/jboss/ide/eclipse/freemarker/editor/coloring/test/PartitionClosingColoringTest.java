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

public class PartitionClosingColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestTemplateName() {
		return "partition-closing.ftl";
	}

	public void test() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.interpolation(2) // ${
				.otherExpPart(2) // 1
				.comment(10) // <!-- } -->
				.otherExpPart(4) // + 2
				.interpolation(1) // }
				.text(1) // <whitespace>
				.interpolation(2) // ${
				.otherExpPart(4) // 1 +
				.string(3) // "}"
				.otherExpPart(4) // + 2
				.interpolation(1) // }
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.otherExpPart(12) // = (1 > 2),
				.variable(1) // y
				.otherExpPart(3) // =
				.string(3) // '>'
				.otherExpPart(2) // ,
				.variable(1) // z
				.otherExpPart(3) // =
				.string(3) // ">"
				.otherExpPart(1) // <whitespace>
				.comment(10) // <#-- > -->
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.otherExpPart(6) // = 1/2
				.directive(2) // />
				.text(1) // <whitespace>
				.directive(4) // <#if
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.otherExpPart(3) // =
				.comment(12) // <#-- ( > -->
				.variable(1) // x
				.directive(1) // >
				.text(1) // t
				.directive(6) // </#if>
				.build();
		validateColoring(expected);
	}

}
