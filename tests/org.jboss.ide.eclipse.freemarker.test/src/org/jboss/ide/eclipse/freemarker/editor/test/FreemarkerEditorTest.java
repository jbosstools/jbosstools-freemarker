package org.jboss.ide.eclipse.freemarker.editor.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.jboss.ide.eclipse.freemarker.editor.CompletionProcessor;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.model.FunctionDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.outline.OutlineContentProvider;
import org.jboss.ide.eclipse.freemarker.outline.OutlinePage;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class FreemarkerEditorTest extends TestCase {

	private static final String MACRO_TXT_FTL = "macro.txt.ftl"; //$NON-NLS-1$
	private static final String EDITPROPERTY_XHTML_FTL = "editproperty.xhtml.ftl"; //$NON-NLS-1$
	private static final String TEST_EDITOR_PROJECT = "testEditor"; //$NON-NLS-1$

	@Override
	protected void setUp() throws Exception {
		ResourcesUtils.importProject(Activator.PLUGIN_ID,"projects/"+TEST_EDITOR_PROJECT); //$NON-NLS-1$
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject(TEST_EDITOR_PROJECT);
	}

	public void testFreemarkerEditorIsOpened() {
		IEditorPart part = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + EDITPROPERTY_XHTML_FTL);
		assertTrue(part instanceof FreemarkerMultiPageEditor);
	}

	public void testFreemarkerContentAssistIsShowed() {
		IEditorPart part = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + EDITPROPERTY_XHTML_FTL);
		assertTrue(part instanceof FreemarkerMultiPageEditor);
		FreemarkerMultiPageEditor editor = (FreemarkerMultiPageEditor)part;
		CompletionProcessor proc = new CompletionProcessor(editor.getEditor());
		IFindReplaceTarget find = (IFindReplaceTarget)editor.getAdapter(IFindReplaceTarget.class);
		int pos = find.findAndSelect(0, "<#", true, false, false); //$NON-NLS-1$
		ICompletionProposal[] proposals = proc.computeCompletionProposals(editor.getEditor().getTextViewer(),pos+2);
		assertTrue(proposals.length>0);

		pos = find.findAndSelect(0, "label(componentProperty.name)", true, false, false); //$NON-NLS-1$
		proposals = proc.computeCompletionProposals(editor.getEditor().getTextViewer(),pos);
		assertTrue(proposals.length>0);
	}

	public void testFreemarkerOutline() throws BackingStoreException {

		/* this pref is important for the items.length check to be what we expect */
		Preferences prefs = InstanceScope.INSTANCE.getNode(org.jboss.ide.eclipse.freemarker.Plugin.ID);
		prefs.put(PreferenceKey.OUTLINE_LEVEL_OF_DETAIL.toString(), OutlineContentProvider.OutlineLevelOfDetail.functionAndMacroDefinitions.name());
		prefs.flush();

		IEditorPart part = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + MACRO_TXT_FTL);
		assertEquals(FreemarkerMultiPageEditor.class, part.getClass());
		FreemarkerMultiPageEditor multiEditor = (FreemarkerMultiPageEditor) part;
		Editor editor = multiEditor.getEditor();
		editor.reconcileInstantly();
		OutlinePage outline = editor.getOutlinePage();
		TreeItem[] items = outline.getTreeViewer().getTree().getItems();
		assertEquals(4, items.length);
		int i = 0;
		assertEquals(MacroDirective.class, items[i++].getData().getClass());
		assertEquals(FunctionDirective.class, items[i++].getData().getClass());
		assertEquals(MacroDirective.class, items[i++].getData().getClass());
		assertEquals(FunctionDirective.class, items[i++].getData().getClass());
	}

}
