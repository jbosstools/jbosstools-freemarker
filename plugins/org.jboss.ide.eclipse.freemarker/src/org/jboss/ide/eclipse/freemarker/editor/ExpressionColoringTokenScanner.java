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
package org.jboss.ide.eclipse.freemarker.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.lang.Keyword;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

import freemarker.template.utility.StringUtil;

/**
 * Superclass for partitions that has a start- and an end-sequence, but between them there's an FTL expression that
 * can contain the end-sequence without ending the partition (for example inside a string literal). 
 */
public class ExpressionColoringTokenScanner implements ITokenScanner {
	
	private final IToken variableToken = PartitionType.createColoringToken(PreferenceKey.COLOR_VARIABLE);
	private final IToken keywordToken = PartitionType.createColoringToken(PreferenceKey.COLOR_KEYWORD);
	private final IToken stringToken = PartitionType.createColoringToken(PreferenceKey.COLOR_STRING);
	private final IToken commentToken = PartitionType.createColoringToken(PreferenceKey.COLOR_COMMENT);
	private final IToken otherExpressionPartToken = PartitionType.createColoringToken(PreferenceKey.COLOR_OTHER_EXP_PART);
	
	@SuppressWarnings("unused")
	private static final int BOF = -2 != ICharacterScanner.EOF ? -2 : -3;
	
	private final IToken partitionBoundaryColorToken;
	private final char[] startSequence1;
	private final char[] endSequence1;
	private final char[] endSequence1Alt;
	private final char[] startSequence2;
	private final char[] endSequence2;
	private final char[] endSequence2Alt;
	private final boolean braceAndBracketCanNotInhibitEndSequence1;
	private final boolean extendStartSequenceWithFirstDottedName;
	
	private IDocument document;
	
	private int offset;
	private int endOffset;
	private int tokenOffset;
	
	private IToken postponedToken;
	private int postponedTokenEndOffset;
	
	private boolean pastStartSequenceConsumingPhase;
	private boolean braceAndBracketCanNotInhibitEndSequence;
	private char[] actualEndSequence;
	private char[] actualEndSequenceAlt;
	List<Character> openedParentesisLikeExps = new ArrayList<>();

	/**
	 * @param partitionBoundaryColorToken
	 *            The color used for the start and end sequences.
	 * @param extendStartSequenceWithFirstDottedName
	 * 			Setting this to {@code true} causes the first dotted name after any of the matching start sequence
	 * 			to be colored like a start sequence. 
	 * @param startSequence1
	 *            The character sequence at the beginning of the partition,
	 *            before the expression. Example: <code>{ '<', '#' }</code>
	 * @param endSequence1
	 *            The character sequence at the end of the partition, after the
	 *            expression, assuming we have started with
	 *            {@code startSequence1}. Example: <code>{ '>' }</code>
	 * @param endSequence1Alt
	 *            Alternative to {@code endSequence1}, or {@code null}.  Example: <code>{ '/', '>' }</code>
	 * @param braceAndBracketCanNotInhibitEndSequence1
	 *            Applicable only if the partition starts with
	 *            {@code startSequence1}
	 * @param startSequence2
	 *            Alternative start sequence, or {@code null}
	 * @param endSequence2
	 *            The end sequence used if the partition starts with
	 *            {@code startSequence2}
	 * @param endSequence2Alt
	 *            Alternative to {@code endSequence2}, or {@code null}
	 */
	public ExpressionColoringTokenScanner(
			IToken partitionBoundaryColorToken, boolean extendStartSequenceWithFirstDottedName,
			char[] startSequence1, char[] endSequence1, char[] endSequence1Alt,
			boolean braceAndBracketCanNotInhibitEndSequence1,
			char[] startSequence2, char[] endSequence2, char[] endSequence2Alt) {
		this.partitionBoundaryColorToken = partitionBoundaryColorToken;
		this.extendStartSequenceWithFirstDottedName = extendStartSequenceWithFirstDottedName;
		this.startSequence1 = startSequence1;
		this.endSequence1 = endSequence1;
		this.endSequence1Alt = endSequence1Alt;
		this.braceAndBracketCanNotInhibitEndSequence1 = braceAndBracketCanNotInhibitEndSequence1;
		this.startSequence2 = startSequence2;
		this.endSequence2 = endSequence2;
		this.endSequence2Alt = endSequence2Alt;
	}

