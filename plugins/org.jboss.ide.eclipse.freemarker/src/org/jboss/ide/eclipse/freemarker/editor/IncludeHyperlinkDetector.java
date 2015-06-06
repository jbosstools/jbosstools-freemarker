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
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.jboss.ide.eclipse.freemarker.model.GenericDirective;
import org.jboss.ide.eclipse.freemarker.model.Item;

public class IncludeHyperlinkDetector implements IHyperlinkDetector {

	private Editor editor;
	public IncludeHyperlinkDetector(ITextViewer textViewer, Editor editor) {
		this.editor = editor;
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		Item item = editor.getItemSet().getItem(region.getOffset());
		if (item instanceof GenericDirective) {
			GenericDirective genericDirective = (GenericDirective) item;
			if ("include".equals(genericDirective.getName())) { //$NON-NLS-1$
				String[] splitContents = genericDirective.splitContents();
				if (splitContents.length>1) {
					String text = splitContents[1].replace("\"", "");  //$NON-NLS-1$//$NON-NLS-2$
					IFile file = genericDirective.getResource().getParent().getFile(new Path(text));
					if (file.exists()) {
						return new IHyperlink[]{new IncludeHyperlink(item, file)};
					}
				}
			}
		}
		return null;
	}

	public void init (ITextViewer viewer) {
	}
}