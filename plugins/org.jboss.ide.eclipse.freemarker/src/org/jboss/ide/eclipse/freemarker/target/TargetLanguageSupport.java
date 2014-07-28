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

import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.IEditorInput;

/**
 * Covers all functionality needed for a syntax coloring of a target language of
 * an FTL template, such as HTML or plain text.
 *
 * @see TargetColoringScanner
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public interface TargetLanguageSupport {

	/**
	 * Creates a new {@link TargetPartitionScanner} that can be used to divide
	 * the target language parts of an FTL template into target language
	 * partitions. E.g. for target language HTML, the returned
	 * {@link TargetPartitionScanner} should be able to divide the HTML parts of
	 * an FTL template into HTML comments, HTML tags, etc.
	 *
	 * @return see above
	 */
	TargetPartitionScanner createPartitionScanner();

	/**
	 * Creates a new {@link IPartitionTokenScanner} responsible for coloring of
	 * the given target language's {@code partitionType}.
	 *
	 * @param partitionType
	 *            the partition type of a target language
	 * @return a coloring scanner
	 */
	ITokenScanner createColoringScanner(String partitionType);

	/**
	 * Retruens {@code true} if the given {@code input} is supported by this
	 * {@link TargetLanguageSupport} and {@code false} otherwise. Typically, the
	 * implementations will decide based on the file name extension of the
	 * {@code input}.
	 *
	 * @param input
	 * @return
	 */
	boolean isDocumentSupported(IEditorInput input);
}
