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
package org.jboss.ide.eclipse.freemarker.editor.test;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;
import org.jboss.ide.eclipse.freemarker.test.Activator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;

public class ErrorMarkerTest extends TestCase  {

    private static final String EMPTY_FTL = "empty.ftl"; //$NON-NLS-1$
    private static final String TEST_EDITOR_PROJECT = "testEditor"; //$NON-NLS-1$
    
    private Editor editor;

    @Override
    protected void setUp() throws Exception {
        ResourcesUtils.importProject(Activator.PLUGIN_ID,"projects/" + TEST_EDITOR_PROJECT); //$NON-NLS-1$
        IEditorPart editorPart = WorkbenchUtils.openEditor(TEST_EDITOR_PROJECT + IPath.SEPARATOR + EMPTY_FTL);
        assertTrue(editorPart instanceof FreemarkerMultiPageEditor);
        this.editor = ((FreemarkerMultiPageEditor) editorPart).getEditor();
    }

    @Override
    protected void tearDown() throws Exception {
        ResourcesUtils.deleteProject(TEST_EDITOR_PROJECT);
    }

    public void testMarkerPositions() throws CoreException {
        // The "*" in "${*}" will be marked as error.

        {
            IMarker marker = getErrorMarker("${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(2, MarkerUtilities.getCharStart(marker));
            assertEquals(3, MarkerUtilities.getCharEnd(marker));
        }
        
        {
            IMarker marker = getErrorMarker("\n\r\n\r${*}");
            assertEquals(4, MarkerUtilities.getLineNumber(marker));
            assertEquals(6, MarkerUtilities.getCharStart(marker));
            assertEquals(7, MarkerUtilities.getCharEnd(marker));
        }
        
        // Tabs are tricky because FreeMarker reports column numbers assuming tab width 8:
        {
            IMarker marker = getErrorMarker("\t${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(3, MarkerUtilities.getCharStart(marker));
            assertEquals(4, MarkerUtilities.getCharEnd(marker));
        }
        {
            IMarker marker = getErrorMarker(" \t${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(4, MarkerUtilities.getCharStart(marker));
            assertEquals(5, MarkerUtilities.getCharEnd(marker));
        }
        {
            IMarker marker = getErrorMarker("       \t${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(10, MarkerUtilities.getCharStart(marker));
            assertEquals(11, MarkerUtilities.getCharEnd(marker));
        }
        {
            IMarker marker = getErrorMarker("\t\t${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(4, MarkerUtilities.getCharStart(marker));
            assertEquals(5, MarkerUtilities.getCharEnd(marker));
        }
        {
            IMarker marker = getErrorMarker("\t  \t${*}");
            assertEquals(1, MarkerUtilities.getLineNumber(marker));
            assertEquals(6, MarkerUtilities.getCharStart(marker));
            assertEquals(7, MarkerUtilities.getCharEnd(marker));
        }
        
        // Strange case (parser bug?) where the error column is after the last character, so we must omit offset info:
        {
            IMarker marker = getErrorMarker("\n${#");
            assertEquals(2, MarkerUtilities.getLineNumber(marker));
            assertEquals(-1, MarkerUtilities.getCharStart(marker));
            assertEquals(-1, MarkerUtilities.getCharEnd(marker));
        }
    }

    protected IMarker getErrorMarker(String editorContent) throws CoreException {
        editor.getDocument().set(editorContent);
        editor.reconcileInstantly();
        
        IResource resource = ResourceUtil.getResource(editor.getEditorInput());
        IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        
        assertEquals(1, markers.length);
        return markers[0];
    }

}
