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

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class AssignmentDirective extends AbstractDirective {

	private AssignmentEndDirective endDirective;

	@SuppressWarnings("unused")
	private String type;

	public AssignmentDirective (String type) {
		this.type = type;
	}

	@Override
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	@Override
	public boolean isStartItem() {
		return super.isNestable();
	}

	@Override
	public void relateItem(Item directive) {
		if (directive instanceof AssignmentEndDirective)
			endDirective = (AssignmentEndDirective) directive;
	}

	@Override
	public boolean relatesToItem(Item directive) {
		return (directive instanceof AssignmentEndDirective);
	}

	@Override
	public boolean isNestable() {
		return (null != getContents() && !getContents().endsWith("/")); //$NON-NLS-1$
	}

	public AssignmentEndDirective getEndDirective() {
		return endDirective;
	}

	@Override
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			ArrayList<Item> l = new ArrayList<Item>();
			if (null != endDirective)
				l.add(endDirective);
			relatedItems = l.toArray(new Item[l.size()]);
		}
		return relatedItems;
	}
	private Item[] relatedItems;

	@Override
	public String getTreeImage() {
		return "assign.png"; //$NON-NLS-1$
	}

	Map<String, Class<?>> contextValues;
	@Override
	public void addToContext(Map<String, Class<?>> context) {
		if (null == contextValues) {
			String[] values = splitContents();
			String key = null;
			String value = null;
			if (values.length >= 2) key = values[1];
			if (values.length >= 4) value = values[3];
			Class<?> valueClass = null;
			if (null != value && value.length() > 0) {
				if (value.charAt(0) == '\"') valueClass = String.class;
				else if (Character.isDigit(value.charAt(0))) valueClass = Number.class;
				else {
					CompletionInterpolation completionInterpolation =
						new CompletionInterpolation("${" + value, 2, getItemSet(), getResource()); //$NON-NLS-1$
					valueClass = completionInterpolation.getReturnClass(context);
				}
			}
			if (null != key) {
				context.put(key, valueClass);
			}
		}
		super.addToContext(context);
	}

	@Override
	public Item getEndItem() {
		return endDirective;
	}
}