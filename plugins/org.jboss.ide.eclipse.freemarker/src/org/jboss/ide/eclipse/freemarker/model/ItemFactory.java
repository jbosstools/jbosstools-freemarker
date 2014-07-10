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
import org.jboss.ide.eclipse.freemarker.editor.PartitionScanner;


public class ItemFactory {

	public static Item getItem (ItemSet itemSet, ITypedRegion region, ISourceViewer viewer, IResource resource) {
		if (null == region) return null;
		else {
			Item directive = null;
			if (region.getType().equals(PartitionScanner.FTL_IF_DIRECTIVE_START))
				directive = new IfDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_IF_DIRECTIVE_END))
				directive = new IfEndDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_IF_ELSE_DIRECTIVE))
				directive = new IfElseDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_ELSE_IF_DIRECTIVE))
				directive = new ElseIfDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_LIST_DIRECTIVE_START))
				directive = new ListDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_LIST_DIRECTIVE_END))
				directive = new ListEndDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_FUNCTION_DIRECTIVE_START))
				directive = new FunctionDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_FUNCTION_DIRECTIVE_END))
				directive = new FunctionEndDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_MACRO_DIRECTIVE_START))
				directive = new MacroDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_MACRO_DIRECTIVE_END))
				directive = new MacroEndDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_MACRO_INSTANCE_START))
				directive = new MacroInstance(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_MACRO_INSTANCE_END))
				directive = new MacroEndInstance(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_INCLUDE))
				directive = new GenericDirective(itemSet, "include.png"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_IMPORT))
				directive = new GenericDirective(itemSet, "import.png"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_ASSIGN)
					|| region.getType().equals(PartitionScanner.FTL_LOCAL)
					|| region.getType().equals(PartitionScanner.FTL_GLOBAL))
				directive = new AssignmentDirective(itemSet, region.getType());
			else if (region.getType().equals(PartitionScanner.FTL_ASSIGN_END)
					|| region.getType().equals(PartitionScanner.FTL_LOCAL_END)
					|| region.getType().equals(PartitionScanner.FTL_GLOBAL_END))
				directive = new AssignmentEndDirective(itemSet, region.getType());
			else if (region.getType().equals(PartitionScanner.FTL_BREAK))
				directive = new GenericDirective(itemSet, "break.png"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_STOP))
				directive = new GenericDirective(itemSet, "stop.png"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_RETURN))
				directive = new GenericDirective(itemSet, "return.png"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_SWITCH_DIRECTIVE_START))
				directive = new GenericNestableDirective(itemSet, "switch", "switch.png"); //$NON-NLS-1$ //$NON-NLS-2$
			else if (region.getType().equals(PartitionScanner.FTL_SWITCH_DIRECTIVE_END))
				directive = new GenericNestableEndDirective(itemSet, "switch"); //$NON-NLS-1$
			else if (region.getType().equals(PartitionScanner.FTL_CASE_DIRECTIVE_START))
				directive = new CaseDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_CASE_DEFAULT_START))
				directive = new CaseDefaultDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_INTERPOLATION))
				directive = new Interpolation(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_FTL_DIRECTIVE))
				directive = new FtlDirective(itemSet);
			else if (region.getType().equals(PartitionScanner.FTL_DIRECTIVE)) {
				String name = getDirectiveName(region, viewer);
				directive = new GenericNestableDirective(itemSet, name, "element.png"); //$NON-NLS-1$
			}
			else if (region.getType().equals(PartitionScanner.FTL_DIRECTIVE_END)) {
				String name = getDirectiveName(region, viewer);
				directive = new GenericNestableEndDirective(itemSet, name);
			}

			if (null != directive) directive.load(region, viewer, resource);
			return directive;
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