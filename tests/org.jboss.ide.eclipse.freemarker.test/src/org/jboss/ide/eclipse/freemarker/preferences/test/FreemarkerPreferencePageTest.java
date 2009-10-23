package org.jboss.ide.eclipse.freemarker.preferences.test;

import org.eclipse.jface.preference.PreferenceDialog;
import org.jboss.ide.eclipse.freemarker.preferences.PreferencePage;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;
/**
 * TODO Get rid of copy paste code
 * 
 * @author eskimo
 *
 */
public class FreemarkerPreferencePageTest extends TestCase {

	public void testFreemarkerPreferencePageShow() {
		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					PreferencePage.ID);

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			Object selectedPage = prefDialog.getSelectedPage();
			assertTrue("Selected page is not an instance of PreferencePage", selectedPage instanceof PreferencePage); //$NON-NLS-1$
		} finally {
			prefDialog.close();
		}
	}
	
	public void testFreemarkerPreferencePagePerformOk() {
		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					PreferencePage.ID);

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			PreferencePage selectedPage = (PreferencePage)prefDialog.getSelectedPage();
			selectedPage.performOk();
		} finally {
			prefDialog.close();
		}
	}

}
