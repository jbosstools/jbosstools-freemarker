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
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.Directive;


public class ItemFactory {

	public static Item getItem(ItemSet itemSet, ITypedRegion region, ISourceViewer viewer, IResource resource) {
		if (null == region) {
			return null;
		}
		else {
			Item directive = null;
			String type = region.getType();
			Directive directiveType = Directive.fastValueOf(type);
			if (directiveType != null) {
				directive = directiveType.createModelItem(itemSet);
			}
			else {
				PartitionType partitionType = PartitionType.fastValueOf(type);
				switch (partitionType) {
				case DOLLAR_INTERPOLATION:
				case HASH_INTERPOLATION:
					directive = new Interpolation(itemSet);
					break;
				case MACRO_INSTANCE_START:
					directive = new MacroInstance(itemSet);
					break;
				case MACRO_INSTANCE_END:
					directive = new MacroEndInstance(itemSet);
					break;
				case DIRECTIVE_END:
				case DIRECTIVE_START:
					/* DIRECTIVE_START and DIRECTIVE_END should never happen here as
					 * all directive regions should return a non null directive
					 * from Directive.fastValueOf(type) which is checked earlier
					 * in this method */
				case COMMENT:
				case TEXT:
					/* COMMENT and TEXT have no representation in an ItemSet */
				default:
					break;
				}
			}

			if (null != directive) {
				directive.load(region, viewer, resource);
			}
			return directive;
		}
	}

}