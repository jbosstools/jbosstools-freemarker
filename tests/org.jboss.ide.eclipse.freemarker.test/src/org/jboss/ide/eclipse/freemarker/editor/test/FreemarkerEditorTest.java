package org.jboss.ide.eclipse.freemarker.editor.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.jboss.ide.eclipse.freemarker.editor.CompletionProcessor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

public class FreemarkerEditorTest extends TestCase {

	
	
	private static final String EDITPROPERTY_XHTML_FTL = "editproperty.xhtml.ftl";
	private static final String TEST_EDITOR_PROJECT = "testEditor";

	protected void setUp() throws Exception {
		ResourcesUtils.importProject(Activator.PLUGIN_ID,"projects/"+TEST_EDITOR_PROJECT);
	}

	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject(TEST_EDITOR_PROJECT);
	}

	public void testFreemarkerEditorIsOpened() {
		IEditorPart part = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + EDITPROPERTY_XHTML_FTL);
		assertTrue(part instanceof FreemarkerMultiPageEditor);
		FreemarkerMultiPageEditor editor = (FreemarkerMultiPageEditor)part;
	}

	public void testFreemarkerContentAssistIsShowed() {
		IEditorPart part = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + EDITPROPERTY_XHTML_FTL);
		assertTrue(part instanceof FreemarkerMultiPageEditor);
		FreemarkerMultiPageEditor editor = (FreemarkerMultiPageEditor)part;
		CompletionProcessor proc = new CompletionProcessor(editor.getEditor());
		IFindReplaceTarget find = (IFindReplaceTarget)editor.getAdapter(IFindReplaceTarget.class);
		int pos = find.findAndSelect(0, "<#", true, false, false);
		ICompletionProposal[] proposals = proc.computeCompletionProposals(editor.getEditor().getTextViewer(),pos+2);
		assertTrue(proposals.length>0);
		
		pos = find.findAndSelect(0, "label(componentProperty.name)", true, false, false);
		proposals = proc.computeCompletionProposals(editor.getEditor().getTextViewer(),pos);
		assertTrue(proposals.length>0);
	}
}
