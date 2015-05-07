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
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class GenericNestableDirective extends AbstractDirective {

	private String image;
	private String name;
	private GenericNestableEndDirective endDirective;

	public GenericNestableDirective (ItemSet itemSet, String name, String image) {
		super(itemSet);
		this.name = name;
		this.image = image;
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) {
	}

	@Override
	public String getTreeImage() {
		return image;
	}

	@Override
	public Item getStartItem() {
		return this;
	}

	@Override
	public boolean isNestable() {
		return !getContents().trim().endsWith("/"); //$NON-NLS-1$
	}

	@Override
	public boolean isStartItem() {
		return true;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		if (directive instanceof GenericNestableEndDirective) {
			return ((GenericNestableEndDirective) directive).getName().equals(name);
		}
		else return false;
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof GenericNestableEndDirective) {
			endDirective = (GenericNestableEndDirective) directive;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Item getEndItem() {
		return endDirective;
	}

	private Item[] relatedItems;
	@Override
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			if (null != endDirective)
				relatedItems = new Item[]{endDirective};
			else
				relatedItems = new Item[0];
		}
		return relatedItems;
	}
}