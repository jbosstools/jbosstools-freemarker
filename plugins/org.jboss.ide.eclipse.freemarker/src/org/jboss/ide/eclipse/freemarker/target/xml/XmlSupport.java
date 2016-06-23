/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.target.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.target.RuleBasedTargetPartitionScanner;
import org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport;
import org.jboss.ide.eclipse.freemarker.target.TargetPartitionScanner;

/**
 * A {@link TargetLanguageSupport} for XML/HTML.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class XmlSupport implements TargetLanguageSupport {

	public static final List<String> SUPPORTED_EXTENSIONS = Collections.unmodifiableList(
			Arrays.asList(
			".xml", //$NON-NLS-1$
			".html", //$NON-NLS-1$
			".xhtml", //$NON-NLS-1$
			".htm", //$NON-NLS-1$
			".xml.ftl", //$NON-NLS-1$
			".html.ftl", //$NON-NLS-1$
			".xhtml.ftl", //$NON-NLS-1$
			".htm.ftl", //$NON-NLS-1$
			".ftlh", //$NON-NLS-1$
			".ftlx" //$NON-NLS-1$
	));

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport#createColoringScanner(java.lang.String)
	 */
	@Override
	public ITokenScanner createColoringScanner(String partitionType) {
		XmlPartitionType xmlPartitionType = XmlPartitionType.fastValueOf(partitionType);
		if (xmlPartitionType != null) {
			return xmlPartitionType.createColoringTokenizer();
		}
		return null;
	}

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport#createPartitionScanner()
	 */
	@Override
	public TargetPartitionScanner createPartitionScanner() {

		XmlPartitionType[] partitiontTypes = XmlPartitionType.values();
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>(partitiontTypes.length);
		for (XmlPartitionType xmlPartitionType : partitiontTypes) {
			IPredicateRule rule = xmlPartitionType.createPartitioningRule();
			if (rule != null) {
				rules.add(rule);
			}
		}
		RuleBasedTargetPartitionScanner result = new RuleBasedTargetPartitionScanner();
		result.setDefaultReturnToken(new Token(XmlPartitionType.OTHER.name()));
		result.setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
		return result;
	}

	/**
	 * @see org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport#isDocumentSupported(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public boolean isDocumentSupported(IEditorInput input) {
		if (input instanceof IStorageEditorInput) {
			try {
				IStorage storage = ((IStorageEditorInput) input).getStorage();
				String fileName = storage.getName();
				if (fileName != null) {
					fileName = fileName.toLowerCase(Locale.ENGLISH);
					for (String suffix : SUPPORTED_EXTENSIONS) {
						if (fileName.endsWith(suffix)) {
							return true;
						}
					}
				}
			} catch (CoreException e) {
				Plugin.log(e);
			}
		}
		return false;
	}

}
