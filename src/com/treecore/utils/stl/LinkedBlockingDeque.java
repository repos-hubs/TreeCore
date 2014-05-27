package com.treecore.utils.stl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements
		BlockingDeque<E>, Serializable {
	private static final long serialVersionUID = -387911632671998426L;
	transient Node<E> first;
	transient Node<E> last;
	private transient int count;
	private final int capacity;
	final ReentrantLock lock = new ReentrantLock();

	private final Condition notEmpty = this.lock.newCondition();

	private final Condition notFull = this.lock.newCondition();

	public LinkedBlockingDeque() {
		this(2147483647);
	}

	public LinkedBlockingDeque(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
	}

	public LinkedBlockingDeque(Collection<? extends E> c) {
		this(2147483647);
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for (Object e : c) {
				if (e == null)
					throw new NullPointerException();
				if (!linkLast(new Node(e)))
					throw new IllegalStateException("Deque full");
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean linkFirst(Node<E> node) {
		if (this.count >= this.capacity)
			return false;
		Node f = this.first;
		node.next = f;
		this.first = node;
		if (this.last == null)
			this.last = node;
		else
			f.prev = node;
		this.count += 1;
		this.notEmpty.signal();
		return true;
	}

	private boolean linkLast(Node<E> node) {
		if (this.count >= this.capacity)
			return false;
		Node l = this.last;
		node.prev = l;
		this.last = node;
		if (this.first == null)
			this.first = node;
		else
			l.next = node;
		this.count += 1;
		this.notEmpty.signal();
		return true;
	}

	private E unlinkFirst() {
		Node f = this.first;
		if (f == null)
			return null;
		Node n = f.next;
		Object item = f.item;
		f.item = null;
		f.next = f;
		this.first = n;
		if (n == null)
			this.last = null;
		else
			n.prev = null;
		this.count -= 1;
		this.notFull.signal();
		return item;
	}

	private E unlinkLast() {
		Node l = this.last;
		if (l == null)
			return null;
		Node p = l.prev;
		Object item = l.item;
		l.item = null;
		l.prev = l;
		this.last = p;
		if (p == null)
			this.first = null;
		else
			p.next = null;
		this.count -= 1;
		this.notFull.signal();
		return item;
	}

	void unlink(Node<E> x) {
		Node p = x.prev;
		Node n = x.next;
		if (p == null) {
			unlinkFirst();
		} else if (n == null) {
			unlinkLast();
		} else {
			p.next = n;
			n.prev = p;
			x.item = null;

			this.count -= 1;
			this.notFull.signal();
		}
	}

	public void addFirst(E e) {
		if (!offerFirst(e))
			throw new IllegalStateException("Deque full");
	}

	public void addLast(E e) {
		if (!offerLast(e))
			throw new IllegalStateException("Deque full");
	}

	public boolean offerFirst(E e) {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return linkFirst(node);
		} finally {
			lock.unlock();
		}
	}

	public boolean offerLast(E e) {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return linkLast(node);
		} finally {
			lock.unlock();
		}
	}

	public void putFirst(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while (!linkFirst(node))
				this.notFull.await();
		} finally {
			lock.unlock();
		}
	}

	public void putLast(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			while (!linkLast(node))
				this.notFull.await();
		} finally {
			lock.unlock();
		}
	}

	public boolean offerFirst(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		long nanos = unit.toNanos(timeout);
		ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (!linkFirst(node)) {
				if (nanos <= 0L)
					return false;
				nanos = this.notFull.awaitNanos(nanos);
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	public boolean offerLast(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		Node node = new Node(e);
		long nanos = unit.toNanos(timeout);
		ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			while (!linkLast(node)) {
				if (nanos <= 0L)
					return false;
				nanos = this.notFull.awaitNanos(nanos);
			}
			return true;
		} finally {
			lock.unlock();
		}
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
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return unlinkFirst();
		} finally {
			lock.unlock();
		}
	}

	public E pollLast() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return unlinkLast();
		} finally {
			lock.unlock();
		}
	}

	public E takeFirst() throws InterruptedException {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object x;
			while ((x = unlinkFirst()) == null) {
				Object x;
				this.notEmpty.await();
			}
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E takeLast() throws InterruptedException {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object x;
			while ((x = unlinkLast()) == null) {
				Object x;
				this.notEmpty.await();
			}
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			Object x;
			while ((x = unlinkFirst()) == null) {
				Object x;
				if (nanos <= 0L)
					return null;
				nanos = this.notEmpty.awaitNanos(nanos);
			}
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			Object x;
			while ((x = unlinkLast()) == null) {
				Object x;
				if (nanos <= 0L)
					return null;
				nanos = this.notEmpty.awaitNanos(nanos);
			}
			return x;
		} finally {
			lock.unlock();
		}
	}

	public E getFirst() {
		Object x = peekFirst();
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E getLast() {
		Object x = peekLast();
		if (x == null)
			throw new NoSuchElementException();
		return x;
	}

	public E peekFirst() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return this.first == null ? null : this.first.item;
		} finally {
			lock.unlock();
		}
	}

	public E peekLast() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return this.last == null ? null : this.last.item;
		} finally {
			lock.unlock();
		}
	}

	public boolean removeFirstOccurrence(Object o) {
		if (o == null)
			return false;
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for (Node p = this.first; p != null; p = p.next) {
				if (o.equals(p.item)) {
					unlink(p);
					return true;
				}
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean removeLastOccurrence(Object o) {
		if (o == null)
			return false;
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for (Node p = this.last; p != null; p = p.prev) {
				if (o.equals(p.item)) {
					unlink(p);
					return true;
				}
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean add(E e) {
		addLast(e);
		return true;
	}

	public boolean offer(E e) {
		return offerLast(e);
	}

	public void put(E e) throws InterruptedException {
		putLast(e);
	}

	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		return offerLast(e, timeout, unit);
	}

	public E remove() {
		return removeFirst();
	}

	public E poll() {
		return pollFirst();
	}

	public E take() throws InterruptedException {
		return takeFirst();
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return pollFirst(timeout, unit);
	}

	public E element() {
		return getFirst();
	}

	public E peek() {
		return peekFirst();
	}

	public int remainingCapacity() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return this.capacity - this.count;
		} finally {
			lock.unlock();
		}
	}

	public int drainTo(Collection<? super E> c) {
		return drainTo(c, 2147483647);
	}

	public int drainTo(Collection<? super E> c, int maxElements) {
		if (c == null)
			throw new NullPointerException();
		if (c == this)
			throw new IllegalArgumentException();
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int n = Math.min(maxElements, this.count);
			for (int i = 0; i < n; i++) {
				c.add(this.first.item);
				unlinkFirst();
			}
			return n;
		} finally {
			lock.unlock();
		}
	}

	public void push(E e) {
		addFirst(e);
	}

	public E pop() {
		return removeFirst();
	}

	public boolean remove(Object o) {
		return removeFirstOccurrence(o);
	}

	public int size() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return this.count;
		} finally {
			lock.unlock();
		}
	}

	public boolean contains(Object o) {
		if (o == null)
			return false;
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for (Node p = this.first; p != null; p = p.next)
				if (o.equals(p.item))
					return true;
			return false;
		} finally {
			lock.unlock();
		}
	}

	public Object[] toArray() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Object[] a = new Object[this.count];
			int k = 0;
			for (Node p = this.first; p != null; p = p.next)
				a[(k++)] = p.item;
			return a;
		} finally {
			lock.unlock();
		}
	}

	public <T> T[] toArray(T[] a) {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (a.length < this.count) {
				a = (Object[]) Array.newInstance(a.getClass()
						.getComponentType(), this.count);
			}
			int k = 0;
			for (Node p = this.first; p != null; p = p.next)
				a[(k++)] = p.item;
			if (a.length > k)
				a[k] = null;
			return a;
		} finally {
			lock.unlock();
		}
	}

	public String toString() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			Node p = this.first;
			if (p == null) {
				return "[]";
			}
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			while (true) {
				Object e = p.item;
				sb.append(e == this ? "(this Collection)" : e);
				p = p.next;
				if (p == null)
					return ']';
				sb.append(',').append(' ');
			}
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for (Node f = this.first; f != null;) {
				f.item = null;
				Node n = f.next;
				f.prev = null;
				f.next = null;
				f = n;
			}
			this.first = (this.last = null);
			this.count = 0;
			this.notFull.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public Iterator<E> iterator() {
		return new Itr(null);
	}

	public Iterator<E> descendingIterator() {
		return new DescendingItr(null);
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			s.defaultWriteObject();

			for (Node p = this.first; p != null; p = p.next) {
				s.writeObject(p.item);
			}
			s.writeObject(null);
		} finally {
			lock.unlock();
		}
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();
		this.count = 0;
		this.first = null;
		this.last = null;
		while (true) {
			Object item = s.readObject();
			if (item == null)
				break;
			add(item);
		}
	}

	private abstract class AbstractItr implements Iterator<E> {
		LinkedBlockingDeque.Node<E> next;
		E nextItem;
		private LinkedBlockingDeque.Node<E> lastRet;

		abstract LinkedBlockingDeque.Node<E> firstNode();

		abstract LinkedBlockingDeque.Node<E> nextNode(
				LinkedBlockingDeque.Node<E> paramNode);

		AbstractItr() {
			ReentrantLock lock = LinkedBlockingDeque.this.lock;
			lock.lock();
			try {
				this.next = firstNode();
				this.nextItem = (this.next == null ? null : this.next.item);
			} finally {
				lock.unlock();
			}
		}

		private LinkedBlockingDeque.Node<E> succ(LinkedBlockingDeque.Node<E> n) {
			while (true) {
				LinkedBlockingDeque.Node s = nextNode(n);
				if (s == null)
					return null;
				if (s.item != null)
					return s;
				if (s == n) {
					return firstNode();
				}
				n = s;
			}
		}

		void advance() {
			ReentrantLock lock = LinkedBlockingDeque.this.lock;
			lock.lock();
			try {
				this.next = succ(this.next);
				this.nextItem = (this.next == null ? null : this.next.item);
			} finally {
				lock.unlock();
			}
		}

		public boolean hasNext() {
			return this.next != null;
		}

		public E next() {
			if (this.next == null)
				throw new NoSuchElementException();
			this.lastRet = this.next;
			Object x = this.nextItem;
			advance();
			return x;
		}

		public void remove() {
			LinkedBlockingDeque.Node n = this.lastRet;
			if (n == null)
				throw new IllegalStateException();
			this.lastRet = null;
			ReentrantLock lock = LinkedBlockingDeque.this.lock;
			lock.lock();
			try {
				if (n.item != null)
					LinkedBlockingDeque.this.unlink(n);
			} finally {
				lock.unlock();
			}
		}
	}

	private class DescendingItr extends LinkedBlockingDeque<E>.AbstractItr {
		private DescendingItr() {
			super();
		}

		LinkedBlockingDeque.Node<E> firstNode() {
			return LinkedBlockingDeque.this.last;
		}

		LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> n) {
			return n.prev;
		}
	}

	private class Itr extends LinkedBlockingDeque<E>.AbstractItr {
		private Itr() {
			super();
		}

		LinkedBlockingDeque.Node<E> firstNode() {
			return LinkedBlockingDeque.this.first;
		}

		LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> n) {
			return n.next;
		}
	}

	static final class Node<E> {
		E item;
		Node<E> prev;
		Node<E> next;

		Node(E x) {
			this.item = x;
		}
	}
}