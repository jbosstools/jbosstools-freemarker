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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.SyntaxMode;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;

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
	private SyntaxMode syntaxMode;
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
				itemParsers
						.put(partitionType, partitionType.createItemParser());
			}
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
	private void reconcile() {

		List<ITypedRegion> regions = parseRegions();
		if (regions != null) {
			this.editor.reconcile(regions);
		}

	}

	/**
	 * Returns a {@link List} of regions which represent entities for building a
	 * new {@link ItemSet}.
	 *
	 * @return {@link List} of regions or null if {@link #monitor} was cancelled.
	 */
	public List<ITypedRegion> parseRegions() {
		final IDocument doc = this.document;
		if (monitor != null) {
			// FIXME: Add translation
			// "Rebuilding FreeMarker model of <filename>"
			monitor.beginTask("", doc.getLength()); //$NON-NLS-1$
		}
		ensureItemParsersInitilized();
		final SyntaxMode newMode = DocumentProvider.findMode(doc);
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
				ITypedRegion region = doc.getPartition(index);
				PartitionType partitionType = PartitionType.fastValueOf(region
						.getType());
				if (partitionType != null) {
					ITokenScanner scanner = itemParsers.get(partitionType);
					if (scanner != null) {
						if (newMode != this.syntaxMode
								&& scanner instanceof SyntaxModeListener) {
							((SyntaxModeListener) scanner)
									.syntaxModeChanged(newMode);
						}
						scanner.setRange(doc, region.getOffset(),
								region.getLength());
						for (IToken token = scanner.nextToken(); !token.isEOF(); token = scanner
								.nextToken()) {
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
				index = region.getOffset() + region.getLength() + 1;
			}
		} catch (BadLocationException ignored) {
		} finally {
			this.syntaxMode = newMode;
			if (monitor != null) {
				monitor.done();
			}
		}
		return regions;
	}

	@Override
	public void setDocument(IDocument document) {
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
