/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.freemarker.lang.test;

import java.util.HashSet;
import java.util.Set;

import org.jboss.ide.eclipse.freemarker.lang.Directive;
import org.jboss.ide.eclipse.freemarker.model.interpolation.BuiltInInfo;
import org.junit.Assert;
import org.junit.Test;

import freemarker.template.Configuration;

public class LangEnumCompletenessTest {

	@Test
	public void testDirectives() {
		Configuration cfg = new Configuration(Configuration.getVersion());
		Set<String> expectedNames = cfg.getSupportedBuiltInDirectiveNames(Configuration.LEGACY_NAMING_CONVENTION);

		Set<String> actualNames = new HashSet<String>();
		for (Directive value : Directive.values()) {
			actualNames.add(value.getKeyword().toString());
		}

		assertNamesEqual(expectedNames, actualNames);
	}

	@Test
	public void testBuiltInNames() {
		Configuration cfg = new Configuration(Configuration.getVersion());
		Set<String> expectedNames = cfg.getSupportedBuiltInNames(Configuration.LEGACY_NAMING_CONVENTION);

		Set<String> actualNames = new HashSet<String>();
		for (BuiltInInfo value : BuiltInInfo.values()) {
			actualNames.add(value.getSnakeCaseName());
		}

		assertNamesEqual(expectedNames, actualNames);
	}

	private void assertNamesEqual(Set<String> expectedNames, Set<String> actualNames) {
		if (!expectedNames.equals(actualNames)) {
			HashSet<String> diff = new HashSet<String>();
			diff.addAll(expectedNames);
			diff.removeAll(actualNames);
			if (!diff.isEmpty()) {
				Assert.fail("Missing names: " + diff);
			}

			diff.clear();
			diff.addAll(actualNames);
			diff.removeAll(expectedNames);
			if (!diff.isEmpty()) {
				Assert.fail("Unrecognized names: " + diff);
			}
		}
	}

}
