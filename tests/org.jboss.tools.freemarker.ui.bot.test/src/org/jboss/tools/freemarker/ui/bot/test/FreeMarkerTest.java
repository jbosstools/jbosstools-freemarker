package org.jboss.tools.freemarker.ui.bot.test;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ConsoleView;
import org.jboss.tools.ui.bot.ext.view.ErrorLogView;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Freemarker ui bot test
 * @author jpeterka
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class FreeMarkerTest extends SWTTestExt {

	private String prj = "org.jboss.tools.freemarker.testprj";
	
	@BeforeClass
	public static void beforeClass() {
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.closeView(IDELabel.View.JBOSS_CENTRAL);
		eclipse.closeAllEditors();
	}

	@Test
	public void emptyTest() {
		assertTrue(true);
	}

	@Test
	public void FreeMarkerTest() {
		emptyErrorLog();
		importTestProject();
		openFTLFileInEditor();
		checkFreemMarkerOutput();
		checkErrorLog();
	}

	private void importTestProject() {
		String rpath = ResourceHelper.getResourceAbsolutePath(
				Activator.PLUGIN_ID, "resources/prj");
		String wpath = ResourceHelper.getWorkspaceAbsolutePath();
		File rfile = new File(rpath);
		File wfile = new File(wpath);
		try {
			FileHelper.copyFilesBinaryRecursively(rfile, wfile, null);
		} catch (IOException e) {
			fail("Unable to copy freemarker test project");
		}
		ImportHelper.importAllProjects(wpath);
		util.waitForNonIgnoredJobs();
	}

	private void openFTLFileInEditor() {
		SWTBotView viewOpen = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		
		SWTBotTree tree = viewOpen.bot().tree();
		Tree.open(viewOpen.bot(),prj, "ftl", "welcome.ftl" );
		SWTBotEditor editor = bot.editorByTitle("welcome.ftl");
		editor.setFocus();

		SWTBotView ov = open.viewOpen(ActionItem.View.GeneralOutline.LABEL);
		SWTBotTreeItem[] allItems = ov.bot().tree().getAllItems();
		List<String> list = new ArrayList<String>();
		for (SWTBotTreeItem i : allItems) {
			list.add(i.getText());
		}
		assertTrue(list.contains("user"));
		assertTrue(list.contains("latestProduct.name"));
		
	    // https://issues.jboss.org/browse/JBIDE-11287
		// remove comment when this jira is fixed
		//assertTrue(list.contains("latestProduct.url"));		
	}

	private void emptyErrorLog() {
		ErrorLogView el = new ErrorLogView();
		el.delete();
		util.waitForNonIgnoredJobs();
	}

	private void checkErrorLog() {
		ErrorLogView el = new ErrorLogView();
		int count = el.getRecordCount();
		if (count > 0) {
			el.logMessages();
			// Ignored for now
			// fail("Unexpected messages in Error log, see test log");
		}
	}

	private void checkFreemMarkerOutput() {
		
		String outputExpected = "";
		String rpath = ResourceHelper.getResourceAbsolutePath(
				Activator.PLUGIN_ID, "resources/fm-output.txt");
		try {
			outputExpected = readTextFileToString(rpath);
			log.info("------------------------------------------");
			log.info(outputExpected);
			log.info("------------------------------------------");
		} catch (IOException e) {
			log.error(e.getMessage());
			new RuntimeException("Unable to read from resource");
		}

		SWTBotView viewConsole = open.viewOpen(ActionItem.View.GeneralConsole.LABEL);
		ConsoleView cv = new ConsoleView();
		cv.clearConsole();
		
		SWTBotView viewOpen = open
				.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		
		SWTBotTreeItem item = Tree.select(viewOpen.bot(), prj,"src","org.jboss.tools.freemarker.testprj","FMTest.java");
		ContextMenuHelper.clickContextMenu(viewOpen.bot().tree(),"Run As","2 Java Application");
		SWTBotShell s = bot.shell("Progress Information");
		bot.waitUntil(shellCloses(s));
						
		SWTBotView consoleView = open.viewOpen(ActionItem.View.GeneralConsole.LABEL);
		String consoleText = cv.getConsoleText(TIME_1S, TIME_10S, true);
				
		bot.waitUntil(shellCloses(s));
		
		assertTrue("Output equal check",consoleText.equals(outputExpected));
		bot.sleep(TIME_10S);
	}

	private String readTextFileToString(String filePath) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);				
				line = br.readLine();
				if (line != null)
					sb.append("\n");
			}
			String everything = sb.toString();
			return everything;
		} finally {
			br.close();
		}
	}

	@AfterClass
	public static void aterClass() {
		// wait for all jobs
		util.waitForAll();
	}

}
