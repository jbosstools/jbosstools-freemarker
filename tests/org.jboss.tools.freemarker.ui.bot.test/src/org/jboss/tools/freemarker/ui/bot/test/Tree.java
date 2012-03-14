package org.jboss.tools.freemarker.ui.bot.test;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * Tree extension for bad trees
 * @author jpeterka
 *
 */
public class Tree {
	
	private static Logger log = Logger.getLogger(Tree.class);

	public static SWTBotTreeItem select(SWTBot bot, String... items) { 
		SWTBotTreeItem item = expand(bot, items);
		item.select();
		return item;
	}
	
	public static SWTBotTreeItem open(SWTBot bot, String... items) {
		SWTBotTreeItem item = select(bot, items);
		item.doubleClick();		
		return item;
	}
	
	public static SWTBotTreeItem expand(SWTBot bot, String... items) {
		SWTBotTree tree = bot.tree();				
		SWTBotTreeItem nextItem = tree.getTreeItem(items[0]);
		SWTBotTreeItem item = null;
		final int sleep = 1000;  // 1s
		final int limit = 5; // 5 cycles max
		
		for (int i = 0 ; i < items.length - 1; i++ ) {
			item = nextItem;
			expandNode(item);
			boolean ok = findChild(item,items[i+1]);
			// 1st cure - time			
			if (!ok) {
				log.info("Nok: 1st round");
				int counter = 0;
				while (counter < limit) {
					bot.sleep(sleep);
					ok = findChild(item, items[i+1]);
					if (!ok) {
						counter++;
					}
					else break;
				}
			}
			// 2nd cure (re-colapse/re-expansion)
			if (!ok) {
				log.info("Nok: 2nd round");
				collapseNode(item);
				expandNode(item);
				ok = findChild(item,items[i+1]);
				if (!ok) {
					int counter = 0;
					while (counter < limit) {
						bot.sleep(sleep);
						ok = findChild(item, items[i+1]);
						if (!ok) {
							counter++;
						}
						else break;
					}
				}
			}
			// 3dr round - final round
			if (ok) {
				nextItem = item.getNode(items[i+1]);				
			}
			else fail("Unable to find node " + items[i+1]);
		}
		return nextItem;
	}	
	
	
	private static void expandNode(SWTBotTreeItem item) {
		item.expand();
		if (!item.isExpanded()) {
			fail("Unable to expand " + item.getText());
		}
	}
	
	
	private static boolean findChild(SWTBotTreeItem item, String newTitle) {
		boolean res = false;
		
		// check nodes if they containt what is required
		if (item.getNodes().contains(newTitle)) {
			res = true;
			log.info("Node " + item.getText() + " contains " + newTitle);
		}
		else
		{
			log.info("Unable to find subnode " + newTitle);
		}	
		return res;
		
	}
	
	private static void collapseNode(SWTBotTreeItem item) {
		if (item.isExpanded()) {
			item.collapse();
		}
		if (item.isExpanded()) {
			fail("Unable to collapse" + item.getText());
		}
	}
}
