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
package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.model.Item;

public class IncludeHyperlink implements IHyperlink {

	private IFile file;
	private Item item;

	public IncludeHyperlink(Item item, IFile file) {
		this.item = item;
		this.file = file;
	}

	@Override
	public void open() {
		try {
			Plugin.getDefault()
					.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.openEditor(new FileEditorInput(file),
							FreemarkerMultiPageEditor.ID);
		} catch (PartInitException e) {
			Plugin.error(e);
		}
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return item.getRegion();
	}

	@Override
	public String getHyperlinkText() {
		return item.getName();
	}

	@Override
	public String getTypeLabel() {
		return Messages.IncludeHyperlink_TYPELABEL_INCLUDE_DECLARATION;
	}
}