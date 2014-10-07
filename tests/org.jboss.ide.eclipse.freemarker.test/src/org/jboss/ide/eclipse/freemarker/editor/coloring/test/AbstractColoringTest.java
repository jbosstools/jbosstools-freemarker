package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.ide.eclipse.freemarker.test.FreemarkerTestUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

import freemarker.template.TemplateException;

@SuppressWarnings("nls")
public abstract class AbstractColoringTest extends TestCase {

	public static final String TEST_DIRECTORY = "coloring";
	protected Editor editor;
	protected IProject project;
	protected FreemarkerMultiPageEditor multiEditor;

	/**
	 * @return test 
	 */
	protected abstract String getTestDirectoryName();

	/**
	 * @return Test FTL file name relative to test directory.
	 */
	protected abstract String getTestTemplateName();

	/**
	 * @return Test project name that should exist under {@code projects} directory of this maven project.
	 */
	protected abstract String getTestProjectName();

	@Override
	protected void setUp() throws Exception {
		this.project = ResourcesUtils.importProject(Activator.PLUGIN_ID,
				"projects/" + getTestProjectName()); //$NON-NLS-1$
		IEditorPart part = WorkbenchUtils.openEditor(getTestProjectName()
				+ IPath.SEPARATOR + getTestDirectoryName() + IPath.SEPARATOR
				+ getTestTemplateName());
		assertEquals(FreemarkerMultiPageEditor.class, part.getClass());
		this.multiEditor = (FreemarkerMultiPageEditor) part;
		this.editor = multiEditor.getEditor();
		this.editor.reconcileInstantly();
	}

	@Override
	protected void tearDown() throws Exception {
		editor.doRevertToSaved();
		ResourcesUtils.deleteProject(getTestProjectName());
	}

	protected void validateColoring(StyleRange[] expected) {
		StyledText st = editor.getTextViewer().getTextWidget();
		StyleRange[] actual = st.getStyleRanges();
		if (!Arrays.equals(expected, actual)) {
			System.out.println(StyleRangeArrayBuilder.propose(actual,
					editor.getDocument()));
		}
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals("Mismatch at index " + i, expected[i], actual[i]); //$NON-NLS-1$
		}
	}

	/**
	 * Tests the validity of the template given by {@code get*Name()} methods.
	 * Expects {@code &lt;template.ftl&gt;.model.properties} and {@code &lt;template.ftl&gt;.expected.txt} files to exist.
	 *
	 * @throws IOException
	 * @throws TemplateException
	 */
	public void testFtl() throws IOException, TemplateException {
		FreemarkerTestUtils.validateFtlTemplate(
				new File(project.getFile(getTestDirectoryName())
						.getLocationURI()), getTestTemplateName());
	}
}
