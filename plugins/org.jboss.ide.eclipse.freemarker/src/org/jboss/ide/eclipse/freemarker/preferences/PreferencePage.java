/*
 * JBoss, a division of Red Hat
 * Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
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
package org.jboss.ide.eclipse.freemarker.preferences;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.ide.eclipse.freemarker.Constants;
import org.jboss.ide.eclipse.freemarker.Plugin;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
		setDescription("FreeMarker Settings");
	}

	public void createFieldEditors() {
		addField(new ColorFieldEditor(Constants.COLOR_DIRECTIVE,
				"Directive:", getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_RELATED_ITEM,
                "Related Directives:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Constants.HIGHLIGHT_RELATED_ITEMS,
				"Highlight Related Directives", getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_INTERPOLATION,
				"Interpolation:", getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_TEXT,
				"Text:", getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_COMMENT,
				"Comment:", getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_STRING,
				"String:", getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_XML_TAG,
                "HTML/XML Tag:", getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_XML_COMMENT,
                "HTML/XML Comment:", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
}