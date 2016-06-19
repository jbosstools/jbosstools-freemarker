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
package org.jboss.ide.eclipse.freemarker.model.interpolation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class BuiltInFragment extends AbstractFragment {

	private static final Map<String, BuiltInEditorInfo> STRING_BUILT_INS = createMap(FTLType.STRING);
	private static final Map<String, BuiltInEditorInfo> NUMBER_BUILT_INS = createMap(FTLType.NUMBER);
	private static final Map<String, BuiltInEditorInfo> DATE_BUILT_INS = createMap(FTLType.DATE_LIKE);
	private static final Map<String, BuiltInEditorInfo> LIST_BUILT_INS = createMap(FTLType.SEQUENCE);
	private static final Map<String, BuiltInEditorInfo> MAP_BUILT_INS = createMap(FTLType.HASH);
    private static final Map<String, BuiltInEditorInfo> NODE_BUILT_INS = createMap(FTLType.NODE);
	private static final Map<String, BuiltInEditorInfo> ALL_BUILT_INS = createMap(FTLType.ANY);
	
	private static final Map<String, BuiltInEditorInfo> createMap(FTLType lhoType) {
	    HashMap<String, BuiltInEditorInfo> map = new HashMap<String, BuiltInEditorInfo>();
	    for (BuiltInInfo bi : BuiltInInfo.values()) {
	        if (bi.isDeprecated()) {
	            continue;
	        }
	        if (isBuiltInOf(bi, lhoType)) {
	            map.put(bi.getSnakeCaseName(),
	                    new BuiltInEditorInfo(
	                            bi.getReturnType().getClosestJavaClass(), bi.isParameterListRequired()));
	        }
	    }
	    return map;
	}

    private static final boolean isBuiltInOf(BuiltInInfo bi, FTLType actualLhoType) {
        if (actualLhoType == FTLType.ANY) {
            return true;
        }
        for (FTLType supportedLhoType : bi.getLeftHandTypes()) {
            if (supportedLhoType == FTLType.ANY) {
                return true;
            }
            if (actualLhoType == supportedLhoType) {
                return true;
            }
        }
        return false;
    }
	
	public BuiltInFragment(int offset, String content) {
		super(offset, content);
	}

	@Override
	public Class<?> getReturnClass (Class<?> parentClass, List<Fragment> fragments, Map<String, Class<?>> context,
	        IResource resource, IProject project) {
		BuiltInEditorInfo biEditorInfo = ALL_BUILT_INS.get(getBuiltInName());
		return biEditorInfo != null ? biEditorInfo.getReturnClass() : null;
	}

    private String builtInNameOrNull;
	
    private String getBuiltInName() {
        if (null == builtInNameOrNull) {
			builtInNameOrNull = getContent();
			int index = builtInNameOrNull.indexOf("("); //$NON-NLS-1$
			if (index > 0) builtInNameOrNull = builtInNameOrNull.substring(0, index);
		}
        return builtInNameOrNull;
    }

	@Override
	public ICompletionProposal[] getCompletionProposals (int subOffset, int offset, Class<?> parentClass,
			List<Fragment> fragments, ISourceViewer sourceViewer, Map<String, Class<?>> context, IResource file, IProject project) {
		if (instanceOf(parentClass, String.class)) {
			return getCompletionProposals(subOffset, offset, STRING_BUILT_INS);
		}
		else if (instanceOf(parentClass, Number.class)) {
			return getCompletionProposals(subOffset, offset, NUMBER_BUILT_INS);
		}
		else if (instanceOf(parentClass, Date.class)) {
			return getCompletionProposals(subOffset, offset, DATE_BUILT_INS);
		}
		else if (instanceOf(parentClass, Collection.class) || instanceOf(parentClass, List.class)
		        || parentClass != null && parentClass.isArray()) {
			return getCompletionProposals(subOffset, offset, LIST_BUILT_INS);
		}
		else if (instanceOf(parentClass, Map.class)) {
			return getCompletionProposals(subOffset, offset, MAP_BUILT_INS);
		}
        else if (instanceOf(parentClass, Node.class)) {
            return getCompletionProposals(subOffset, offset, NODE_BUILT_INS);
        }
		else return getCompletionProposals(subOffset, offset, ALL_BUILT_INS);
	}

	private ICompletionProposal[] getCompletionProposals(
	        int subOffset, int offset, Map<String, BuiltInEditorInfo> builtInsSubset) {
		if (offset == 0) return null;
		String prefix = getContent().substring(0, subOffset-1);
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (Map.Entry<String, BuiltInEditorInfo> entry : builtInsSubset.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(prefix)) {
				proposals.add(getCompletionProposal(key, entry.getValue(), offset, subOffset));
			}
		}
		return completionProposals(proposals);
	}

	private ICompletionProposal getCompletionProposal(String key, BuiltInEditorInfo biEditorInfo, int offset, int subOffset) {
		if (!biEditorInfo.isParametersRequired()) {
			return getCompletionProposal(offset, subOffset-1,
					key, getContent());
		}
		else {
			String replacementString = key + "()"; //$NON-NLS-1$
			return new CompletionProposal (
					replacementString, offset-subOffset+1,
					getContent().length(), replacementString.length()-1);
		}
	}

}