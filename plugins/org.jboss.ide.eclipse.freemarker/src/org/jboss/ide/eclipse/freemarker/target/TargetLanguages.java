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
package org.jboss.ide.eclipse.freemarker.target;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.jboss.ide.eclipse.freemarker.target.txt.TxtSupport;
import org.jboss.ide.eclipse.freemarker.target.xml.XmlSupport;

/**
 * A registry for supported target languages.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 *
 */
public class TargetLanguages {

	private static final List<TargetLanguageSupport> SUPPORTS = Collections
			.unmodifiableList(Arrays.<TargetLanguageSupport> asList(
					new XmlSupport(),
					/* keep TxtSupport on the last position as the last resort */
					new TxtSupport()));

	/**
	 * Returns a {@link TargetLanguageSupport} that corresponds to the given
	 * {@link IEditorInput}. Internally, this method loops over
	 * {@link #SUPPORTS} and returns the first entry that returns {@code true}
	 * for {@link TargetLanguageSupport#isDocumentSupported(IEditorInput)}.
	 * <p>
	 * As {@link TxtSupport} always returns {@code true} from
	 * {@link TargetLanguageSupport#isDocumentSupported(IEditorInput)} this
	 * method never returns {@code null}.
	 *
	 * @param input
	 * @return
	 */
	public static TargetLanguageSupport findSupport(IEditorInput input) {
		for (TargetLanguageSupport targetLanguageSupport : SUPPORTS) {
			if (targetLanguageSupport.isDocumentSupported(input)) {
				return targetLanguageSupport;
			}
		}
		return null;
	}
}
