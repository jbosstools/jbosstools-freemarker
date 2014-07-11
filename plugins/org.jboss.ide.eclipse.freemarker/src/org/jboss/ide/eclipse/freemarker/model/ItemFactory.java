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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.lang.Directive;


public class ItemFactory {

	public static Item getItem (ItemSet itemSet, ITypedRegion region, ISourceViewer viewer, IResource resource) {
		if (null == region) {
			return null;
		}
		else {
			Directive directiveType = Directive.fastValueOf(region.getType());
			if (directiveType == null) {
				/* ignore non-directives */
				return null;
			}
			else {
				Item directive = null;
				switch (directiveType) {
				case __ftl_if_directive_start:
					directive = new IfDirective(itemSet);
					break;
				case __ftl_if_directive_end:
					directive = new IfEndDirective(itemSet);
					break;
				case __ftl_if_else_directive:
					directive = new IfElseDirective(itemSet);
					break;
				case __ftl_else_if_directive:
					directive = new ElseIfDirective(itemSet);
					break;
				case __ftl_list_directive_start:
					directive = new ListDirective(itemSet);
					break;
				case __ftl_list_directive_end:
					directive = new ListEndDirective(itemSet);
					break;
				case __ftl_function_directive_start:
					directive = new FunctionDirective(itemSet);
					break;
				case __ftl_function_directive_end:
					directive = new FunctionEndDirective(itemSet);
					break;
				case __ftl_macro_directive_start:
					directive = new MacroDirective(itemSet);
					break;
				case __ftl_macro_directive_end:
					directive = new MacroEndDirective(itemSet);
					break;
				case __ftl_macro_instance_start:
					directive = new MacroInstance(itemSet);
					break;
				case __ftl_macro_instance_end:
					directive = new MacroEndInstance(itemSet);
					break;
				case __ftl_include:
					directive = new GenericDirective(itemSet, "include.png"); //$NON-NLS-1$
					break;
				case __ftl_import:
					directive = new GenericDirective(itemSet, "import.png"); //$NON-NLS-1$
					break;
				case __ftl_assign:
				case __ftl_local:
				case __ftl_global:
					directive = new AssignmentDirective(itemSet, directiveType);
					break;
				case __ftl_assign_end:
				case __ftl_local_end:
				case __ftl_global_end:
					directive = new AssignmentEndDirective(itemSet, region.getType());
					break;
				case __ftl_break:
					directive = new GenericDirective(itemSet, "break.png"); //$NON-NLS-1$
					break;
				case __ftl_nested:
					directive = new GenericDirective(itemSet, "nested.png"); //$NON-NLS-1$
					break;
				case __ftl_stop:
					directive = new GenericDirective(itemSet, "stop.png"); //$NON-NLS-1$
					break;
				case __ftl_return:
					directive = new GenericDirective(itemSet, "return.png"); //$NON-NLS-1$
					break;
				case __ftl_switch_directive_start:
					directive = new GenericNestableDirective(itemSet, "switch", "switch.png"); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case __ftl_switch_directive_end:
					directive = new GenericNestableEndDirective(itemSet, "switch"); //$NON-NLS-1$
					break;
				case __ftl_case_directive_start:
					directive = new CaseDirective(itemSet);
					break;
				case __ftl_case_default_start:
					directive = new CaseDefaultDirective(itemSet);
					break;
				case __ftl_interpolation:
					directive = new Interpolation(itemSet);
					break;
				case __ftl_ftl_directive:
					directive = new FtlDirective(itemSet);
					break;
				case __ftl_directive:
					String name = getDirectiveName(region, viewer);
					directive = new GenericNestableDirective(itemSet, name, "element.png"); //$NON-NLS-1$
					break;
				case __ftl_directive_end:
					name = getDirectiveName(region, viewer);
					directive = new GenericNestableEndDirective(itemSet, name);
					break;
				default:
					Plugin.log("Unexpected "+ Directive.class.getName() + "'"+ region.getType() +"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				if (null != directive) {
					directive.load(region, viewer, resource);
				}
				return directive;
			}
		}
	}

	private static String getDirectiveName (ITypedRegion region, ISourceViewer viewer) {
		StringBuilder sb = new StringBuilder();
		try {
			int offset = region.getOffset();
			int stopIndex = offset + region.getLength();
			char c = viewer.getDocument().getChar(offset);
			while (c != ' ' && c != '>' && offset <= stopIndex) {
				if (c != '<' && c != '#' && c != '/')
					sb.append(c);
				c = viewer.getDocument().getChar(++offset);
			}
			return sb.toString();
		}
		catch (BadLocationException e) {}
		return sb.toString();
	}
}