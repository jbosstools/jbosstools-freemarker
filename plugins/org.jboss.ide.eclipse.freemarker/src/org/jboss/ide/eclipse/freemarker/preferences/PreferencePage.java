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
package org.jboss.ide.eclipse.freemarker.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.outline.OutlineContentProvider;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public static final String ID = "org.jboss.ide.eclipse.freemarker.preferences.OutlinePreferencePage"; //$NON-NLS-1$

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void adjustGridLayout() {
		GridLayout layout = (GridLayout) getFieldEditorParent().getLayout();
		layout.numColumns = 1;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
	}

	private static GridLayout createGroupLayout() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		return layout;
	}

	@Override
	public void createFieldEditors() {

		Group editorGroup = new Group(getFieldEditorParent(), SWT.NONE);
		editorGroup.setText(Messages.PreferencePage_GROUP_EDITOR_SYNTAX_COLORING);
		editorGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		addField(new ColorFieldEditor(PreferenceKey.COLOR_DIRECTIVE.toString(),
				Messages.PreferencePage_FIELD_DIRECTIVE, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_RELATED_ITEM.toString(),
				Messages.PreferencePage_FIELD_RELATED_DIRECTIVES, editorGroup));

		BooleanFieldEditor highlightRelated = new BooleanFieldEditor(
				PreferenceKey.HIGHLIGHT_RELATED_ITEMS.toString(),
				Messages.PreferencePage_FIELD_HIGHLIGHT_RELATED_DIRECTIVES,
				editorGroup) {
			@Override
			protected void doFillIntoGrid(Composite parent, int numColumns) {
				super.doFillIntoGrid(parent, 2);
			}
		};
		addField(highlightRelated);

		addField(new ColorFieldEditor(PreferenceKey.COLOR_INTERPOLATION.toString(),
				Messages.PreferencePage_FIELD_INTERPOLATION, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_TEXT.toString(),
				Messages.PreferencePage_FIELD_TEXT, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_COMMENT.toString(),
				Messages.PreferencePage_FIELD_COMMENT, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_STRING.toString(),
				Messages.PreferencePage_FIELD_STRING, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_VARIABLE.toString(),
				Messages.PreferencePage_FIELD_VARIABLE, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_KEYWORD.toString(),
				Messages.PreferencePage_FIELD_KEYWORD, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_OTHER_EXP_PART.toString(),
				Messages.PreferencePage_FIELD_OTHER_EXP_PART, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_XML_TAG.toString(),
				Messages.PreferencePage_FIELD_HTML_XML_TAG, editorGroup));
		addField(new ColorFieldEditor(PreferenceKey.COLOR_XML_COMMENT.toString(),
				Messages.PreferencePage_FIELD_HTML_XML_COMMENT, editorGroup));

		editorGroup.setLayout(createGroupLayout());

		addField(new RadioGroupFieldEditor(
				PreferenceKey.OUTLINE_LEVEL_OF_DETAIL.toString(),
				Messages.PreferencePage_FIELD_OUTLINE_LEVEL_OF_DETAIL,
				1,
				new String[][] {
						{
								Messages.PreferencePage_FIELD_OUTLINE_LEVEL_OF_DETAIL_FUNCTION_AND_MACRO_DEFINITIONS,
								OutlineContentProvider.OutlineLevelOfDetail.functionAndMacroDefinitions
										.name() },
						{
								Messages.PreferencePage_FIELD_OUTLINE_LEVEL_OF_DETAIL_FULL,
								OutlineContentProvider.OutlineLevelOfDetail.full.name() } },
				getFieldEditorParent(), true));

	}

	@Override
	public void init(IWorkbench workbench) {
	}
}