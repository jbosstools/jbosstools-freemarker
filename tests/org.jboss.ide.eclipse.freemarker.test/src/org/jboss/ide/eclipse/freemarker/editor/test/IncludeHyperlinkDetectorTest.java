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
package org.jboss.ide.eclipse.freemarker.editor.test;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.ide.eclipse.freemarker.editor.Editor;
import org.jboss.ide.eclipse.freemarker.editor.IncludeHyperlink;
import org.jboss.ide.eclipse.freemarker.editor.IncludeHyperlinkDetector;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.mockito.Mockito;

import junit.framework.TestCase;

public class IncludeHyperlinkDetectorTest extends TestCase{

	public void testDetectHyperlinks() {
				
		IFile file = Mockito.mock(IFile.class);
		Mockito.when(file.exists()).thenReturn(true);
		
		IContainer container = Mockito.mock(IContainer.class);
		Mockito.when(container.getFile(new Path("filename"))).thenReturn(file);

		IResource resource = Mockito.mock(IResource.class);
		Mockito.when(resource.getParent()).thenReturn(container);		

		Editor fe = Mockito.mock(Editor.class);
		ItemSet value = Mockito.mock(ItemSet.class);

		GenericDirective gd = Mockito.mock(GenericDirective.class);
		Mockito.when(gd.getName()).thenReturn("include");
		Mockito.when(gd.getResource()).thenReturn(resource);
		
		Mockito.when(gd.splitContents()).thenReturn(new String[] {"","\"filename\""});
		Mockito.when(value.getItem(0)).thenReturn(gd);
		Mockito.when(fe.getItemSet()).thenReturn(value);
		
		IncludeHyperlinkDetector detector =  new IncludeHyperlinkDetector(null,fe);
		IRegion region = Mockito.mock(IRegion.class);
		Mockito.when(region.getOffset()).thenReturn(0);
		IHyperlink[] hlinks = detector.detectHyperlinks(null, region , false);
		
		assertTrue("Shere should be always one link", hlinks.length ==1 );
		assertTrue("IncludeHyperlink instance expected", hlinks[0] instanceof IncludeHyperlink );
		
	}

}
