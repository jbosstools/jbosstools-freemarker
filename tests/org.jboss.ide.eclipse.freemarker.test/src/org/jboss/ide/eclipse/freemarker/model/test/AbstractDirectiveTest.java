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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.editor.coloring.test.StyleRangeArrayBuilder;
import org.jboss.ide.eclipse.freemarker.model.AssignmentDirective;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.IfEndDirective;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ListEndDirective;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

public abstract class AbstractDirectiveTest extends TestCase {

	public static final String TEST_PROJECT = "testEditor"; //$NON-NLS-1$
	public static final String TEST_DIRECTORY = "model"; //$NON-NLS-1$

	protected IProject project;
	protected Editor editor;

	@Override
	protected void setUp() throws Exception {
		this.project = ResourcesUtils.importProject(Activator.PLUGIN_ID,"projects/"+TEST_PROJECT); //$NON-NLS-1$
		IEditorPart part = WorkbenchUtils.openEditor(TEST_PROJECT + IPath.SEPARATOR + TEST_DIRECTORY + IPath.SEPARATOR + getTestFileName());
		assertEquals(FreemarkerMultiPageEditor.class, part.getClass());
		FreemarkerMultiPageEditor multiEditor = (FreemarkerMultiPageEditor) part;
		this.editor = multiEditor.getEditor();
	}

	/**
	 * @return Test file name relative to test directory.
	 */
	protected abstract String getTestFileName();

	@Override
	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject(TEST_PROJECT);
	}

	protected Collection<Item> load() {
		editor.reconcileInstantly();
		return editor.getItemSet().getDirectiveRegions().values();
	}

	protected void validateColoring(StyleRange[] expected) {
		StyledText st = editor.getTextViewer().getTextWidget();
		StyleRange[] actual = st.getStyleRanges();
		if (!Arrays.equals(expected, actual)) {
			System.out.println(StyleRangeArrayBuilder.propose(actual, editor.getDocument()));
		}
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals("Mismatch at index "+ i, expected[i], actual[i]); //$NON-NLS-1$
		}
	}

	protected void assertAssignment(Iterator<Item> i) {
		assertDirective(i, AssignmentDirective.class);
	}

	protected void assertInterpolation(Iterator<Item> i) {
		assertDirective(i, Interpolation.class);
	}

	protected void assertGeneric(Iterator<Item> i) {
		assertDirective(i, GenericDirective.class);
	}


	protected void assertListEnd(Iterator<Item> i) {
		assertDirective(i, ListEndDirective.class);
	}

	protected void assertDirective(Iterator<Item> i, Class<?> cl) {
		Item item;
		item = i.next();
		assertEquals(cl, item.getClass());
		assertEquals(0, item.getChildItems().size());
	}

	protected void assertIfEnd(Iterator<Item> i) {
		assertDirective(i, IfEndDirective.class);
	}
	protected Item assertChildren(Iterator<Item> i, Class<?> cl, Class<?>... expectedChildren) {
		Item item;
		List<Item> childItems;
		item = i.next();
		assertEquals(cl, item.getClass());
		childItems = item.getChildItems();
		assertEquals(expectedChildren.length, childItems.size());
		for (int j = 0; j < expectedChildren.length; j++) {
			assertEquals("Child #"+ i +" found "+ childItems.get(j).getClass() + " expected "+expectedChildren[j], expectedChildren[j], childItems.get(j).getClass()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return item;
	}

}
