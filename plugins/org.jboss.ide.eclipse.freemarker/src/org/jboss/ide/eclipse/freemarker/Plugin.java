/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.ide.eclipse.freemarker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class Plugin extends AbstractUIPlugin {

	public static final String ID = "org.jboss.ide.eclipse.freemarker";
	
	//The shared instance.
	private static Plugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	public Plugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.jboss.ide.eclipse.freemarker.resources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * The constructor.
	 */
	public Plugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Plugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static Plugin getInstance() {
		return plugin;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Initializes the plugin preferences with default preference values for
	 * this plug-in.
	 */
	protected void initializeDefaultPluginPreferences() {
		Preferences prefs = getPluginPreferences();
		prefs.setDefault(Constants.HIGHLIGHT_RELATED_ITEMS, true);
		prefs.setDefault(Constants.COLOR_COMMENT, "170,0,0");
		prefs.setDefault(Constants.COLOR_TEXT, "0,0,0");
		prefs.setDefault(Constants.COLOR_INTERPOLATION, "255,0,128");
		prefs.setDefault(Constants.COLOR_DIRECTIVE, "0,0,255");
		prefs.setDefault(Constants.COLOR_STRING, "0,128,128");
		prefs.setDefault(Constants.COLOR_XML_COMMENT, "128,128,128");
		prefs.setDefault(Constants.COLOR_XML_TAG, "0,0,128");
		prefs.setDefault(Constants.COLOR_RELATED_ITEM, "255,255,128");
	}

	public static void error (Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		MessageDialog.openError(Display.getCurrent().getActiveShell(), t.getMessage(), sw.toString());
		log(t);
	}

	public static void log (Throwable t) {
		log("Error: " + t.getMessage() + "\n");
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		log("Trace: " + sw.toString());
		
	}

	public static void log (String s) {
		// TODO figure out a good place to log messages
	}
}