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
import org.jboss.ide.eclipse.freemarker.model.AssignmentDirective;
import org.jboss.ide.eclipse.freemarker.model.AssignmentEndDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.IfDirective;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;

import freemarker.template.TemplateException;

public class ListDirectiveTest extends AbstractDirectiveTest {

	private static final String TEST_FTL_FILE = "list.txt.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testListFtl() throws IOException, TemplateException {
		Map<String, Object> model = new HashMap<String, Object>();
		validateFtlTemplate(TEST_FTL_FILE, model);
	}

	public void testListModel() {
		Collection<Item> items = load();
		assertEquals(26, items.size());
		Iterator<Item> i = items.iterator();

		assertAssignment(i);

		/* <#list seq as x>
		 *   ${x_index + 1}. ${x}<#if x_has_next>,</#if>
		 * </#list>  */
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				Interpolation.class,
				IfDirective.class
		);
		assertInterpolation(i);
		assertInterpolation(i);
		assertChildren(i, IfDirective.class);
		assertIfEnd(i);
		assertListEnd(i);

		/* <#list 1..3 as n>
		 *   <#list 1..3 as m>
		 *     list item #${n}x${m}
		 *   </#list>
		 * </#list> */
		assertChildren(i, ListDirective.class,
				ListDirective.class
		);
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				Interpolation.class
		);
		assertInterpolation(i);
		assertInterpolation(i);
		assertListEnd(i);
		assertListEnd(i);

		/* <#list seq as x>
		 *   ${x}
		 *   <#if x = "spring"><#break></#if>
		 * </#list>
 */

		assertChildren(i, ListDirective.class,
				Interpolation.class,
				IfDirective.class
		);
		assertInterpolation(i);
		assertChildren(i, IfDirective.class,
				GenericDirective.class
		);
		assertGeneric(i);
		assertIfEnd(i);
		assertListEnd(i);

		/* <#assign x>
		 *   <#list 1..3 as n>
		 *     list item #${n}
		 *   </#list>
		 * </#assign> */
		assertChildren(i, AssignmentDirective.class,
				ListDirective.class
		);
		assertChildren(i, ListDirective.class,
				Interpolation.class
		);
		assertInterpolation(i);
		assertListEnd(i);
		assertDirective(i, AssignmentEndDirective.class);

		/* Number of words: ${x?word_list?size}
		 * ${x} */
		assertInterpolation(i);
		assertInterpolation(i);
	}

	public void testListColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
		.directive(16) // <#assign seq = [
		.string(8) // "winter"
		.directive(2) // ,
		.string(8) // "spring"
		.directive(2) // ,
		.string(8) // "summer"
		.directive(2) // ,
		.string(8) // "autumn"
		.directive(2) // ]>
		.text(1) // <whitespace>
		.directive(16) // <#list seq as x>
		.text(3) // <whitespace>
		.interpolation(14) // ${x_index + 1}
		.text(2) // .
		.interpolation(4) // ${x}
		.directive(16) // <#if x_has_next>
		.text(1) // ,
		.directive(6) // </#if>
		.text(1) // <whitespace>
		.directive(8) // </#list>
		.text(3) // <whitespace>
		.directive(17) // <#list 1..3 as n>
		.text(3) // <whitespace>
		.directive(17) // <#list 1..3 as m>
		.text(16) // list item #
		.interpolation(4) // ${n}
		.text(1) // x
		.interpolation(4) // ${m}
		.text(3) // <whitespace>
		.directive(8) // </#list>
		.text(1) // <whitespace>
		.directive(8) // </#list>
		.text(2) // <whitespace>
		.directive(16) // <#list seq as x>
		.text(3) // <whitespace>
		.interpolation(4) // ${x}
		.text(3) // <whitespace>
		.directive(9) // <#if x =
		.string(8) // "spring"
		.directive(1) // >
		.directive(8) // <#break>
		.directive(6) // </#if>
		.text(1) // <whitespace>
		.directive(8) // </#list>
		.text(2) // <whitespace>
		.directive(11) // <#assign x>
		.text(3) // <whitespace>
		.directive(17) // <#list 1..3 as n>
		.text(16) // list item #
		.interpolation(4) // ${n}
		.text(3) // <whitespace>
		.directive(8) // </#list>
		.text(1) // <whitespace>
		.directive(10) // </#assign>
		.text(18) // Number of words:
		.interpolation(19) // ${x?word_list?size}
		.text(1) // <whitespace>
		.interpolation(4) // ${x}
		.build();
		validateColoring(expected);
	}
}
