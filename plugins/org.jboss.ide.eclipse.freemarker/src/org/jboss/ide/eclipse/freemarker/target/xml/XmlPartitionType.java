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
package org.jboss.ide.eclipse.freemarker.target.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.SingleTokenScanner;
import org.jboss.ide.eclipse.freemarker.editor.partitions.PartitionType;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;
import org.jboss.ide.eclipse.freemarker.target.TargetMultiLineRule;

/**
 * XML/HTML partition types. See <a
 * href="http://wiki.eclipse.org/FAQ_What_is_a_document_partition%3F">What is a
 * document partition</a> in Eclipse FAQ.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 */
public enum XmlPartitionType {
	COMMENT(PreferenceKey.COLOR_XML_COMMENT) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new TargetMultiLineRule(
					XmlLexicalConstants.XML_COMMENT_START,
					XmlLexicalConstants.XML_COMMENT_END,
					new Token(this.name()), (char) 0, true);
		}
	},
	TAG(PreferenceKey.COLOR_XML_TAG) {
		@Override
		public IPredicateRule createPartitioningRule() {
			return new XmlTagRule(new Token(this.name()));
		}
	},
	OTHER(PreferenceKey.COLOR_TEXT) {
		@Override
		public IPredicateRule createPartitioningRule() {
			/* no explicit rule for OTHER */
			return null;
		}
	};

	/** The preference key for syntax coloring. */
	final PreferenceKey foregroundPreferenceKey;

	/** See {@link #fastValueOf(String)} */
	private static final Map<String, XmlPartitionType> FAST_LOOKUP;

	static {
		XmlPartitionType[] partitionTypes = XmlPartitionType.values();
		Map<String, XmlPartitionType> fastLookUp = new HashMap<String, XmlPartitionType>();
		for (XmlPartitionType partitionType : partitionTypes) {
			fastLookUp.put(partitionType.name(), partitionType);
		}
		FAST_LOOKUP = Collections.unmodifiableMap(fastLookUp);
	}

	private XmlPartitionType(PreferenceKey foregroundPreferenceKey) {
		this.foregroundPreferenceKey = foregroundPreferenceKey;
	}

	/**
	 * Creates a new {@link IPredicateRule} that matches the partitions in a
	 * document that correspond to this {@link PartitionType}.
	 *
	 * @return
	 */
	public abstract IPredicateRule createPartitioningRule();

	/**
	 * Creates a new {@link ITokenScanner} that retuns {@link IToken}s with
	 * {@link TextAttribute} set to color defined in {@link Preferences}.
	 *
	 * @return never {@code null}.
	 */
	public ITokenScanner createColoringTokenizer() {
		return new SingleTokenScanner(
				PartitionType.createColoringToken(foregroundPreferenceKey));
	}

	/**
	 * A static {@link HashMap}-backed and {@code null}-tolerant alternative of
	 * {@link #valueOf(String)}.
	 *
	 * @param name
	 * @return the {@link XmlPartitionType} that corresponds to the given
	 *         {@code name} or {@code null} of there is no such.
	 */
	public static XmlPartitionType fastValueOf(String name) {
		return FAST_LOOKUP.get(name);
	}
}
