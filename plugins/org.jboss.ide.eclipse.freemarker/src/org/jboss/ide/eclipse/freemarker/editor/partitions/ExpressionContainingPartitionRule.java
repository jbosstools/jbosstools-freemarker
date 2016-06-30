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
package org.jboss.ide.eclipse.freemarker.editor.partitions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.lang.LexicalConstants;
import org.jboss.ide.eclipse.freemarker.lang.ParserUtils;

/**
 * Superclass for partitions that has a start- and an end-sequence, but between them there's an FTL expression that
 * can contain the end-sequence without ending the partition (for example inside a string literal). 
 */
public abstract class ExpressionContainingPartitionRule extends NonResumablePrediaceRule {
	
	@SuppressWarnings("unused")
	private static final int BOF = -2 != ICharacterScanner.EOF ? -2 : -3;
	
	/**
	 * Sequences that aren't legal inside expressions (outside string literals),
	 * and will be assumes to belong to the beginning of the next partition.
	 * This is a trick used to limit the effect of unclosed constructs.
	 */
	private final char[][] FORCED_NEXT_PARTITON_STARTS = new char[][] {
		LexicalConstants.DIRECTIVE_START_AB, LexicalConstants.DIRECTIVE_START_SB,
		LexicalConstants.DOLLAR_INTERPOLATION_START, LexicalConstants.HASH_INTERPOLATION_START,
		new char[] { '<', '/' } // HTML/XML or FTL end tag start
	};
	private final char[][] FORCED_PARTITON_ENDS = new char[][] {
		LexicalConstants.DIRECTIVE_END_AB_EMPTY, LexicalConstants.DIRECTIVE_END_SB_EMPTY,
	};
	
	protected char[] startSequence;
	protected char[] endSequence;
	protected char[] altEndSequence;
	protected boolean braceAndBracketCanNotInhibitEndSequence;
	
