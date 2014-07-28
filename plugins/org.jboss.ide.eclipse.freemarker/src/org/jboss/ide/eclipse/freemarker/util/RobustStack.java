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
package org.jboss.ide.eclipse.freemarker.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A stack that returns {@link #defautElement} from {@link #pop()} and
 * {@link #peek()} rather than throwning an {@link EmptyStackException} when
 * empty.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 *
 * @param <E>
 *            the element type
 */
/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 * @since 1.4.0
 *
 * @param <E>
 */
public class RobustStack<E> {
	private final List<E> store;
	private final E defautElement;

	/**
	 * @param defautElement
	 *            the element to return from {@link #pop()} and {@link #peek()}
	 *            when this {@link RobustStack} is empty.
	 */
	public RobustStack(E defautElement) {
		super();
		this.defautElement = defautElement;
		this.store = new ArrayList<E>();
	}

	/**
	 * Removes all elements from this stack.
	 */
	public void clear() {
		store.clear();
	}

	/**
	 * @param element the element to check
	 * @return {@code true} is this {@link RobustStack} contains the given
	 *         {@code element} and {@code false} otherwise
	 */
	public boolean contains(E element) {
		return store.contains(element);
	}

	/**
	 * @return the element on top of this {@link RobustStack} or
	 *         {@link #defautElement} if this {@link RobustStack} is empty.
	 */
	public E peek() {
		return store.size() > 0 ? store.get(store.size() - 1) : defautElement;
	}

	/**
	 * If this {@link RobustStack} is empty, just returns the
	 * {@link #defautElement}; otherwise removes the top element from this
	 * {@link RobustStack} and returns it.
	 *
	 * @return the element on top of this {@link RobustStack} or
	 *         {@link #defautElement} if this {@link RobustStack} is empty.
	 */
	public E pop() {
		return store.size() > 0 ? store.remove(store.size() - 1)
				: defautElement;
	}

	/**
	 * Push the given element on top of this {@link RobustStack}.
	 *
	 * @param element
	 *            the element to push
	 */
	public void push(E element) {
		store.add(element);
	}

	/**
	 * @return the number of elements stored in this {@link RobustStack}.
	 */
	public int size() {
		return store.size();
	}

}
