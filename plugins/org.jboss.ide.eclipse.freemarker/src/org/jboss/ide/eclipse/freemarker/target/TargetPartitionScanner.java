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
package org.jboss.ide.eclipse.freemarker.target;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;

/**
 * An {@link IPartitionTokenScanner} with a partition type stack.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 *
 */
public interface TargetPartitionScanner extends IPartitionTokenScanner {

	/**
	 * {@link IPredicateRule}s should push their {@code partitionType} using
	 * this method whenever they encounter a start of a partition.
	 *
	 * @param partitionType
	 */
	void pushPartitionType(String partitionType);

	/**
	 * @return the partitionType on top of the internal stack or
	 *         {@code null} if the stack was empty.
	 */
	String peekPartitionType();

	/**
	 * {@link IPredicateRule}s should call this method whenever they encounter
	 * an end of a partition.
	 *
	 * @return the partitionType just removed from the internal stack or
	 *         {@code null} if the stack was empty.
	 */
	String popPartitionType();

}