	public ExpressionContainingPartitionRule(
			char[] startSequence, char[] endSequence, char[] altEndSequence,
			boolean braceAndBracketCanNotInhibitEndSequence) {
		Assert.isNotNull(startSequence, "startSequence can't be null"); //$NON-NLS-1$
		this.startSequence = startSequence;
		Assert.isNotNull(endSequence, "endSequence can't be null"); //$NON-NLS-1$
		this.endSequence = endSequence;
		this.altEndSequence = altEndSequence;
		this.braceAndBracketCanNotInhibitEndSequence = braceAndBracketCanNotInhibitEndSequence;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		// startSequence
		if (!readSequence(scanner, startSequence, false)) {
			return Token.UNDEFINED;
		}

		int c = BOF;
		int lastC;
		List<Integer> openedParentesisLikeExps = null;
		findPartitonEnd: while (true) {
			lastC = c;
			c = scanner.read();
			if (c == ICharacterScanner.EOF) break findPartitonEnd;
			
			// endSequence
			if ((c == endSequence[0] || (altEndSequence != null && c == altEndSequence[0]))
					&& (openedParentesisLikeExps == null || openedParentesisLikeExps.isEmpty()
						|| isUnopenedClosingParenthesisLikeCharacter(c, openedParentesisLikeExps) // [1]
						|| braceAndBracketCanNotInhibitEndSequence
							&& !openedParentesisLikeExps.contains((int) LexicalConstants.LEFT_PARENTHESIS)
					)
					&&
					( // [2]
						c == endSequence[0] && readSequence(scanner, endSequence, true)
						|| altEndSequence != null
								&& c == altEndSequence[0] && readSequence(scanner, altEndSequence, true)
					)) {
				// [1]: This is a trick to prevent the annoyance when, with an example, you have `${}`, and then start
				//      typing `f(x)` into it, and when you get to `${f(}`, the coloring of the dollar interpolation
				//      will leak out beyond the `}`, possibly coloring everything till the end of the file, because
				//      the `}` is inside a still open `(...)` expression, and so the partition won't end.
				// [2]: Checked last, otherwise we had to unread the endSequence if another condition is false.
				break findPartitonEnd;
			}
			
			// String literal
			if (c == LexicalConstants.QUOT || c == LexicalConstants.APOS) {
				boolean rawStr = lastC == LexicalConstants.r; // r"Backslash doesn't escape here: \"
				int quot = c;
				findStrLiteralEnd: while (true) {
					c = scanner.read();
					if (c == ICharacterScanner.EOF) {
						break findPartitonEnd;
					}
					
					if (c == LexicalConstants.BACKSLASH && !rawStr) {
						c = scanner.read();  // drop next character
					} else if (c == quot) {
						break findStrLiteralEnd;
					}
				}
			// In expression only, <#-- --> or [#-- --] or <!-- --> or [!-- --], regardless of the tag syntax
			} else if ((c == LexicalConstants.COMMENT_START_HEAD_1 || c == LexicalConstants.COMMENT_START_HEAD_2)
					&& (readSequence(scanner, LexicalConstants.COMMENT_START_TAIL_1, false) ||
							readSequence(scanner, LexicalConstants.COMMENT_START_TAIL_2, false))) {
				findCommentEnd: while (true) {
					c = scanner.read();
					if (c == ICharacterScanner.EOF) {
						break findPartitonEnd;
					}
					if (c == LexicalConstants.COMMENT_END_HEAD) {
						// A quirk in FTL; comments can be ended both ways, regardless of what the start was. 
						if ((readSequence(scanner, LexicalConstants.COMMENT_END_TAIL_1, false))
							|| readSequence(scanner, LexicalConstants.COMMENT_END_TAIL_2, false)) {
								break findCommentEnd;
							}
					}
				}
			// Opening (...) or [...] or {...}
			} else if (c == LexicalConstants.LEFT_PARENTHESIS || c == LexicalConstants.LEFT_BRACE
					|| c == LexicalConstants.LEFT_SQUARE_BRACKET && !nextCharIs(scanner, LexicalConstants.HASH)) {
				if (openedParentesisLikeExps == null) {
					openedParentesisLikeExps = new ArrayList<>(4);
				}
				openedParentesisLikeExps.add(c);
			// Closing (...) or [...] or {...}
			} else if (c == LexicalConstants.RIGHT_PARENTHESIS || c == LexicalConstants.RIGHT_BRACE
					|| c == LexicalConstants.RIGHT_SQUARE_BRACKET) {
				closeParenthesisLike(openedParentesisLikeExps, ParserUtils.mirrorClosingParentesisLikeChar(c));
			} else {
				for (char[] forcedNextPartitionStart : FORCED_NEXT_PARTITON_STARTS) {
					if (c == forcedNextPartitionStart[0]
							&& readSequence(scanner, forcedNextPartitionStart, true)) {
						// It doesn't belongs to this paratition:
						for (int i = 0; i < forcedNextPartitionStart.length; i++) {
							scanner.unread();
						}
						break findPartitonEnd;
					}
				}
				for (char[] forcedPartitionEnd : FORCED_PARTITON_ENDS) {
					if (c == forcedPartitionEnd[0]
							&& readSequence(scanner, forcedPartitionEnd, true)) {
						break findPartitonEnd;
					}
				}
			}
		} // findPartitonEnd: while (true)
		
		return getSuccessToken();
	}

	private boolean nextCharIs(ICharacterScanner scanner, char c) {
		int c2 = scanner.read();
		scanner.unread();
		return c2 == c;
	}

	private boolean isUnopenedClosingParenthesisLikeCharacter(int c, List<Integer> openedParentesisLikeExps) {
		int openingChar = ParserUtils.mirrorClosingParentesisLikeChar(c);
		return openingChar != c && !openedParentesisLikeExps.contains(openingChar);
	}

	private void closeParenthesisLike(List<Integer> openedParentesisLikeExps, int openerChar) {
		if (openedParentesisLikeExps == null) { 
			return;
		}
		for (int i = openedParentesisLikeExps.size() - 1; i >= 0; i--) {
			Integer removed = openedParentesisLikeExps.remove(i);
			if (removed == openerChar) {
				return;
			}
		}
	}

	private boolean readSequence(
			ICharacterScanner scanner, char[] sequence, boolean firstCharAlreadyMatched) {
		int i = firstCharAlreadyMatched ? 1 : 0;
		while (i < sequence.length) {
			int c = scanner.read();
			if (c != sequence[i]) {
				for (int j = firstCharAlreadyMatched ? 1 : 0; j <= i; j++) {
					scanner.unread();
				}
				return false;
			}
			i++;
		}
		return true;
	}

}