	@Override
	public IToken nextToken() {
		// If we have a token postponed from the last nextToken() call, so return that: 
		if (postponedToken != null) {
			tokenOffset = offset;
			offset = postponedTokenEndOffset;
			IToken token = postponedToken;
			postponedToken = null;
			
			return token;
		}
		
		try {
			int initalOffset = offset;
			IToken token = nextTokenInternal();
			// Do we have a gap between the tokenOffset and the end of the previously returned token?
			if (tokenOffset > initalOffset) {
				// Return gap filler token, postpone current token for the next nextToken() call:
				postponedToken = token;
				postponedTokenEndOffset = offset;
				offset = tokenOffset;
				tokenOffset = initalOffset;
				return otherExpressionPartToken;
			} else {
				return token;
			}
		} catch (BadLocationException e) {
			offset = endOffset;
			return partitionBoundaryColorToken;
		}
	}
	
	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.document = document;
		this.offset = offset;
		endOffset = offset + length;
		pastStartSequenceConsumingPhase = false; // reset nextToken() state
	}

	@Override
	public int getTokenOffset() {
		return tokenOffset;
	}

	@Override
	public int getTokenLength() {
		return offset - tokenOffset;
	}

	/**
	 * Can leave gaps between the returned tokens. The gaps meant to be filled
	 * with non-{@link #otherExpressionPartToken} tokens.
	 */
	private IToken nextTokenInternal() throws BadLocationException {
		// The returned token will start at `tokenOffset`, and will stretch until `offset`. So don't forget to ensure
		// that `tokneOffset` is set before returning.
		
		if (!pastStartSequenceConsumingPhase) {
			// This branch runs once after every setRange(), and is responsible for consuming the start sequence,
			// and also for resetting the state for the next phase.
			
			tokenOffset = offset;
			
			// Try to consume the start sequence:
			actualEndSequence = null; // We don't yet know
			boolean startSeqConsumingResult = consumeSequence(startSequence1, false);
			if (startSeqConsumingResult) {
				braceAndBracketCanNotInhibitEndSequence = braceAndBracketCanNotInhibitEndSequence1;
				actualEndSequence = endSequence1;
				actualEndSequenceAlt = endSequence1Alt;
			} else if (startSequence2 != null) {
				braceAndBracketCanNotInhibitEndSequence = false;				
				startSeqConsumingResult = consumeSequence(startSequence2, false);
				if (startSeqConsumingResult) {
					actualEndSequence = endSequence2;
					actualEndSequenceAlt = endSequence2Alt;
				}
			}

			// Regardless of if we could consume the start sequence, this phase is over:
			pastStartSequenceConsumingPhase = true;
			
			// Reset state for later phases:
			openedParentesisLikeExps.clear();
			
			if (startSeqConsumingResult) {
				if (extendStartSequenceWithFirstDottedName) {
					findDottedNameEnd: while (offset < endOffset) {
						if (!consumeIdentifier(document.getChar(offset++))) {
							offset--; // Rewind to the character consumed in the argument above
							break findDottedNameEnd;
						}
						if (offset < endOffset) {
							char c = document.getChar(offset);
							if (c != LexicalConstants.PERIOD) {
								break findDottedNameEnd;
							}
							offset++; // skip dot
						}
					}
				}
				
				return partitionBoundaryColorToken;
			}
			// Falls through to expression processing phase. This shouldn't occur, unless the partitioner disagrees
			// with this tokenizer, which is then either a bug or the partitioning is outdated.
		}

		// Expression processing state:
		while (true) {
			if (offset == endOffset) {
				this.tokenOffset = offset;
				return Token.EOF;
			}

			final int cOffset = offset;
			final char c = document.getChar(offset++);
			
			// endSequence:
			if ((actualEndSequence != null && c == actualEndSequence[0]
					|| actualEndSequenceAlt != null && c == actualEndSequenceAlt[0]) 
					&& (openedParentesisLikeExps.isEmpty()
						|| isUnopenedClosingParenthesisLikeCharacter(c, openedParentesisLikeExps) // [1]
						|| braceAndBracketCanNotInhibitEndSequence
							&& !openedParentesisLikeExps.contains(LexicalConstants.LEFT_PARENTHESIS)
					)
					&& ( // [2]
							c == actualEndSequence[0] && consumeSequence(actualEndSequence, true)
							|| actualEndSequenceAlt != null
									&& c == actualEndSequenceAlt[0] && consumeSequence(actualEndSequenceAlt, true))) {
				// [1]: This is a trick to prevent the annoyance when, with an example, you have `${}`, and then start
				//      typing `f(x)` into it, and when you get to `${f(}`, the coloring of the dollar interpolation
				//      will leak out beyond the `}`, possibly coloring everything till the end of the file, because
				//      the `}` is inside a still open `(...)` expression, and so the partition won't end.
				// [2]: Checked last, otherwise we had to unread the endSequence if another condition is false.
				
				offset = endOffset; // Include whatever is left from the partition (normally nothing) in this token
				tokenOffset = cOffset;
				return partitionBoundaryColorToken;
			}
			
			// String literal:
			if (ParserUtils.isQuotingChar(c)
					|| c == LexicalConstants.r && offset < endOffset
							&& ParserUtils.isQuotingChar(document.getChar(offset))) {
				tokenOffset = cOffset;
				
				// rawStr: r"Backslash doesn't escape here: \"
				final boolean rawStr;
				final char q;
				if (c == LexicalConstants.r) {
					rawStr = true;
					q = document.getChar(offset++);
				} else {
					rawStr = false;
					q = c;
				}
				while (true) {
					if (offset == endOffset) {
						return stringToken;
					}
					char c2 = document.getChar(offset++);
					
					if (c2 == LexicalConstants.BACKSLASH && !rawStr) {
						// Skip the character after \
						if (offset == endOffset) {
							return stringToken;
						}
						offset++;
					} else if (c2 == q) {
						return stringToken;
					}
				}
			// In expression only, <#-- --> or [#-- --] or <!-- --> or [!-- --], regardless of the tag syntax
			} else if ((c == LexicalConstants.COMMENT_START_HEAD_1 || c == LexicalConstants.COMMENT_START_HEAD_2)
					&& (consumeSequence(LexicalConstants.COMMENT_START_TAIL_1, false)
							|| consumeSequence(LexicalConstants.COMMENT_START_TAIL_2, false))) {
				tokenOffset = cOffset;
				
				while (true) {
					if (offset == endOffset) {
						return commentToken;
					}
					char c2 = document.getChar(offset++);
					
					if (c2 == LexicalConstants.COMMENT_END_HEAD) {
						// A quirk in FTL; comments can be ended both ways, regardless of what the start was. 
						if ((consumeSequence(LexicalConstants.COMMENT_END_TAIL_1, false))
								|| consumeSequence(LexicalConstants.COMMENT_END_TAIL_2, false)) {
							return commentToken;
						}
					}
				}
			// Opening (...) or [...] or {...}
			} else if (c == LexicalConstants.LEFT_PARENTHESIS || c == LexicalConstants.LEFT_BRACE
					|| c == LexicalConstants.LEFT_SQUARE_BRACKET) {
				openedParentesisLikeExps.add(c);
			// Closing (...) or [...] or {...}
			} else if (c == LexicalConstants.RIGHT_PARENTHESIS || c == LexicalConstants.RIGHT_BRACE
					|| c == LexicalConstants.RIGHT_SQUARE_BRACKET) {
				closeParenthesisLike(openedParentesisLikeExps, ParserUtils.mirrorClosingParentesisLikeChar(c));
			// ?identifier - we do this as we don't want foo to be colorized as a variableToken 
			} else if (c == LexicalConstants.QUESTION_MARK) {
				if (offset < endOffset) {
					consumeIdentifier(document.getChar(offset++));					
				}
				// Don't return here; this will be part of the otherExpressionPartToken
			// identifier
			} else if (consumeIdentifier(c)) {
				tokenOffset = cOffset;
				return Keyword.EXPRESSION_KEYWORDS.contains(document.get(cOffset, offset - cOffset))
						? keywordToken : variableToken;
			}
			// There's no final else {}; We just let the otherExpressionPartToken to stretch.
		} // while (true)
	}

	/**
	 * @param c The recently consumed character, that is the character at {@code offset - 1}.
	 */
	private boolean consumeIdentifier(char c) throws BadLocationException {
		if (!StringUtil.isFTLIdentifierStart(c)
				&& !(c == LexicalConstants.BACKSLASH && isNextCharEscapeableNameChar())) {
			return false;
		}

		if (c == LexicalConstants.BACKSLASH) {
			offset++;
		}
		while (true) {
			if (offset == endOffset) {
				return true;
			}
			c = document.getChar(offset++);

			if (!StringUtil.isFTLIdentifierPart(c)) {
				if (c == LexicalConstants.BACKSLASH && isNextCharEscapeableNameChar()) {
					// Skip the character after \
					offset++;
				} else {
					// c wasn't part of the identifier
					offset--;
					return true;
				}
			}
		}
	}

	private boolean isNextCharEscapeableNameChar() throws BadLocationException {
		return offset < endOffset && ParserUtils.isEscapeableNameChar(document.getChar(offset));
	}

	private boolean isUnopenedClosingParenthesisLikeCharacter(char c, List<Character> openedParentesisLikeExps) {
		char openingChar = ParserUtils.mirrorClosingParentesisLikeChar(c);
		return openingChar != c && !openedParentesisLikeExps.contains(openingChar);
	}

	private void closeParenthesisLike(List<Character> openedParentesisLikeExps, int openerChar) {
		if (openedParentesisLikeExps == null) { 
			return;
		}
		for (int i = openedParentesisLikeExps.size() - 1; i >= 0; i--) {
			Character removed = openedParentesisLikeExps.remove(i);
			if (removed == openerChar) {
				return;
			}
		}
	}

	private boolean consumeSequence(
			char[] sequence, boolean firstCharAlreadyMatched) throws BadLocationException {
		int i = firstCharAlreadyMatched ? 1 : 0;
		int initalOffset = offset;
		while (i < sequence.length) {
			if (offset == endOffset) return false;
			int c = document.getChar(offset++);
			if (c != sequence[i]) {
				offset = initalOffset;
				return false;
			}
			i++;
		}
		return true;
	}

}
