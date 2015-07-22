/*
 * JBoss by Red Hat
 * Copyright 2006-2015, Red Hat Middleware, LLC, and individual contributors as indicated
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.model.CompletionDirective;
import org.jboss.ide.eclipse.freemarker.model.CompletionInterpolation;
import org.jboss.ide.eclipse.freemarker.model.CompletionMacroInstance;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.model.MacroInstance;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class CompletionProcessor extends TemplateCompletionProcessor implements
		IContentAssistProcessor {

	private Editor editor;

	private static final ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];

	public CompletionProcessor(Editor editor) {
		this.editor = editor;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

		Map<String, Class<?>> context = new HashMap<String, Class<?>>();
		ContextValue[] values = ConfigurationManager.getInstance(
				editor.getProject()).getContextValues(editor.getFile(),
				true);
		for (int i = 0; i < values.length; i++) {
			context.put(values[i].name, values[i].objClass);
		}
		
		// we might be starting something
		try {
			int topOffset = viewer.getDocument().getLineInformationOfOffset(offset).getOffset();

			for (int i = offset - 1; i >= 0; i--) {
				char c = editor.getDocument().getChar(i);
				if (c == LexicalConstants.RIGHT_ANGLE_BRACKET || c == LexicalConstants.RIGHT_SQUARE_BRACKET) {
					break;
				}
				if (c == LexicalConstants.LEFT_ANGLE_BRACKET || c == LexicalConstants.LEFT_SQUARE_BRACKET) {
					if (editor.getDocument().getLength() > i + 1) {
						char c2 = editor.getDocument().getChar(i + 1);
						if (c2 == LexicalConstants.HASH) {
							CompletionDirective completionDirective = new CompletionDirective(
									editor.getItemSet(), i, offset - i,
									(ISourceViewer) viewer,
									(IResource) editor.getFile());
							return completionDirective
									.getCompletionProposals(offset,
											context);
						} else if (c2 == LexicalConstants.AT) {
							CompletionMacroInstance completionMacroInstance = new CompletionMacroInstance(
									editor.getItemSet(), editor
											.getDocument().get(i,
													offset - i), i,
									editor.getFile());
							return completionMacroInstance
									.getCompletionProposals(offset,
											context);
						} else if (c2 == LexicalConstants.SLASH) {
							if (editor.getDocument().getLength() < i + 3
									|| editor.getDocument().getChar(
											i + 2) == LexicalConstants.SPACE
									|| editor.getDocument().getChar(
											i + 2) == LexicalConstants.CR
									|| editor.getDocument().getChar(
											i + 2) == LexicalConstants.LF) {
								Item stackItem = editor.getItemSet()
										.getPreviousStartItem(offset);
								StringBuilder value = new StringBuilder();
								if (null != stackItem
										&& stackItem instanceof MacroInstance)
									value.append(LexicalConstants.AT);
								else
									value.append(LexicalConstants.HASH);
								String name = null;
								if (null != stackItem)
									name = stackItem.getFirstToken();
								if (null != name)
									value.append(name);
								if (c == LexicalConstants.LEFT_ANGLE_BRACKET)
									value.append(LexicalConstants.RIGHT_ANGLE_BRACKET);
								else
									value.append(LexicalConstants.RIGHT_SQUARE_BRACKET);
								ICompletionProposal completionProposal = new CompletionProposal(
										value.toString(), offset, 0,
										offset
												+ value.toString()
														.length());
								return new ICompletionProposal[] { completionProposal };
							}
						} else {
							break;
						}
					}
				}
			}

			// check for interpolations
			for (int i = offset - 1; i >= topOffset; i--) {
				char c = editor.getDocument().getChar(i);
				if (c == LexicalConstants.LF)
					break;
				else if (c == LexicalConstants.DOLLAR) {
					if (editor.getDocument().getLength() > (i + 1)) {
						char c2 = editor.getDocument().getChar(i + 1);
						if (c2 == LexicalConstants.LEFT_BRACE) {
							int j = offset;
							while (editor.getDocument().getLength() > j) {
								char c3 = editor.getDocument().getChar(
										j);
								if (Character.isWhitespace(c3)
										|| c3 == LexicalConstants.LEFT_PARENTHESIS || c3 == LexicalConstants.PERIOD
										|| c3 == LexicalConstants.RIGHT_PARENTHESIS || c3 == LexicalConstants.RIGHT_BRACE
										|| c3 == LexicalConstants.QUESTION_MARK) {
									// j = j-1;
									break;
								}
								j++;
							}
							CompletionInterpolation interpolation = new CompletionInterpolation(
									editor.getItemSet(), editor
											.getDocument()
											.get(i, j - i), i,
									editor.getFile());
							interpolation.setParentItem(editor
									.getItemSet().getPreviousStartItem(
											offset));
							return interpolation
									.getCompletionProposals(offset,
											context);
						}
					}
				}
			}
		} catch (BadLocationException e) {
			Plugin.log(e);
		}
			
		return NO_COMPLETIONS;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		return null;
	}

	@Override
	protected Image getImage(Template template) {
		return null;
	}

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { LexicalConstants.PERIOD, LexicalConstants.DOLLAR, LexicalConstants.HASH, LexicalConstants.AT, LexicalConstants.SLASH, LexicalConstants.QUESTION_MARK, LexicalConstants.LEFT_BRACE };
	}
}