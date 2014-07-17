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
package org.jboss.ide.eclipse.freemarker.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.outline.OutlineContentProvider.OutlineLevelOfDetail;

/**
 * An access to Freemarker plugin preferences. Basically a shorthand for {@code Platform.getPreferencesService().get*()} methods.
 *
 * @since 1.4.0
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class Preferences {
	private static final Preferences INSTANCE = new Preferences();

	/**
	 * Preference keys and their default values.
	 *
	 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
	 *
	 */
	public enum PreferenceKey {

		COLOR_DIRECTIVE("color.directive", "0,0,255"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_INTERPOLATION("color.interpolation", "255,0,128"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_COMMENT("color.comment", "170,0,0"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_TEXT("color.text", "0,0,0"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_STRING("color.string", "0,128,128"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_XML_TAG("color.xmlTag", "0,0,128"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_XML_COMMENT("color.xmlComment", "128,128,128"), //$NON-NLS-1$ //$NON-NLS-2$
		COLOR_RELATED_ITEM("color.oppositeRegion", "255,255,128"), //$NON-NLS-1$ //$NON-NLS-2$
		HIGHLIGHT_RELATED_ITEMS("boolean.highlightRelatedItems", Boolean.TRUE), //$NON-NLS-1$
		OUTLINE_LEVEL_OF_DETAIL("outline.level.of.detail", OutlineLevelOfDetail.functionAndMacroDefinitions.name()); //$NON-NLS-1$

		private final String key;
		private final Object defaultValue;

		/**
		 * Make sure that the type of {@code defaultValue} is supported by {@link IPreferencesService}.
		 * @param value
		 * @param defaultValue
		 */
		private PreferenceKey(String value, Object defaultValue) {
			this.key = value;
			this.defaultValue = defaultValue;
		}

		@Override
		public String toString() {
			return key;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}

	}

	/**
	 * @return the singleton
	 */
	public static Preferences getInstance() {
		return INSTANCE;
	}

	private IPreferencesService preferencesService;

	/** A map to speed up the conversion of colors from string to {@link Color} */
	private Map<String, Color> colorCache = new HashMap<String, Color>(16);

	/**
	 * You may want to use {@link #getInstance()} rather than this private constructor.
	 */
	private Preferences() {
		super();
		this.preferencesService = Platform.getPreferencesService();
	}

	/**
	 * Calls {@link IPreferencesService#getString(String, String, String, org.eclipse.core.runtime.preferences.IScopeContext[])}
	 * using {@link Plugin#ID} and a default ({@code null}) context order.
	 * @param key
	 * @return
	 */
	public String getString(PreferenceKey key) {
		return preferencesService.getString(Plugin.ID, key.toString(), (String) key.getDefaultValue(), null);
	}

	/**
	 * Calls {@link IPreferencesService#getBoolean(String, String, String, org.eclipse.core.runtime.preferences.IScopeContext[])}
	 * using {@link Plugin#ID} and a default ({@code null}) context order.
	 *
	 * @param key
	 * @return
	 */
	public boolean getBoolean(PreferenceKey key) {
		return preferencesService.getBoolean(Plugin.ID, key.toString(), ((Boolean) key.getDefaultValue()).booleanValue(), null);
	}

	/**
	 * Calls {@link IPreferencesService#getString(String, String, String, org.eclipse.core.runtime.preferences.IScopeContext[])}
	 * using {@link Plugin#ID} and a default ({@code null}) context order and then converts
	 * the returned {@link String} to {@link Color} using {@link StringConverter#asRGB(String)}.
	 *
	 * @param preferenceKey
	 * @return
	 */
	public Color getColor(PreferenceKey preferenceKey) {
		String stringColor = getString(preferenceKey);
		Color color = colorCache.get(stringColor);
		if (color == null) {
			RGB rgb = StringConverter.asRGB(stringColor);
			if (rgb == null) {
				rgb = new RGB(0,0,0);
			}
			color = new Color(Display.getCurrent(), rgb);
			colorCache.put(stringColor, color);
		}
		return color;
	}

	/**
	 * Releases the cached {@link Color}s.
	 */
	public void dispose() {
		for (Color color : this.colorCache.values()) {
			color.dispose();
		}
		this.colorCache = null;
		this.preferencesService = null;
	}

}
