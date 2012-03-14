package org.jboss.tools.freemarker.testprj;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FMTest {
	public static void main(String[] args) throws IOException {
		FMTest test = new FMTest();
		test.perform();
	}

	public void perform() {
		// Add the values in the datamodel
		Map<String,Object> datamodel = new HashMap<String,Object>();
		datamodel.put("pet", "Bunny");
		datamodel.put("number", new Integer(6));
		try {

			freemarkerDo(datamodel, "welcome.ftl");
		}

		catch (Exception e) {
			System.out.println("Exception");
			System.out.println(e.getLocalizedMessage());

		}
	}

	public void freemarkerDo(Map datamodel, String template) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(this.getClass(), "/");
		Template tpl = cfg.getTemplate(template);
		OutputStreamWriter output = new OutputStreamWriter(System.out);
		tpl.process(datamodel, output);
	}
}
