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
package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.ide.eclipse.freemarker.model.test.AbstractDirectiveTest;
import org.jboss.ide.eclipse.freemarker.model.test.InterpolationTest;

public class InterpolationColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestDirectoryName() {
		return AbstractDirectiveTest.TEST_DIRECTORY;
	}

	@Override
	protected String getTestTemplateName() {
		return InterpolationTest.TEST_FTL_FILE;
	}

	public void testInterpolationColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(10) // attributes
				.otherExpPart(4) // = {
				.string(6) // "href"
				.otherExpPart(1) // :
				.string(21) // "https://example.com"
				.otherExpPart(1) // ,
				.string(8) // "target"
				.otherExpPart(1) // :
				.string(8) // "_blank"
				.otherExpPart(1) // }
				.directive(1) // >
				.text(1) // <whitespace>
				.xmlTag(4) // <h1>
				.text(8) // Welcome
				.interpolation(2) // ${
				.variable(4) // user
				.interpolation(1) // }
				.text(1) // !
				.xmlTag(5) // </h1>
				.text(1) // <whitespace>
				.xmlTag(29) // <p style="font-weight: bold">
				.text(19) // Our latest product:
				.xmlTag(4) // </p>
				.text(1) // <whitespace>
				.xmlTag(9) // <a href="
				.interpolation(2) // ${
				.variable(3) // url
				.interpolation(1) // }
				.xmlTag(2) // ">
				.interpolation(2) // ${
				.variable(7) // content
				.interpolation(1) // }
				.xmlComment(18) // <!-- comment 1 -->
				.xmlTag(4) // </a>
				.text(1) // <whitespace>
				.xmlTag(1) // <
				.interpolation(2) // ${
				.variable(3) // tag
				.interpolation(1) // }
				.xmlTag(1) // <whitespace>
				.interpolation(2) // ${
				.variable(9) // attribute
				.interpolation(1) // }
				.xmlTag(2) // ="
				.interpolation(2) // ${
				.variable(5) // value
				.interpolation(1) // }
				.xmlTag(2) // ">
				.interpolation(2) // ${
				.variable(7) // content
				.interpolation(1) // }
				.xmlTag(2) // </
				.interpolation(2) // ${
				.variable(3) // tag
				.interpolation(1) // }
				.xmlTag(1) // >
				.text(1) // <whitespace>
				.xmlTag(1) // <
				.interpolation(2) // ${
				.variable(3) // tag
				.interpolation(1) // }
				.xmlTag(1) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(1) // <whitespace>
				.variable(10) // attributes
				.otherExpPart(6) // ?keys
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(3) // key
				.directive(1) // >
				.interpolation(2) // ${
				.variable(3) // key
				.interpolation(1) // }
				.xmlTag(2) // ="
				.interpolation(2) // ${
				.variable(10) // attributes
				.otherExpPart(1) // [
				.variable(3) // key
				.otherExpPart(1) // ]
				.interpolation(1) // }
				.xmlTag(2) // "
				.directive(8) // </#list>
				.xmlTag(1) // >
				.interpolation(2) // ${
				.variable(7) // content
				.interpolation(1) // }
				.xmlTag(2) // </
				.interpolation(2) // ${
				.variable(3) // tag
				.interpolation(1) // }
				.xmlTag(1) // >
				.text(2) // !
				.xmlComment(60) // <!-- <p style="font-weight:bold">Our latest product:</p> -->
				.text(1) // <whitespace>
				.xmlComment(5) // <!--
				.interpolation(2) // ${
				.variable(7) // content
				.interpolation(1) // }
				.xmlComment(4) // -->
				.text(1) // <whitespace>
				.comment(19) // <#-- ${content} -->
				.build();
		validateColoring(expected);
	}

}
