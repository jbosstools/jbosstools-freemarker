package org.jboss.ide.eclipse.freemarker.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Properties;

import org.junit.Assert;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTestUtils {

	public static String readFile(File f) throws IOException {
		StringBuilder result = new StringBuilder();
		Reader in = null;

		try {
			in = new InputStreamReader(new FileInputStream(f), "utf-8"); //$NON-NLS-1$
			int c = 0;
			while ((c = in.read()) >= 0) {
				result.append((char) c);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return result.toString();
	}

	public static void validateFtlTemplate(File templateDirectory, String tempateFileName) throws IOException, TemplateException {
		String propsFileName = tempateFileName + ".model.properties"; //$NON-NLS-1$
		Properties model = loadModel(new File(templateDirectory, propsFileName));
		String fileNameExpected = tempateFileName + ".expected.txt"; //$NON-NLS-1$
		Configuration config = new Configuration();
		config.setDirectoryForTemplateLoading(templateDirectory);
		Template temp = config.getTemplate(tempateFileName);
		StringWriter found = new StringWriter();
		temp.process(model, found);
		File expectedFile = new File(templateDirectory, fileNameExpected);
		if (expectedFile.exists()) {
			String expected = readFile(expectedFile);
			Assert.assertEquals(expected, found.toString());
		}
		else {
			Assert.fail("You may want to create "+ expectedFile.getAbsolutePath() +":\n"+ found.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static Properties loadModel(File propertiesFile) throws IOException {
		Properties result = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesFile);
			result.load(in);
			return result;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
