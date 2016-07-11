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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.ide.eclipse.freemarker.model.AssignmentDirective;
import org.jboss.ide.eclipse.freemarker.model.AssignmentEndDirective;
import org.jboss.ide.eclipse.freemarker.model.ElseDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.IfDirective;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemsDirective;
import org.jboss.ide.eclipse.freemarker.model.ItemsEndDirective;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;
import org.jboss.ide.eclipse.freemarker.model.SepDirective;
import org.jboss.ide.eclipse.freemarker.model.SepEndDirective;
import org.jboss.ide.eclipse.freemarker.test.FreemarkerTestUtils;
import org.junit.Assert;

import freemarker.template.TemplateException;

public class ListDirectiveTest extends AbstractDirectiveTest {

	public static final String TEST_FTL_FILE = "list.txt.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testListFtl() throws IOException, TemplateException {
		FreemarkerTestUtils.validateFtlTemplate(new File(project.getFile(TEST_DIRECTORY).getLocationURI()), TEST_FTL_FILE);
	}

	public void testListModel() {
		Collection<Item> items = load();
		assertEquals(42, items.size());
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

		/* <#list 1..3>
		 *   Items:
		 *   <#items as n>
		 *   	${n}<#sep>,
		 *   </#items>
		 * <#else>
		 *   No items 
		 * </#list>
		 */
		Item listDirective = assertChildren(i, ListDirective.class,
				ItemsDirective.class,
				ElseDirective.class
		);
		assertChildren(i, ItemsDirective.class,
				Interpolation.class,
				SepDirective.class);
		assertInterpolation(i);
		assertDirective(i, SepDirective.class);
		assertDirective(i, ItemsEndDirective.class);
		Item elseDirective = assertDirective(i, ElseDirective.class);
		Item listEndDirective = assertListEnd(i);
		
		Item[] relatedItems = new Item[] { listDirective, elseDirective, listEndDirective };
		Assert.assertArrayEquals(relatedItems, listDirective.getRelatedItems());
		Assert.assertArrayEquals(relatedItems, elseDirective.getRelatedItems());
		Assert.assertArrayEquals(relatedItems, listEndDirective.getRelatedItems());
		
		/* 
		 * <#list 1..3 as x>${x}<#sep>, </#list>
		 */
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				SepDirective.class);
		assertInterpolation(i);
		assertDirective(i, SepDirective.class);
		assertListEnd(i);
		
		/* 
		 * <#list 1..3 as x>
		 *   <div>${x}<#sep>,</#sep></div>
		 * </#list>
		 */		
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				SepDirective.class);
		assertInterpolation(i);
		assertDirective(i, SepDirective.class);
		assertDirective(i, SepEndDirective.class);
		assertListEnd(i);
	}

}
