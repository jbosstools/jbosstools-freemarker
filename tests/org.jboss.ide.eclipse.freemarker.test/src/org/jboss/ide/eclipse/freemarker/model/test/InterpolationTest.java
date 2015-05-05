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

import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;
import org.jboss.ide.eclipse.freemarker.test.FreemarkerTestUtils;

import freemarker.template.TemplateException;

public class InterpolationTest extends AbstractDirectiveTest {

	public static final String TEST_FTL_FILE = "interpolation.html.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testInterpolationFtl() throws IOException, TemplateException {
		FreemarkerTestUtils.validateFtlTemplate(new File(project.getFile(TEST_DIRECTORY).getLocationURI()), TEST_FTL_FILE);
	}

	public void testInterpolationModel() {
		Collection<Item> items = load();
		assertEquals(17, items.size());
		Iterator<Item> i = items.iterator();
		/* <#assign attributes = {"href":"https://example.com","target":"_blank"}> */
		assertAssignment(i);
		/* <h1>Welcome ${user}!</h1> */
		assertInterpolation(i);

		/* <p style="font-weight: bold">Our latest product:</p>
		 * <a href="${url}">${content}<!-- comment 1 --></a> */
		assertInterpolation(i);
		assertInterpolation(i);

		/* <${tag} ${attribute}="${value}">${content}</${tag}> */
		assertInterpolation(i);
		assertInterpolation(i);
		assertInterpolation(i);
		assertInterpolation(i);
		assertInterpolation(i);

		/* <${tag} <#list attributes as attrib>${attrib.key}="${attrib.value}"</#list>>${content}</${tag}>! */
		assertInterpolation(i);
		assertChildren(i, ListDirective.class,
				Interpolation.class,
				Interpolation.class
		);
		assertInterpolation(i);
		assertInterpolation(i);
		assertListEnd(i);
		assertInterpolation(i);
		assertInterpolation(i);
		assertInterpolation(i);

	}
}
