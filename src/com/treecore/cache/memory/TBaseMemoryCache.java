package com.treecore.cache.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class TBaseMemoryCache<K, V> implements
		TIMemoryCacheAware<K, V> {
	private final Map<K, Reference<V>> softMap = Collections
			.synchronizedMap(new HashMap());

	public V get(K key) {
		Object result = null;
		Reference reference = (Reference) this.softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return (V) result;
	}

	public boolean put(K key, V value) {
		this.softMap.put(key, createReference(value));
		return true;
	}

	public void remove(K key) {
		this.softMap.remove(key);
	}

	public Collection<K> keys() {
		synchronized (this.softMap) {
			return new HashSet(this.softMap.keySet());
		}
	}

	public void clear() {
		this.softMap.clear();
	}

	protected abstract Reference<V> createReference(V paramV);
}