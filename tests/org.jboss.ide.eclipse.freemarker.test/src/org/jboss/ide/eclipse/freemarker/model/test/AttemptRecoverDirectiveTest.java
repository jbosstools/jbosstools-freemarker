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
package org.jboss.ide.eclipse.freemarker.model.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.ide.eclipse.freemarker.model.AttemptDirective;
import org.jboss.ide.eclipse.freemarker.model.AttemptEndDirective;
import org.jboss.ide.eclipse.freemarker.model.Interpolation;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.RecoverDirective;
import org.jboss.ide.eclipse.freemarker.test.FreemarkerTestUtils;
import org.junit.Assert;

import freemarker.template.TemplateException;

public class AttemptRecoverDirectiveTest extends AbstractDirectiveTest {

	public static final String TEST_FTL_FILE = "attempt-recover.txt.ftl"; //$NON-NLS-1$

	@Override
	protected String getTestFileName() {
		return TEST_FTL_FILE;
	}

	public void testFtl() throws IOException, TemplateException {
		FreemarkerTestUtils.validateFtlTemplate(new File(project.getFile(TEST_DIRECTORY).getLocationURI()), TEST_FTL_FILE);
	}

	public void testModel() {
		Collection<Item> items = load();
		Iterator<Item> i = items.iterator();

		Item attemptDirective = assertChildren(i, AttemptDirective.class,
				Interpolation.class,
				RecoverDirective.class);
		assertDirective(i, Interpolation.class);
		Item recoverDirective = assertChildren(i, RecoverDirective.class,
				Interpolation.class);
		assertDirective(i, Interpolation.class);
		Item attemptEndDirective = assertDirective(i, AttemptEndDirective.class);
		
		Item[] relatedItems = new Item[] { attemptDirective, recoverDirective, attemptEndDirective };
		Assert.assertArrayEquals(relatedItems, attemptDirective.getRelatedItems());
		Assert.assertArrayEquals(relatedItems, recoverDirective.getRelatedItems());
		Assert.assertArrayEquals(relatedItems, attemptEndDirective.getRelatedItems());
	}

}
