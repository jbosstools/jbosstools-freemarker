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
package org.jboss.ide.eclipse.freemarker.linetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.jboss.ide.eclipse.freemarker.Plugin;

/**
 * @author <a href="mailto:joe&binamics.net">Joe Hudson </a>
 */
public class ConsoleLineTracker implements IConsoleLineTracker {

	private IConsole console;
	// Last updated for FreeMarker 2.3.25.
	private static final Pattern RUNTIME_ERROR_PATTERN = Pattern.compile(
			"\\[in template \"(.*)\" at line (\\d+), column (\\d+)\\]"); //$NON-NLS-1$
	private static final Pattern PARSE_TIME_PATTERN = Pattern.compile(
			" freemarker.+in template \"(.*)\" (?:in|at) line (\\d+), column (\\d+)"); //$NON-NLS-1$

	@Override
	public void init(IConsole cons) {
		this.console = cons;
	}

	@Override
	public void lineAppended(IRegion line) {
		try {
			String text = console.getDocument().get(line.getOffset(), line.getLength());
			
			Matcher matcher = RUNTIME_ERROR_PATTERN.matcher(text);
			boolean found = matcher.find();
			if (!found) {
				matcher = PARSE_TIME_PATTERN.matcher(text);
				found = matcher.find();
			}
			
			if (found) {
				String fileName = matcher.group(1);
				int lineNumber;
				try {
					lineNumber = Integer.parseInt(matcher.group(2));
				} catch (NumberFormatException e1) {
					return; // Give up
				}
				int linkOffset = line.getOffset() + matcher.start(1) - 1;
				int linkLength = matcher.end(2) - matcher.start(1) + 1;

				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

				List<IResource> files = new ArrayList<IResource>();
				for (int i = 0; i < projects.length; i++) {
					IProject project = projects[i];
					// FIXME: javaProject is not used. Find out if it can be
					// removed or if it should be
					// used in populateMatchingFiles()
					IJavaProject javaProject = JavaCore.create(project);
					fileName = fileName.replace('\\', '/');
					try {
						populateMatchingFiles(project, files, fileName.split("/")); //$NON-NLS-1$
					} catch (CoreException e) {
						Plugin.log(e);
						// TODO log this exception
					}

					if (files.size() != 0) {
						IFile file = (IFile) files.get(0);
						if (file != null && file.exists()) {
							IHyperlink link = new FileLink(file, null, -1, -1, lineNumber);
							console.addLink(link, linkOffset, linkLength);
						}
					}
				}
			}
		} catch (BadLocationException e) {
			Plugin.log(e);
		}
	}

	public void populateMatchingFiles(IContainer container, List<IResource> files, String[] fileNameSeq) throws CoreException {
		IResource[] resources = container.members();
		for (int i=0; i<resources.length; i++) {
			IResource resource = resources[i];
			if (resource instanceof IContainer) {
				populateMatchingFiles((IContainer) resource, files, fileNameSeq);
			}
			else if (resource instanceof IFile) {
				if (isCorrectFile((IFile) resource, fileNameSeq)) {
					boolean doAdd = true;
					try {
						IJavaProject javaProject = JavaCore.create(resource.getProject());
						if (javaProject.getOutputLocation().isPrefixOf(((IFile) resource).getFullPath())) doAdd = false;
					}
					catch (JavaModelException e) {
						Plugin.log(e);
					}
					if (doAdd) files.add(resource);
				}
			}
		}
	}

	private static boolean isCorrectFile(IFile file, String[] filenameSeqs) {
		IResource temp = file;
		for (int i = filenameSeqs.length - 1; i >= 0; i--) {
			String seq = filenameSeqs[i];
			if (!seq.equals(temp.getName())) {
				return false;
			}
			temp = temp.getParent();
		}
		return true;
	}

	@Override
	public void dispose() {
	}
}