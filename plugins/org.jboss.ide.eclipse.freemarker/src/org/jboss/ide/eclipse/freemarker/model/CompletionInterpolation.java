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
package org.jboss.ide.eclipse.freemarker.model;

import org.eclipse.core.resources.IResource;

public class CompletionInterpolation extends Interpolation {

	private String contents;
	private int offset;

	public CompletionInterpolation (ItemSet itemSet, String contents, int offset, IResource resource) {
		super(itemSet);
		this.contents = contents + "}"; //$NON-NLS-1$
		this.offset = offset;
		setResource(resource);
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public String getFullContents() {
		return contents;
	}

	@Override
	public int getLength() {
		return contents.length();
	}
}