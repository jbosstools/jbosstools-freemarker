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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class Configuration extends TextSourceViewerConfiguration {
	private Editor editor;

	public Configuration(IPreferenceStore preferenceStore, Editor editor) {
		super(preferenceStore);
		this.editor = editor;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return PartitionType.PARTITION_TYPES;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		PartitionType[] partitionTypes = PartitionType.values();
		for (PartitionType partitionType : partitionTypes) {
			ITokenScanner scanner = partitionType.createColoringTokenizer(editor);
			if (scanner != null) {
				DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
				reconciler.setDamager(dr, partitionType.name());
				reconciler.setRepairer(dr, partitionType.name());
			}
		}
		//FIXME: Add back XML syntax coloring some day
//		ndr =
//			new NonRuleBasedDamagerRepairer(
//				new TextAttribute(
//						Preferences.getInstance().getColor(PreferenceKey.COLOR_XML_COMMENT)));
//		reconciler.setDamager(ndr, PartitionScanner.XML_COMMENT);
//		reconciler.setRepairer(ndr, PartitionScanner.XML_COMMENT);
//
//		ndr =
//			new NonRuleBasedDamagerRepairer(
//				new TextAttribute(
//						Preferences.getInstance().getColor(PreferenceKey.COLOR_COMMENT)));
//		reconciler.setDamager(ndr, PartitionScanner.FTL_COMMENT);
//		reconciler.setRepairer(ndr, PartitionScanner.FTL_COMMENT);
//
//		defaultToken = new Token(
//				new TextAttribute(
//						Preferences.getInstance().getColor(PreferenceKey.COLOR_XML_TAG)));
//		dr = new DefaultDamagerRepairer(new ContentScanner(defaultToken));
//		reconciler.setDamager(dr, PartitionScanner.XML_TAG);
//		reconciler.setRepairer(dr, PartitionScanner.XML_TAG);

		return reconciler;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer aSourceViewer)
	{
		ContentAssistant assistant = new ContentAssistant();
		CompletionProcessor completionProcessor = new CompletionProcessor(editor);
		assistant.setContentAssistProcessor(completionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		for (PartitionType partitionType : PartitionType.values()) {
			assistant.setContentAssistProcessor(completionProcessor, partitionType.name());
		}
		//FIXME: Add back XML content assist some day
//		assistant.setContentAssistProcessor(completionProcessor, PartitionScanner.XML_COMMENT);
//		assistant.setContentAssistProcessor(completionProcessor, PartitionScanner.XML_TAG);
		assistant.enableAutoInsert(true);
		assistant.enableAutoActivation(true);
		return assistant;
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new AnnotationHover();
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] detectors = super.getHyperlinkDetectors(sourceViewer);
		if (null == detectors) {
			detectors = new IHyperlinkDetector[0];
		}
		IHyperlinkDetector[] detectorsNew = new IHyperlinkDetector[detectors.length+1];
		System.arraycopy(detectors, 0, detectorsNew, 0, detectors.length);
		detectorsNew[detectorsNew.length-1] = new MacroHyperlinkDetector(sourceViewer, editor);
		return detectorsNew;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		IReconcilingStrategy reconcilingStrategy= new ReconcilingStrategy(editor);
		MonoReconciler reconciler= new MonoReconciler(reconcilingStrategy, false);
		reconciler.setDelay(500);
		return reconciler;
	}

}