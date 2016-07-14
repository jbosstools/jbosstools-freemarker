/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Daniel Dekany 
 ******************************************************************************/
package org.jboss.ide.eclipse.freemarker.editor.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.junit.Assert;

public class ExpectedTokenList {
	
	private final List<TokenAssertion> expectedTokens = new ArrayList<TokenAssertion>();
	private int nextOffset = 0;

	private static class TokenAssertion {
		private final int offset;
		private final int length;
		private final String data;
		
		public TokenAssertion(int offset, int length, String data) {
			this.offset = offset;
			this.length = length;
			this.data = data;
		}

		public void assertEquals(ITokenScanner scanner, IToken actualToken, int index) {
			if (!data.equals(actualToken.getData()) || scanner.getTokenOffset() != offset
					|| scanner.getTokenLength() != length) {
				Assert.fail("Token at index " + index + " differs from expected.\n"
						+ "Expected: offset=" + offset + ", length=" + length + ", data=" + data + "\n"
						+ "Actual: offset=" + scanner.getTokenOffset() + ", length=" + scanner.getTokenLength()
						+ ", data=" + actualToken.getData());
			}
		}
		
	}
	
	public ExpectedTokenList setOffset(int offset) {
		nextOffset = offset;
		return this;
	}
	
	public ExpectedTokenList add(PartitionType partitionType, int length) {
		expectedTokens.add(new TokenAssertion(nextOffset, length, partitionType.getContentType()));
		nextOffset += length;
		return this;
	}
	
	public void assertEqualsTokensFrom(ITokenScanner scanner) {
		int fetchedCount = 0;
		while (true) {
			IToken actualToken = scanner.nextToken();
			
			if (actualToken.isEOF()) {
				if (fetchedCount != expectedTokens.size()) {
					Assert.fail("Expected " + expectedTokens.size() + " token(s), but EOF was reached after "
							+ fetchedCount + " tokens.");
				}
				return;
			}
			
			if (fetchedCount >= expectedTokens.size()) {
				Assert.fail("Expected only " + expectedTokens.size() + " token(s), but had more.");
			}
			
			expectedTokens.get(fetchedCount).assertEquals(scanner, actualToken, fetchedCount);
			
			fetchedCount++;
		}
		
	}
	
}
