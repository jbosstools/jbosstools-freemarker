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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public abstract class AbstractFragment implements Fragment {

	private int offset;
	private String content;

	public AbstractFragment (int offset, String content) {
		this.content = content;
		this.offset = offset;
	}

	@Override
	public int getLength() {
		return content.length();
	}

	@Override
	public int getOffset() {
		return offset;
	}

	public String getContent() {
		return content;
	}

	public ICompletionProposal[] completionProposals (List<ICompletionProposal> l) {

		Collections.sort(l, COMPLETION_PROPOSAL_COMPARATOR);
		return l.toArray(new ICompletionProposal[l.size()]);
	}

	public ICompletionProposal getCompletionProposal (int offset, int subOffset,
			String replacementString, String replacingString) {
		return new CompletionProposal (
				replacementString, offset-subOffset,
				replacingString.length(), replacementString.length());
	}

	protected boolean instanceOf (Class<?> test, Class<?> base) {
		if (null == test || null == base) return false;
		while (null != test) {
			for (int i=0; i<test.getInterfaces().length; i++) {
				if (test.getInterfaces()[i].getClass().getName().equals(base.getName())) return true;
			}
			if (test.getName().equals(base.getName())) return true;
			test = test.getSuperclass();
		}
		return false;
	}

	private static final Comparator<ICompletionProposal> COMPLETION_PROPOSAL_COMPARATOR = new Comparator<ICompletionProposal>() {
		@Override
		public int compare(ICompletionProposal arg0, ICompletionProposal arg1) {
			return arg0.getDisplayString().compareTo(arg1.getDisplayString());
		}
	};

	@Override
	public Class<?> getSingularReturnClass(Class<?> parentClass, List<Fragment> fragments, Map<String, Class<?>> context, IResource resource, IProject project) {
		return Object.class;
	}
}