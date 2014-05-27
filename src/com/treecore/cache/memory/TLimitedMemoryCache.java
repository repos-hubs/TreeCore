package com.treecore.cache.memory;

import com.treecore.utils.log.TLog;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TLimitedMemoryCache<K, V> extends TBaseMemoryCache<K, V> {
	private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
	private static final int MAX_NORMAL_CACHE_SIZE = 16777216;
	private final int sizeLimit;
	private final AtomicInteger cacheSize;
	private final List<V> hardCache = Collections
			.synchronizedList(new LinkedList());

	public TLimitedMemoryCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;
		this.cacheSize = new AtomicInteger();
		if (sizeLimit > 16777216)
			TLog.w(this, String.format(
					"You set too large memory cache size (more than %1$d Mb)",
					new Object[] { Integer.valueOf(16) }));
	}

	public boolean put(K key, V value) {
		boolean putSuccessfully = false;

		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		int curCacheSize = this.cacheSize.get();
		if (valueSize < sizeLimit) {
			while (curCacheSize + valueSize > sizeLimit) {
				Object removedValue = removeNext();
				if (this.hardCache.remove(removedValue)) {
					curCacheSize = this.cacheSize
							.addAndGet(-getSize((V) removedValue));
				}
			}
			this.hardCache.add(value);
			this.cacheSize.addAndGet(valueSize);

			putSuccessfully = true;
		}

		super.put(key, value);
		return putSuccessfully;
	}

	public void remove(K key) {
		Object value = super.get(key);
		if ((value != null) && (this.hardCache.remove(value))) {
			this.cacheSize.addAndGet(-getSize((V) value));
		}

		super.remove(key);
	}

	public void clear() {
		this.hardCache.clear();
		this.cacheSize.set(0);
		super.clear();
	}

	protected int getSizeLimit() {
		return this.sizeLimit;
	}

	protected abstract int getSize(V paramV);

	protected abstract V removeNext();
}