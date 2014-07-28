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
import java.util.TreeMap;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ListDirective;

import freemarker.template.TemplateException;

@SuppressWarnings("nls")
public class InterpolationTest extends AbstractDirectiveTest {

	private static final String TEST_FTL_FILE = "interpolation.html.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testInterpolationFtl() throws IOException, TemplateException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", "Joe");
		model.put("url", "http://example.com");
		model.put("content", "Example Inc.");
		model.put("tag", "a");
		model.put("attribute", "href");
		model.put("value", "https://example.com");
		Map<String, Object> attributes = new TreeMap<String, Object>();
		attributes.put("href", "https://example.com");
		attributes.put("target", "_blank");
		model.put("attributes", attributes.entrySet());
		validateFtlTemplate(TEST_FTL_FILE, model);
	}

	public void testInterpolationModel() {
		Collection<Item> items = load();
		assertEquals(16, items.size());
		Iterator<Item> i = items.iterator();

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

	public void testInterpolationColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
		.xmlTag(4) // <h1>
		.text(8) // Welcome
		.interpolation(7) // ${user}
		.text(1) // !
		.xmlTag(5) // </h1>
		.text(1) // <whitespace>
		.xmlTag(29) // <p style="font-weight: bold">
		.text(19) // Our latest product:
		.xmlTag(4) // </p>
		.text(1) // <whitespace>
		.xmlTag(9) // <a href="
		.interpolation(6) // ${url}
		.xmlTag(2) // ">
		.interpolation(10) // ${content}
		.xmlComment(18) // <!-- comment 1 -->
		.xmlTag(4) // </a>
		.text(1) // <whitespace>
		.xmlTag(1) // <
		.interpolation(6) // ${tag}
		.xmlTag(1) // <whitespace>
		.interpolation(12) // ${attribute}
		.xmlTag(2) // ="
		.interpolation(8) // ${value}
		.xmlTag(2) // ">
		.interpolation(10) // ${content}
		.xmlTag(2) // </
		.interpolation(6) // ${tag}
		.xmlTag(1) // >
		.text(1) // <whitespace>
		.xmlTag(1) // <
		.interpolation(6) // ${tag}
		.xmlTag(1) // <whitespace>
		.directive(28) // <#list attributes as attrib>
		.interpolation(13) // ${attrib.key}
		.xmlTag(2) // ="
		.interpolation(15) // ${attrib.value}
		.xmlTag(2) // "
		.directive(8) // </#list>
		.xmlTag(1) // >
		.interpolation(10) // ${content}
		.xmlTag(2) // </
		.interpolation(6) // ${tag}
		.xmlTag(1) // >
		.text(2) // !
		.xmlComment(60) // <!-- <p style="font-weight:bold">Our latest product:</p> -->
		.text(1) // <whitespace>
		.xmlComment(5) // <!--
		.interpolation(10) // ${content}
		.xmlComment(4) // -->
		.text(1) // <whitespace>
		.comment(19) // <#-- ${content} -->
		.build();
		validateColoring(expected);
	}
}
