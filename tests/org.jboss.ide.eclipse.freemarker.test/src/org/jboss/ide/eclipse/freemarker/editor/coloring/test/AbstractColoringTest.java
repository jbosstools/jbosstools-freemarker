package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.model.test.AbstractDirectiveTest;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.junit.Assert;
import org.osgi.service.prefs.BackingStoreException;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public abstract class AbstractColoringTest extends TestCase {

	public static final String TEST_DIRECTORY = "coloring";
	
	protected Editor editor;
	protected IProject project;
	protected FreemarkerMultiPageEditor multiEditor;

	protected String getTestDirectoryName() {
		return TEST_DIRECTORY;
	}

	/**
	 * @return Test FTL file name relative to test directory.
	 */
	protected abstract String getTestTemplateName();

	/**
	 * @return Test project name that should exist under {@code projects} directory of this maven project.
	 */
	protected String getTestProjectName() {
		return AbstractDirectiveTest.TEST_PROJECT;
	}

	private static final String DIRECTIVE_COLOR = "1,200,1";
	
	@Override
	protected void setUp() throws Exception {
		this.project = ResourcesUtils.importProject(Activator.PLUGIN_ID,
				"projects/" + getTestProjectName()); //$NON-NLS-1$

		 // Ensure that we don't re-use an editor with the old color preferences
		WorkbenchUtils.closeAllEditors();
		
		setUpColorPreferences();
		
		Assert.assertEquals("Configuring color preferences had no effect.",
				DIRECTIVE_COLOR, Preferences.getInstance().getString(PreferenceKey.COLOR_DIRECTIVE));
			
		IEditorPart part = WorkbenchUtils.openEditor(getTestProjectName()
				+ IPath.SEPARATOR + getTestDirectoryName() + IPath.SEPARATOR
				+ getTestTemplateName());
		assertEquals(FreemarkerMultiPageEditor.class, part.getClass());
		this.multiEditor = (FreemarkerMultiPageEditor) part;
		this.editor = multiEditor.getEditor();
		this.editor.reconcileInstantly();
		Assert.assertEquals("S2 Configuring color preferences had no effect.",
				DIRECTIVE_COLOR, Preferences.getInstance().getString(PreferenceKey.COLOR_DIRECTIVE));
	}

	/**
	 * Ensure that the syntax highlight color of each kind of parts is unique,
	 * as we will identify them based on the text color.
	 */
	private void setUpColorPreferences() throws BackingStoreException {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Plugin.ID);
		prefs.put(PreferenceKey.COLOR_DIRECTIVE.toString(), DIRECTIVE_COLOR);
		prefs.put(PreferenceKey.COLOR_INTERPOLATION.toString(), "255,2,128");
		prefs.put(PreferenceKey.COLOR_COMMENT.toString(), "163,164,165");
		prefs.put(PreferenceKey.COLOR_TEXT.toString(), "4,4,4");
		prefs.put(PreferenceKey.COLOR_VARIABLE.toString(), "5,128,255");
		prefs.put(PreferenceKey.COLOR_KEYWORD.toString(), "6,6,255");
		prefs.put(PreferenceKey.COLOR_STRING.toString(), "7,255,255");
		prefs.put(PreferenceKey.COLOR_OTHER_EXP_PART.toString(), "63,64,65");
		prefs.put(PreferenceKey.COLOR_XML_TAG.toString(), "8,8,255");
		prefs.put(PreferenceKey.COLOR_XML_COMMENT.toString(), "166,222,168");
		// Turn off related tag highlighting to not interfere:
		prefs.put(PreferenceKey.HIGHLIGHT_RELATED_ITEMS.toString(), "false");
		prefs.flush();
	}

	@Override
	protected void tearDown() throws Exception {
		editor.doRevertToSaved();
		ResourcesUtils.deleteProject(getTestProjectName());
	}

	protected void validateColoring(StyleRange[] expected) {
		StyledText st = editor.getTextViewer().getTextWidget();
		
		StyleRange[] actual = st.getStyleRanges();
		mapStyleRangePositions(actual, getEOLNormalizationIndexMapping(st.getText()));
		
		if (!Arrays.equals(expected, actual)) {
			System.out.println();
			System.out.println(this.getClass() + " expected coloring:");
			System.out.println(StyleRangeArrayBuilder.propose(actual, editor.getDocument()));
			System.out.println();
		}
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals("Mismatch at index " + i, expected[i], actual[i]); //$NON-NLS-1$
		}
	}

	private void mapStyleRangePositions(StyleRange[] actual, int[] indexMapping) {
		for (int i = 0; i < actual.length; i++) {
			StyleRange origRange = actual[i];
			int origFirst = origRange.start;
			int origLast = origRange.start + origRange.length - 1; // inclusive end
			int mappedFirst = mapIndex(origFirst, indexMapping);
			int mappedLast = mapIndex(origLast, indexMapping);
			if (mappedFirst != origFirst || mappedLast != origLast) {
				StyleRange mappedRange = (StyleRange) origRange.clone();
				mappedRange.start = mappedFirst;
				mappedRange.length = mappedLast - mappedFirst + 1;
				actual[i] = mappedRange;
			}
		}
	}
	
	private int mapIndex(int index, int[] indexMapping) {
		if (index < 0) {
			return index;
		}
		if (index >= indexMapping.length) {
			if (indexMapping.length == 0) {
				return index;
			}
			return indexMapping[indexMapping.length - 1] + (index - indexMapping.length + 1);
		}
		return indexMapping[index];
	}

	/**
	 * Returns an index mapping the maps character indexes of the argument text to the indexes we had if the text uses
	 * LF for line breaks (rather than CR LF). Thus the test tolerates if the sample templates are converted to use
	 * CRLF.
	 */
	private int[] getEOLNormalizationIndexMapping(String text) {
		int len = text.length();
		int[] indexMapping = new int[len];
		int dstIdx = 0;
		for (int i = 0; i < len; i++) {
			indexMapping[i] = dstIdx;
			if (!(text.charAt(i) == '\r' && i + 1 < len && text.charAt(i + 1) == '\n')) {
				dstIdx++;
			}
		}
		return indexMapping;
	}

}
