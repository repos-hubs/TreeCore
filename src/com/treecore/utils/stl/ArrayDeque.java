package com.treecore.utils.stl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>,
		Cloneable, Serializable {
	private transient E[] elements;
	private transient int head;
	private transient int tail;
	private static final int MIN_INITIAL_CAPACITY = 8;
	private static final long serialVersionUID = 2340985798034038923L;

	private void allocateElements(int numElements) {
		int initialCapacity = 8;

		if (numElements >= initialCapacity) {
			initialCapacity = numElements;
			initialCapacity |= initialCapacity >>> 1;
			initialCapacity |= initialCapacity >>> 2;
			initialCapacity |= initialCapacity >>> 4;
			initialCapacity |= initialCapacity >>> 8;
			initialCapacity |= initialCapacity >>> 16;
			initialCapacity++;

			if (initialCapacity < 0)
				initialCapacity >>>= 1;
		}
		this.elements = new Object[initialCapacity];
	}

	private void doubleCapacity() {
		assert (this.head == this.tail);
		int p = this.head;
		int n = this.elements.length;
		int r = n - p;
		int newCapacity = n << 1;
		if (newCapacity < 0)
			throw new IllegalStateException("Sorry, deque too big");
		Object[] a = new Object[newCapacity];
		System.arraycopy(this.elements, p, a, 0, r);
		System.arraycopy(this.elements, 0, a, r, p);
		this.elements = a;
		this.head = 0;
		this.tail = n;
	}

	private <T> T[] copyElements(T[] a) {
		if (this.head < this.tail) {
			System.arraycopy(this.elements, this.head, a, 0, size());
		} else if (this.head > this.tail) {
			int headPortionLen = this.elements.length - this.head;
			System.arraycopy(this.elements, this.head, a, 0, headPortionLen);
			System.arraycopy(this.elements, 0, a, headPortionLen, this.tail);
		}
		return a;
	}

	public ArrayDeque() {
		this.elements = new Object[16];
	}

	public ArrayDeque(int numElements) {
		allocateElements(numElements);
	}

	public ArrayDeque(Collection<? extends E> c) {
		allocateElements(c.size());
		addAll(c);
	}

	public void addFirst(E e) {
		if (e == null)
			throw new NullPointerException();
		int tmp31_30 = (this.head - 1 & this.elements.length - 1);
		this.head = tmp31_30;
		this.elements[tmp31_30] = e;
		if (this.head == this.tail)
			doubleCapacity();
	}

	public void addLast(E e) {
		if (e == null)
			throw new NullPointerException();
		this.elements[this.tail] = e;
		if ((this.tail = this.tail + 1 & this.elements.length - 1) == this.head)
			doubleCapacity();
	}

	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	public E removeFirst() {
		Object x = pollFirst();
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E removeLast() {
		Object x = pollLast();
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E pollFirst() {
		int h = this.head;
		Object result = this.elements[h];
		if (result == null)
			return null;
		this.elements[h] = null;
		this.head = (h + 1 & this.elements.length - 1);
		return result;
	}

	public E pollLast() {
		int t = this.tail - 1 & this.elements.length - 1;
		Object result = this.elements[t];
		if (result == null)
			return null;
		this.elements[t] = null;
		this.tail = t;
		return result;
	}

	public E getFirst() {
		Object x = this.elements[this.head];
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E getLast() {
		Object x = this.elements[(this.tail - 1 & this.elements.length - 1)];
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E peekFirst() {
		return this.elements[this.head];
	}

	public E peekLast() {
		return this.elements[(this.tail - 1 & this.elements.length - 1)];
	}

	public boolean removeFirstOccurrence(Object o) {
		if (o == null)
			return false;
		int mask = this.elements.length - 1;
		int i = this.head;
		Object x;
		while ((x = this.elements[i]) != null) {
			Object x;
			if (o.equals(x)) {
				delete(i);
				return true;
			}
			i = i + 1 & mask;
		}
		return false;
	}

	public boolean removeLastOccurrence(Object o) {
		if (o == null)
			return false;
		int mask = this.elements.length - 1;
		int i = this.tail - 1 & mask;
		Object x;
		while ((x = this.elements[i]) != null) {
			Object x;
			if (o.equals(x)) {
				delete(i);
				return true;
			}
			i = i - 1 & mask;
		}
		return false;
	}

	public boolean add(E e) {
		addLast(e);
		return true;
	}

	public boolean offer(E e) {
		return offerLast(e);
	}

	public E remove() {
		return removeFirst();
	}

	public E poll() {
		return pollFirst();
	}

	public E element() {
		return getFirst();
	}

	public E peek() {
		return peekFirst();
	}

	public void push(E e) {
		addFirst(e);
	}

	public E pop() {
		return removeFirst();
	}

	private void checkInvariants() {
		assert (this.elements[this.tail] == null);
		assert (this.head == this.tail ? this.elements[this.head] != null
				: (this.elements[this.head] != null)
						&& (this.elements[(this.tail - 1 & this.elements.length - 1)] != null));
		assert (this.elements[(this.head - 1 & this.elements.length - 1)] == null);
	}

	private boolean delete(int i) {
		checkInvariants();
		Object[] elements = this.elements;
		int mask = elements.length - 1;
		int h = this.head;
		int t = this.tail;
		int front = i - h & mask;
		int back = t - i & mask;

		if (front >= (t - h & mask)) {
			throw new ConcurrentModificationException();
		}

		if (front < back) {
			if (h <= i) {
				System.arraycopy(elements, h, elements, h + 1, front);
			} else {
				System.arraycopy(elements, 0, elements, 1, i);
				elements[0] = elements[mask];
				System.arraycopy(elements, h, elements, h + 1, mask - h);
			}
			elements[h] = null;
			this.head = (h + 1 & mask);
			return false;
		}
		if (i < t) {
			System.arraycopy(elements, i + 1, elements, i, back);
			this.tail = (t - 1);
		} else {
			System.arraycopy(elements, i + 1, elements, i, mask - i);
			elements[mask] = elements[0];
			System.arraycopy(elements, 1, elements, 0, t);
			this.tail = (t - 1 & mask);
		}
		return true;
	}

	public int size() {
		return this.tail - this.head & this.elements.length - 1;
	}

	public boolean isEmpty() {
		return this.head == this.tail;
	}

	public Iterator<E> iterator() {
		return new DeqIterator(null);
	}

	public Iterator<E> descendingIterator() {
		return new DescendingIterator(null);
	}

	public boolean contains(Object o) {
		if (o == null)
			return false;
		int mask = this.elements.length - 1;
		int i = this.head;
		Object x;
		while ((x = this.elements[i]) != null) {
			Object x;
			if (o.equals(x))
				return true;
			i = i + 1 & mask;
		}
		return false;
	}

	public boolean remove(Object o) {
		return removeFirstOccurrence(o);
	}

	public void clear() {
		int h = this.head;
		int t = this.tail;
		if (h != t) {
			this.head = (this.tail = 0);
			int i = h;
			int mask = this.elements.length - 1;
			do {
				this.elements[i] = null;
				i = i + 1 & mask;
			} while (i != t);
		}
	}

	public Object[] toArray() {
		return copyElements(new Object[size()]);
	}

	public <T> T[] toArray(T[] a) {
		int size = size();
		if (a.length < size)
			a = (Object[]) Array.newInstance(a.getClass().getComponentType(),
					size);
		copyElements(a);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	public ArrayDeque<E> clone() {
		try {
			ArrayDeque result = (ArrayDeque) super.clone();
			result.elements = Arrays
					.copyOf(this.elements, this.elements.length);
			return result;
		} catch (CloneNotSupportedException e) {
		}
		throw new AssertionError();
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		s.writeInt(size());

		int mask = this.elements.length - 1;
		for (int i = this.head; i != this.tail; i = i + 1 & mask)
			s.writeObject(this.elements[i]);
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();

		int size = s.readInt();
		allocateElements(size);
		this.head = 0;
		this.tail = size;

		for (int i = 0; i < size; i++)
			this.elements[i] = s.readObject();
	}

	private class DeqIterator implements Iterator<E> {
		private int cursor = ArrayDeque.this.head;

		private int fence = ArrayDeque.this.tail;

		private int lastRet = -1;

		private DeqIterator() {
		}

		public boolean hasNext() {
			return this.cursor != this.fence;
		}

		public E next() {
			if (this.cursor == this.fence)
				throw new NoSuchElementException();
			Object result = ArrayDeque.this.elements[this.cursor];

			if ((ArrayDeque.this.tail != this.fence) || (result == null))
				throw new ConcurrentModificationException();
			this.lastRet = this.cursor;
			this.cursor = (this.cursor + 1 & ArrayDeque.this.elements.length - 1);
			return result;
		}

		public void remove() {
			if (this.lastRet < 0)
				throw new IllegalStateException();
			if (ArrayDeque.this.delete(this.lastRet)) {
				this.cursor = (this.cursor - 1 & ArrayDeque.this.elements.length - 1);
				this.fence = ArrayDeque.this.tail;
			}
			this.lastRet = -1;
		}
	}

	private class DescendingIterator implements Iterator<E> {
		private int cursor = ArrayDeque.this.tail;
		private int fence = ArrayDeque.this.head;
		private int lastRet = -1;

		private DescendingIterator() {
		}

		public boolean hasNext() {
			return this.cursor != this.fence;
		}

		public E next() {
			if (this.cursor == this.fence)
				throw new NoSuchElementException();
			this.cursor = (this.cursor - 1 & ArrayDeque.this.elements.length - 1);
			Object result = ArrayDeque.this.elements[this.cursor];
			if ((ArrayDeque.this.head != this.fence) || (result == null))
				throw new ConcurrentModificationException();
			this.lastRet = this.cursor;
			return result;
		}

		public void remove() {
			if (this.lastRet < 0)
				throw new IllegalStateException();
			if (!ArrayDeque.this.delete(this.lastRet)) {
				this.cursor = (this.cursor + 1 & ArrayDeque.this.elements.length - 1);
				this.fence = ArrayDeque.this.head;
			}
			this.lastRet = -1;
		}
	}
}