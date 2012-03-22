package org.jboss.tools.freemarker.ui.bot.test;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

/**
 * RunAs bot helper  
 * @author jpeterka
 *
 */
public class RunAs {

	public static void click(final String contains) {

		SWTBotExt bot = new SWTBotExt();
		final SWTBotMenu menuRunAs = bot.menu(IDELabel.Menu.RUN).menu(
				IDELabel.Menu.RUN_AS);
		final MenuItem menuItem = UIThreadRunnable
				.syncExec(new WidgetResult<MenuItem>() {
					public MenuItem run() {
						int menuItemIndex = 0;
						MenuItem menuItem = null;
						final MenuItem[] menuItems = menuRunAs.widget.getMenu()
								.getItems();
						while (menuItem == null
								&& menuItemIndex < menuItems.length) {
							if (menuItems[menuItemIndex].getText().indexOf(
									contains) > -1) {
								menuItem = menuItems[menuItemIndex];
							} else {
								menuItemIndex++;
							}
						}
						return menuItem;
					}
				});
		if (menuItem != null) {
			new SWTBotMenu(menuItem).click();
		} else {
			throw new WidgetNotFoundException(
					"Unable to find Menu Item with Label " + contains);
		}
	}
}
