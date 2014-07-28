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
package org.jboss.ide.eclipse.freemarker.model.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils.ParseException;
import org.jboss.ide.eclipse.freemarker.model.AssignmentDirective;
import org.jboss.ide.eclipse.freemarker.model.AssignmentEndDirective;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroEndDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroInstance;

import freemarker.template.TemplateException;

public class AssignmentDirectiveTest extends AbstractDirectiveTest {

	private static final String TEST_FTL_FILE = "assign.txt.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testAssignFtl() throws IOException, TemplateException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", "Joe"); //$NON-NLS-1$ //$NON-NLS-2$
		model.put("counter", Integer.valueOf(0)); //$NON-NLS-1$
		validateFtlTemplate(TEST_FTL_FILE, model);
	}

	private static void testNestable(boolean expected, String contents) throws ParseException {
		Boolean nestable = AssignmentDirective.isNestable(contents, Directive.ASSIGN);
		assertEquals(expected, nestable.booleanValue());
	}

	public void testNestable() throws ParseException {
		testNestable(false, "assign x=y"); //$NON-NLS-1$
		testNestable(false, "assign\tx\n=\ry\r"); //$NON-NLS-1$
		testNestable(false, "assign  \"x x\"   = \"val\""); //$NON-NLS-1$
		testNestable(true, "assign x"); //$NON-NLS-1$
		testNestable(true, "assign x "); //$NON-NLS-1$
		testNestable(true, "assign \"x x\" "); //$NON-NLS-1$
		testNestable(true, "assign 'x x' "); //$NON-NLS-1$
	}

	public void testAssignModel() {
		Collection<Item> items = load();
		assertEquals(16, items.size());
		Iterator<Item> i = items.iterator();

		/* <#assign key="val"> */
		assertAssignment(i);

		/* <#assign seasons = ["winter", "spring", "summer", "autumn"]> */
		assertAssignment(i);

		/* <#assign counter = counter + 1> */
		assertAssignment(i);

		/* <#assign
		 *   days = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"]
		 *   counter = counter + 1
		 * > */
		assertAssignment(i);

		/* <#macro myMacro>foo</#macro> */
		/* <#assign formattedSeasons>
		 *   <#list seasons as s>
		 *     ${s} <@myMacro />
		 *   </#list>
		 * </#assign> */
		assertDirective(i, MacroDirective.class);
		assertDirective(i, MacroEndDirective.class);
		assertChildren(i, AssignmentDirective.class,
				ListDirective.class
		);
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				MacroInstance.class
		);
		assertInterpolation(i);
		assertDirective(i, MacroInstance.class);
		assertListEnd(i);
		assertDirective(i, AssignmentEndDirective.class);

		/* Number of words: ${formattedSeasons?word_list?size}
		 * ${formattedSeasons} */
		assertInterpolation(i);
		assertInterpolation(i);

		/* <#assign hello="Hello ${user}!">
		 * ${hello} */
		assertAssignment(i);
		assertInterpolation(i);
	}

	public void testAssignColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
		.directive(13) // <#assign key=
		.string(5) // "val"
		.directive(1) // >
		.text(1) // <whitespace>
		.directive(20) // <#assign seasons = [
		.string(8) // "winter"
		.directive(2) // ,
		.string(8) // "spring"
		.directive(2) // ,
		.string(8) // "summer"
		.directive(2) // ,
		.string(8) // "autumn"
		.directive(2) // ]>
		.text(1) // <whitespace>
		.directive(31) // <#assign counter = counter + 1>
		.text(1) // <whitespace>
		.directive(19) // <#assign   days = [
		.string(4) // "Mo"
		.directive(2) // ,
		.string(4) // "Tu"
		.directive(2) // ,
		.string(4) // "We"
		.directive(2) // ,
		.string(4) // "Th"
		.directive(2) // ,
		.string(4) // "Fr"
		.directive(2) // ,
		.string(4) // "Sa"
		.directive(2) // ,
		.string(4) // "Su"
		.directive(27) // ]   counter = counter + 1 >
		.text(1) // <whitespace>
		.directive(16) // <#macro myMacro>
		.text(3) // foo
		.directive(9) // </#macro>
		.text(1) // <whitespace>
		.directive(26) // <#assign formattedSeasons>
		.text(3) // <whitespace>
		.directive(20) // <#list seasons as s>
		.text(5) // <whitespace>
		.interpolation(4) // ${s}
		.text(1) // <whitespace>
		.directive(12) // <@myMacro />
		.text(3) // <whitespace>
		.directive(8) // </#list>
		.text(1) // <whitespace>
		.directive(10) // </#assign>
		.text(18) // Number of words:
		.interpolation(34) // ${formattedSeasons?word_list?size}
		.text(1) // <whitespace>
		.interpolation(19) // ${formattedSeasons}
		.text(1) // <whitespace>
		.directive(15) // <#assign hello=
		.string(7) // "Hello
		.interpolation(7) // ${user}
		.string(2) // !"
		.directive(1) // >
		.text(1) // <whitespace>
		.interpolation(8) // ${hello}
		.build();
		validateColoring(expected);
	}

}
