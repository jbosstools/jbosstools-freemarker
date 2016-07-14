/*
 * JBoss by Red Hat
 * Copyright 2006-2015, Red Hat Middleware, LLC, and individual contributors as indicated
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyleRange;

@SuppressWarnings("nls")
public class EditInterpolationColoringTest extends AbstractColoringTest {

	private static final String TEST_FTL_FILE = "edit-interpolation.ftl";

	@Override
	protected String getTestTemplateName() {
		return TEST_FTL_FILE;
	}

	/**
	 * Tests a single character insert operation.
	 *
	 * @throws BadLocationException
	 */
	public void testInterpolationColoringEdit() throws BadLocationException {
		IDocument doc = editor.getDocument();
		/* make ${ab} out of ${a} */
		doc.replace(3, 0, "b");
		String docText = doc.get();
		/* make sure we have inserted the "1" where we wanted */
		assertTrue("Editor text should be \"${ab}\"; found: "+ docText, docText.equals("${ab}"));
		/* make sure the coloring gets applied instantly */
		editor.reconcileInstantly();

		StyleRange[] expected = new StyleRangeArrayBuilder()
				.interpolation(2) // ${
				.variable(2) // ab
				.interpolation(1) // }
				.build();
		validateColoring(expected);
	}

	/**
	 * Tests the initial coloring juts after the template was opened.
	 *
	 * @throws BadLocationException
	 */
	public void testInterpolationColoringInitial() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.interpolation(2) // ${
				.variable(1) // a
				.interpolation(1) // }
				.build();
		validateColoring(expected);
	}

}
