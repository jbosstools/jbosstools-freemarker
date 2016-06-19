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
package org.jboss.ide.eclipse.freemarker.model.interpolation;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class BuiltInEditorInfo {

	private Class<?> returnClass;
	private boolean parametersRequired;

	public BuiltInEditorInfo (Class<?> returnClass, boolean requireParameters) {
		this.returnClass = returnClass;
		this.parametersRequired = requireParameters;
	}

	public Class<?> getReturnClass() {
		return returnClass;
	}

	public boolean isParametersRequired() {
		return parametersRequired;
	}
}
