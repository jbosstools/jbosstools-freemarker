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
package org.jboss.ide.eclipse.freemarker.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.IEditorInput;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * A {@link IReconcilingStrategy} that triggers re-build of {@link ItemSet} in
 * {@link Editor}. This happens both on document opening and document change.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public class ReconcilingStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension {
	private IDocument document;
	private Editor editor;
	private EnumMap<PartitionType, ITokenScanner> itemParsers;
	private SyntaxMode lastSyntaxMode;
	private IProgressMonitor monitor;

	public ReconcilingStrategy(Editor editor) {
		super();
		this.editor = editor;
	}

	private void ensureItemParsersInitilized() {
		if (this.itemParsers == null) {
			this.itemParsers = new EnumMap<PartitionType, ITokenScanner>(
					PartitionType.class);
			PartitionType[] partitionTypes = PartitionType.values();
			for (PartitionType partitionType : partitionTypes) {
				itemParsers.put(partitionType, partitionType.createItemParser());
			}
			lastSyntaxMode = SyntaxMode.getDefault();
		}
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void initialReconcile() {
		reconcile();
	}

	private boolean canceled() {
		return monitor != null && monitor.isCanceled();
	}

	/**
	 * Creates a {@link List} of {@link ITypedRegion}s that can be used to build
	 * a new {@link ItemSet} in {@link Editor#reconcile(List)}
	 */
	void reconcile() {
		long stamp1 = ((IDocumentExtension4) document).getModificationStamp();
		List<ITypedRegion> regions = parseRegions();
		long stamp2 = ((IDocumentExtension4) document).getModificationStamp();
		if (regions != null) {
			this.editor.updateModel(regions, stamp1 == stamp2 ? stamp1 : null);
		}

		ParseException e = checkTemplateSyntax();		
		this.editor.updateMarkers(e);
	}

	private Configuration fmConfiguration;
	
	/**
	 * Returns the syntactical error in the template, or {@code null} if there's none.
	 */
	private ParseException checkTemplateSyntax() {
		if (fmConfiguration == null) {
			fmConfiguration = new Configuration(Configuration.getVersion());
			fmConfiguration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
			fmConfiguration.setTabSize(1);
		}
		try {
			@SuppressWarnings("unused")
			Template dummy = new Template(
					editor.getEditorInput().getName(),
					editor.getDocument().get(),
					fmConfiguration);
		} catch (ParseException e) {
			return e;
		} catch (IOException e) {
			Plugin.log(e);
		}
		return null;
	}

	/**
	 * Returns a {@link List} of regions which represent entities for building a
	 * new {@link ItemSet}.
	 *
	 * @return {@link List} of regions or null if {@link #monitor} was cancelled.
	 */
	private List<ITypedRegion> parseRegions() {
		final IDocument doc = this.document;
		if (monitor != null) {
			// FIXME: Add translation
			// "Rebuilding FreeMarker model of <filename>"
			monitor.beginTask("", doc.getLength()); //$NON-NLS-1$
		}
		ensureItemParsersInitilized();
		final SyntaxMode actualSyntaxMode = DocumentProvider.findMode(doc);
		if (actualSyntaxMode != this.lastSyntaxMode) {
			for (ITokenScanner scanner : itemParsers.values()) {
				if (scanner instanceof SyntaxModeListener) {
					((SyntaxModeListener) scanner).syntaxModeChanged(actualSyntaxMode);
				}
			}
			this.lastSyntaxMode = actualSyntaxMode;
		}
		int index = 0;
		List<ITypedRegion> regions = new ArrayList<ITypedRegion>(64);
		try {
			while (index < doc.getLength()) {
				if (canceled()) {
					return null;
				}
				if (monitor != null) {
					monitor.worked(index);
				}
				ITypedRegion region = TextUtilities.getPartition(doc, DocumentProvider.FTL_PARTITIONING, index, false);
				PartitionType partitionType = PartitionType.getByContentType(region.getType());
				if (partitionType != null) {
					ITokenScanner scanner = itemParsers.get(partitionType);
					if (scanner != null) {
						scanner.setRange(doc, region.getOffset(), region.getLength());
						for (IToken token = scanner.nextToken(); !token.isEOF(); token = scanner.nextToken()) {
							if (canceled()) {
								return null;
							}
							if (monitor != null) {
								monitor.worked(index);
							}
							Object itemType = token.getData();
							if (!token.isUndefined() && !token.isWhitespace()
									&& itemType instanceof String) {
								TypedRegion itemRegion = new TypedRegion(
										scanner.getTokenOffset(),
										scanner.getTokenLength(),
										(String) itemType);
								regions.add(itemRegion);
							}
						}
					} else {
						regions.add(region);
					}
				}
				index = region.getOffset() + region.getLength();
			}
		} catch (BadLocationException e) {
			Plugin.log(e);
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
		return regions;
	}

	@Override
	public void setDocument(IDocument document) {
		if (!(document instanceof IDocumentExtension4)) {
			throw new IllegalStateException(
					"Document must implement " + IDocumentExtension4.class.getName() + ".");  //$NON-NLS-1$//$NON-NLS-2$
		}
		this.document = document;
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile();
	}

	@Override
	public void reconcile(IRegion partition) {
		reconcile();
	}

}
