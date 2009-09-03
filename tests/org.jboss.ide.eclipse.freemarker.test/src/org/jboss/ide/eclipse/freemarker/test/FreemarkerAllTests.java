package org.jboss.ide.eclipse.freemarker.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jboss.tools.tests.AbstractPluginsLoadTest;

public class FreemarkerAllTests extends TestCase {

	public static Test suite ()
	{
		TestSuite suite = new TestSuite(FreemarkerAllTests.class.getName());
		suite.addTestSuite(FreemarkerPluginsLoadTest.class);
		return suite;
	}
	
	
	static public class FreemarkerPluginsLoadTest extends AbstractPluginsLoadTest {
		
		public FreemarkerPluginsLoadTest() {}
		
		public void testBirtPluginsAreResolvedAndActivated() {
			testBundlesAreLoadedFor("org.jboss.ide.eclipse.freemarker.feature");
		}
	}
}
