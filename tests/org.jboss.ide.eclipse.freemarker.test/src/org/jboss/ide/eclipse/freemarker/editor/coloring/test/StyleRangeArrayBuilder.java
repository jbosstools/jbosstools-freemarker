package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences;
import org.jboss.ide.eclipse.freemarker.preferences.Preferences.PreferenceKey;

public class StyleRangeArrayBuilder {
	public static String propose(StyleRange[] actual, IDocument document) {
		Map<Color, PreferenceKey> reverse = new HashMap<Color, PreferenceKey>();
		Preferences prefs = Preferences.getInstance();
		for (PreferenceKey k : PreferenceKey.values()) {
			if (k.name().startsWith("COLOR_")) { //$NON-NLS-1$
				Color c = prefs.getColor(k);
				reverse.put(c, k);
			}
		}

		StringBuilder out = new StringBuilder();
		out.append(
				"StyleRange[] expected = new " + StyleRangeArrayBuilder.class.getSimpleName()) //$NON-NLS-1$
				.append("()\n"); //$NON-NLS-1$
		for (StyleRange styleRange : actual) {
			PreferenceKey key = reverse.get(styleRange.foreground);
			String method = proposeBuilderMethod(key);
			String comment = proposeComment(styleRange, document);
			out.append("." + method + "(" + styleRange.length + ") " + comment + "\n"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
		}
		out.append(".build();\n"); //$NON-NLS-1$
		return out.toString();
	}

	private static String proposeBuilderMethod(PreferenceKey k) {
		String result = k.name().substring("COLOR_".length()); //$NON-NLS-1$
		result = result.toLowerCase(Locale.ENGLISH);
		Matcher m = Pattern.compile("_.").matcher(result); //$NON-NLS-1$
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb,
					m.group().substring(1).toUpperCase(Locale.ENGLISH));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String proposeComment(StyleRange styleRange,
			IDocument document) {
		try {
			String text = document.get(styleRange.start, styleRange.length);
			if (text.matches("[ \t\n\r]+")) { //$NON-NLS-1$
				return "// <whitespace>"; //$NON-NLS-1$
			} else {
				text = text.replace("\r", "").replace('\n', ' ').trim(); //$NON-NLS-1$ //$NON-NLS-2$
				return "// " + text; //$NON-NLS-1$
			}
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}

	private int offset = 0;

	private final List<StyleRange> styleRanges = new ArrayList<StyleRange>();

	public StyleRange[] build() {
		return styleRanges.toArray(new StyleRange[styleRanges.size()]);
	}

	public StyleRangeArrayBuilder comment(int length) {
		return range(length, PreferenceKey.COLOR_COMMENT);
	}

	public StyleRangeArrayBuilder directive(int length) {
		return range(length, PreferenceKey.COLOR_DIRECTIVE);
	}

	public StyleRangeArrayBuilder interpolation(int length) {
		return range(length, PreferenceKey.COLOR_INTERPOLATION);
	}

	public StyleRangeArrayBuilder range(int length, PreferenceKey preferenceKey) {
		styleRanges.add(new StyleRange(offset, length, Preferences
				.getInstance().getColor(preferenceKey), null));
		offset += length;
		return this;
	}

	public StyleRangeArrayBuilder string(int length) {
		return range(length, PreferenceKey.COLOR_STRING);
	}

	public StyleRangeArrayBuilder text(int length) {
		return range(length, PreferenceKey.COLOR_TEXT);
	}

	public StyleRangeArrayBuilder xmlComment(int length) {
		return range(length, PreferenceKey.COLOR_XML_COMMENT);
	}

	public StyleRangeArrayBuilder xmlTag(int length) {
		return range(length, PreferenceKey.COLOR_XML_TAG);
	}

}
