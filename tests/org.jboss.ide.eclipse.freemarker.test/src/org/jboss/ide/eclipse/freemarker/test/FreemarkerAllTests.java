package org.jboss.ide.eclipse.freemarker.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jboss.ide.eclipse.freemarker.editor.test.FreemarkerEditorTest;
import org.jboss.ide.eclipse.freemarker.preferences.test.FreemarkerPreferencePageTest;
import org.jboss.tools.tests.AbstractPluginsLoadTest;

public class FreemarkerAllTests extends TestCase {

	public static Test suite ()
	{
		TestSuite suite = new TestSuite(FreemarkerAllTests.class.getName());
		suite.addTestSuite(FreemarkerPluginsLoadTest.class);
		suite.addTestSuite(FreemarkerPreferencePageTest.class);
		suite.addTestSuite(FreemarkerEditorTest.class);		
		return suite;
	}
	
	
	static public class FreemarkerPluginsLoadTest extends AbstractPluginsLoadTest {
		
		public FreemarkerPluginsLoadTest() {}
		
		public void testFreemarkerPluginsAreResolvedAndActivated() {
			testBundlesAreLoadedFor("org.jboss.ide.eclipse.freemarker.feature");
		}
	}
}
