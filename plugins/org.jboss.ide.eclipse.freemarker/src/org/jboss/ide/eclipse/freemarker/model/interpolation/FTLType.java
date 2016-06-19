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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

public enum FTLType {
    
    STRING(String.class), BOOLEAN(Boolean.class), NUMBER(Number.class), DATE_LIKE(Date.class), SEQUENCE(List.class),
    HASH(Map.class), NODE(Node.class), MARKUP_OUTPUT(Object.class), DIRECTIVE(Object.class), FUNCTION(Object.class),
    MACRO_OR_FUNCTION(Object.class), REG_EXP_MATCH(Object.class), LOOP_VAR(Object.class),
    ANY(Object.class);

    private final Class<?> closestJavaClass;
    
    public Class<?> getClosestJavaClass() {
        return closestJavaClass;
    }

    private FTLType(Class<?> closestJavaClass) {
        this.closestJavaClass = closestJavaClass;
    }

}
