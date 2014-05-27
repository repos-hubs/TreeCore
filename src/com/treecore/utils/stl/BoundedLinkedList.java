package com.treecore.utils.stl;

import java.util.Collection;
import java.util.LinkedList;

public class BoundedLinkedList<E> extends LinkedList<E> {
	private final int maxSize;

	public BoundedLinkedList(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean add(E object) {
		if (size() == this.maxSize) {
			removeFirst();
		}
		return super.add(object);
	}

	public void add(int location, E object) {
		if (size() == this.maxSize) {
			removeFirst();
		}
		super.add(location, object);
	}

	public boolean addAll(Collection<? extends E> collection) {
		int totalNeededSize = size() + collection.size();
		int overhead = totalNeededSize - this.maxSize;
		if (overhead > 0) {
			removeRange(0, overhead);
		}
		return super.addAll(collection);
	}

	public boolean addAll(int location, Collection<? extends E> collection) {
		throw new UnsupportedOperationException();
	}

	public void addFirst(E object) {
		throw new UnsupportedOperationException();
	}

	public void addLast(E object) {
		add(object);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Object object : this) {
			result.append(object.toString());
		}
		return result.toString();
	}
}