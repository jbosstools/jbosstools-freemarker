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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.outline.OutlinePage;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.target.TargetLanguageSupport;
import org.jboss.ide.eclipse.freemarker.target.TargetLanguages;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class Editor extends TextEditor implements KeyListener, MouseListener {

	private OutlinePage fOutlinePage;
	private org.jboss.ide.eclipse.freemarker.editor.Configuration configuration;

	private ItemSet itemSet;
	private Item selectedItem;
	private Item[] relatedItems;
	private static final char[] VALIDATION_TOKENS = new char[] {
			LexicalConstants.QUOT, LexicalConstants.LEFT_SQUARE_BRACKET,
			LexicalConstants.RIGHT_SQUARE_BRACKET, LexicalConstants.COMMA,
			LexicalConstants.PERIOD, LexicalConstants.LF, '4' };
	private boolean readOnly = false;

	private boolean mouseDown = false;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;

	public Editor() {
		super();
		configuration = new org.jboss.ide.eclipse.freemarker.editor.Configuration(
				getPreferenceStore(), this);
		setSourceViewerConfiguration(configuration);
		setDocumentProvider(Plugin.getDefault().getDocumentProvider());

	}

	@Override
	public void dispose() {
		ConfigurationManager.getInstance(getProject()).reload();
		super.dispose();
		if (matchingCharacterPainter != null) {
			matchingCharacterPainter.dispose();
		}
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class aClass) {
		Object adapter;
		if (aClass.equals(IContentOutlinePage.class)) {
			if (fOutlinePage == null) {
				fOutlinePage = new OutlinePage(this);
				if (getEditorInput() != null) {
					fOutlinePage.setInput(getEditorInput());
				}
			}
			adapter = fOutlinePage;
		} else {
			adapter = super.getAdapter(aClass);
		}
		return adapter;
	}

	protected static final char[] BRACKETS = { LexicalConstants.LEFT_BRACE,
			LexicalConstants.RIGHT_BRACE, LexicalConstants.LEFT_PARENTHESIS,
			LexicalConstants.RIGHT_PARENTHESIS,
			LexicalConstants.LEFT_SQUARE_BRACKET,
			LexicalConstants.RIGHT_SQUARE_BRACKET,
			LexicalConstants.LEFT_ANGLE_BRACKET,
			LexicalConstants.RIGHT_ANGLE_BRACKET };
	private MatchingCharacterPainter matchingCharacterPainter;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSourceViewer().getTextWidget().addKeyListener(this);
		getSourceViewer().getTextWidget().addMouseListener(this);
		// matchingCharacterPainter = new MatchingCharacterPainter(
		// getSourceViewer(),
		// new JavaPairMatcher(BRACKETS));
		// ((SourceViewer)
		// getSourceViewer()).addPainter(matchingCharacterPainter);
	}

	@Override
	protected void createActions() {
		super.createActions();
		// Add content assist propsal action
		ContentAssistAction action = new ContentAssistAction(Plugin
				.getDefault().getResourceBundle(),
				"FreemarkerEditor.ContentAssist", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("FreemarkerEditor.ContentAssist", action); //$NON-NLS-1$
		action.setEnabled(true);
	}

	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		if (!mouseDown) {
			int offset = getCaretOffset();
			Item item = getItemSet().getSelectedItem(offset);
			if (null == item && offset > 0)
				item = getItemSet().getSelectedItem(offset - 1);
			if (Preferences.getInstance().getBoolean(
					PreferenceKey.HIGHLIGHT_RELATED_ITEMS)) {
				if (null != item && null != item.getRelatedItems()
						&& item.getRelatedItems().length > 0) {
					highlightRelatedRegions(item.getRelatedItems(), item);
				} else {
					highlightRelatedRegions(null, item);
				}
			}
			if (null == item) {
				item = getItemSet().getContextItem(getCaretOffset());
			}
			if (null != fOutlinePage) {
				fOutlinePage.update(item);
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		mouseDown = true;
	}

	@Override
	public void mouseUp(MouseEvent e) {
		mouseDown = false;
		handleCursorPositionChanged();
	}

	public void select(Item item) {
		selectAndReveal(item.getRegion().getOffset(), item.getRegion()
				.getLength());
	}

	public IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer != null) {
			return viewer.getDocument();
		}
		return null;
	}

	public ITextViewer getTextViewer() {
		return getSourceViewer();
	}

	public void addProblemMarker(String aMessage, int aLine, int aCharStart, int aCharEnd) {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			Map<String, Object> attributes = new HashMap<String, Object>();
			MarkerUtilities.setMessage(attributes, aMessage);
			// There was no MarkerUtilities.setSeverity method:
			attributes.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
			MarkerUtilities.setLineNumber(attributes, aLine);
            // Specify the column only if it isn't after the last char, otherwise Eclipse won't show the error marker
			// on the editor margin: 
			if (aCharStart < getDocument().getLength()) {			
    			MarkerUtilities.setCharStart(attributes, aCharStart);
    			MarkerUtilities.setCharEnd(attributes, aCharEnd);
			}
			MarkerUtilities.createMarker(file, attributes, IMarker.PROBLEM);
		} catch (CoreException e) {
			Plugin.log(e);
		}
	}

	private synchronized void highlightRelatedRegions(Item[] items,
			Item selectedItem) {
		if (null == items || items.length == 0) {
			if (null != relatedItems && relatedItems.length > 0) {
				for (int i = 0; i < relatedItems.length; i++) {
					if (getDocument().getLength() >= relatedItems[i]
							.getRegion().getOffset()
							+ relatedItems[i].getRegion().getLength()) {
						if (null == this.selectedItem
								|| !relatedItems[i].equals(this.selectedItem))
							resetRange(relatedItems[i].getRegion());
					}
				}
			}
			relatedItems = null;
		}
		if (null != relatedItems) {
			for (int i = 0; i < relatedItems.length; i++) {
				if (getDocument().getLength() >= relatedItems[i].getRegion()
						.getOffset() + relatedItems[i].getRegion().getLength()) {
					if (null == this.selectedItem
							|| !relatedItems[i].equals(this.selectedItem))
						resetRange(relatedItems[i].getRegion());
				}
			}
		}
		if (null != items && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (getDocument().getLength() >= items[i].getRegion()
						.getOffset() + items[i].getRegion().getLength()
						&& !items[i].equals(selectedItem)) {
					ITypedRegion region = items[i].getRegion();
					getSourceViewer().getTextWidget().setStyleRange(
							new StyleRange(region.getOffset(), region
									.getLength(), null, Preferences
									.getInstance().getColor(
											PreferenceKey.COLOR_RELATED_ITEM)));
				}
			}
		}
		relatedItems = items;
		this.selectedItem = selectedItem;
	}

	private void resetRange(ITypedRegion region) {
		if (getSourceViewer() instanceof ITextViewerExtension2)
			((ITextViewerExtension2) getSourceViewer())
					.invalidateTextPresentation(region.getOffset(),
							region.getLength());
		else
			getSourceViewer().invalidateTextPresentation();
	}

	public Item getSelectedItem(boolean allowFudge) {
		int caretOffset = getCaretOffset();
		Item item = getItemSet().getSelectedItem(getCaretOffset());
		if (null == item && caretOffset > 0)
			item = getItemSet().getSelectedItem(caretOffset - 1);
		return item;
	}

	public Item getSelectedItem() {
		return getItemSet().getSelectedItem(getCaretOffset());
	}

	public int getCaretOffset() {
		return getSourceViewer().getTextWidget().getCaretOffset();
	}

	public ItemSet getItemSet() {
		if (null == this.itemSet) {
			this.itemSet = createItemSet(Collections.<ITypedRegion> emptyList());
		}
		return this.itemSet;
	}

	/**
	 * Creates a new {@link ItemSet} based on the given {@link List} or regions.
	 * This method is public for the sake of testing.
	 *
	 * @param regions
	 * @return
	 */
	public ItemSet createItemSet(List<ITypedRegion> regions) {
		IResource resource = null;
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			resource = ((IFileEditorInput) input).getFile();
			// } else if (getEditorInput() instanceof JarEntryEditorInput) {
			// resource = null;
		}
		return new ItemSet(getSourceViewer(), regions, resource);
	}

	public OutlinePage getOutlinePage() {
		return fOutlinePage;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.CTRL) {
			ctrlDown = true;
		}
		if (e.keyCode == SWT.SHIFT) {
			shiftDown = true;
		}
		// this feature is for avoiding double closing brackets 
		if (e.character == LexicalConstants.RIGHT_SQUARE_BRACKET 
						|| e.character == LexicalConstants.RIGHT_BRACE ) {
			try {
				int offset = getCaretOffset();
				if (offset < getDocument().getLength()) {
					char c = getDocument().getChar(offset);
					if (c == e.character) {
						// remove this
						getDocument().replace(getCaretOffset(), 1, ""); //$NON-NLS-1$
					}
				}
			} catch (BadLocationException e1) {
				Plugin.log(e1);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CTRL) {
			ctrlDown = false;
		} else if (e.keyCode == SWT.SHIFT) {
			shiftDown = false;
		}
		try {
			IDocument document = getSourceViewer().getDocument();
			if (shiftDown && (e.keyCode == '3' || e.keyCode == '2')) {
				int offset = getCaretOffset();
				char c = document.getChar(offset - 2);
				if (c == LexicalConstants.LEFT_SQUARE_BRACKET
						|| c == LexicalConstants.LEFT_ANGLE_BRACKET) {
					// directive
					char endChar = ParserUtils.getMatchingRightBracket(c);
					if (document.getLength() > offset) {
						if (offset > 0) {
							for (int i = offset + 1; i < document.getLength(); i++) {
								char c2 = document.getChar(i);
								if (c2 == endChar) {
									return;
								} else if (c2 == LexicalConstants.LF) {
									break;
								}
							}
							document.replace(offset, 0, String.valueOf(endChar));
						}
					} else {
						document.replace(offset, 0, String.valueOf(endChar));
					}
				}
			} else if (shiftDown && e.keyCode == LexicalConstants.LEFT_BRACE) {
				int offset = getCaretOffset();
				char c = document.getChar(offset - 2);
				if (c == LexicalConstants.DOLLAR) {
					// interpolation
					if (document.getLength() > offset) {
						if (offset > 0) {
							for (int i = offset + 1; i < document.getLength(); i++) {
								char c2 = document.getChar(i);
								if (c2 == LexicalConstants.RIGHT_BRACE) {
									return;
								} else if (c2 == LexicalConstants.LF) {
									break;
								}
							}
							document.replace(offset, 0, String
									.valueOf(LexicalConstants.RIGHT_BRACE));
						}
					} else {
						document.replace(offset, 0,
								String.valueOf(LexicalConstants.RIGHT_BRACE));
					}
				}
			}
		} catch (BadLocationException exc) {
			Plugin.log(exc);
		}

		boolean stale = false;
		if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
			stale = true;
		} else if (null != getSelectedItem(true)) {
			stale = true;
		} else {
			char c = (char) e.keyCode;
			for (int j = 0; j < VALIDATION_TOKENS.length; j++) {
				if (c == VALIDATION_TOKENS[j]) {
					stale = true;
					break;
				}
			}
			if (ctrlDown && (e.keyCode == 'v' || e.keyCode == 'x')) {
				stale = true;
			}
		}
		if (stale) {
			int offset = getCaretOffset();
			Item item = getItemSet().getSelectedItem(offset);
			if (null == item && offset > 0)
				item = getItemSet().getSelectedItem(offset - 1);
			if (Preferences.getInstance().getBoolean(
					PreferenceKey.HIGHLIGHT_RELATED_ITEMS)) {
				if (null != item && null != item.getRelatedItems()
						&& item.getRelatedItems().length > 0) {
					highlightRelatedRegions(item.getRelatedItems(), item);
				} else {
					highlightRelatedRegions(null, item);
				}
			}
			validateContentsAsync();
		}
	}

	public static Validator VALIDATOR;

	/**
	 * Synchronous validation of the content.
	 */
	// TODO: This is a hack used for testing
    public synchronized void validateContents() throws CoreException {
        // Wait for any ongoing async validation to finish. 
        while (VALIDATOR != null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrputed while waiting previos validation to finish.", e);
            }
        }
        
        new Validator(this).run();
    }
	
	public synchronized void validateContentsAsync() {
	    // TODO: Rework this; The markers can get stale this this way.
		if (null == VALIDATOR) {
			VALIDATOR = new Validator(this);
			VALIDATOR.start();
		}
	}

	public IProject getProject() {
		return ((IFileEditorInput) getEditorInput()).getFile().getProject();
	}

	public IFile getFile() {
		return (null != getEditorInput()) ? ((IFileEditorInput) getEditorInput())
				.getFile() : null;
	}

	private Configuration fmConfiguration;
	private TargetLanguageSupport targetLanguageSupport;

	public class Validator extends Thread {
		Editor editor;

		public Validator(Editor editor) {
			this.editor = editor;
		}

		@Override
		public void run() {
			try {
				if (null != getFile()) {
					if (null == fmConfiguration) {
						fmConfiguration = new Configuration(Configuration.getVersion());
						fmConfiguration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
						fmConfiguration.setTabSize(1);
					}
					getFile().deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
					String documentContent = getDocument().get();
					/*
					 * dummy is here to be able to suppress the warning about
					 * the unused new Template()
					 */
					try {
						@SuppressWarnings("unused")
						Template dummy = new Template(getFile().getName(), documentContent, fmConfiguration);
					} catch (ParseException e) {
						editor.addProblemMarker(e.getEditorMessage(), e.getLineNumber(),
								getDocument().getLineOffset(e.getLineNumber() - 1) + e.getColumnNumber() - 1,
								getDocument().getLineOffset(e.getEndLineNumber() - 1) + e.getEndColumnNumber());
					}
				}
			} catch (Exception e) {
				Plugin.log(e);
			} finally {
			    synchronized (Editor.this) {
	                Editor.VALIDATOR = null;
	                Editor.this.notify();
                }
			}
		}

	}

	@Override
	protected void editorSaved() {
		super.editorSaved();
		validateContentsAsync();
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Reconciles in the current thread using a newly create
	 * {@link ReconcilingStrategy}.
	 * <p>
	 * For test purposes. Useful when testing functionality related to
	 * {@link #getItemSet()} The {@link ReconcilingStrategy} configured inside
	 * presentation reconciler works asynchronously and therefore
	 * {@link #getItemSet()} returns an empty {@link ItemSet} when called from
	 * the UI thread just after opening the editor. Calling this method before
	 * {@link #getItemSet()} fixes the problem.
	 * <p>
	 * This method can be called from the UI thread.
	 *
	 */
	public void reconcileInstantly() {
		ReconcilingStrategy s = new ReconcilingStrategy(this);
		s.setDocument(getDocument());
		List<ITypedRegion> regions = s.parseRegions();
		reconcile(regions);
	}

	public void reconcile(List<ITypedRegion> regions) {
		/* re-create the model in the reconciler thread */
		final ItemSet newItemSet = createItemSet(regions);

		Runnable newItemSetTask = new Runnable() {
			@Override
			public void run() {
				Editor.this.itemSet = newItemSet;
				if (null != Editor.this.fOutlinePage) {
					Editor.this.fOutlinePage.refresh();
				}
			}
		};
		/* make sure to run in the UI thread */
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			/* we are in the UI thread - run synchrounously */
			newItemSetTask.run();
		} else {
			/* run asynchronously */
			Display.getDefault().asyncExec(newItemSetTask);
		}

	}

	public TargetLanguageSupport getTargetLanguageSupport() {
		if (targetLanguageSupport == null) {
			targetLanguageSupport = TargetLanguages.findSupport(getEditorInput());
		}
		return targetLanguageSupport;
	}
}