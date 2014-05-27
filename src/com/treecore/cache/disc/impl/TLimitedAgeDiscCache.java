package com.treecore.cache.disc.impl;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.TBaseDiscCache;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TLimitedAgeDiscCache extends TBaseDiscCache {
	private final long maxFileAge;
	private final Map<File, Long> loadingDates = Collections
			.synchronizedMap(new HashMap());

	public TLimitedAgeDiscCache(File cacheDir, long maxAge) {
		this(cacheDir, TCacheManager.createFileNameGenerator(), maxAge);
	}

	public TLimitedAgeDiscCache(File cacheDir,
			TIFileNameGenerator fileNameGenerator, long maxAge) {
		super(cacheDir, fileNameGenerator);
		this.maxFileAge = (maxAge * 1000L);
	}

	public void put(String key, File file) {
		long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		this.loadingDates.put(file, Long.valueOf(currentTime));
	}

	public File get(String key) {
		File file = super.get(key);
		if (file.exists()) {
			Long loadingDate = (Long) this.loadingDates.get(file);
			boolean cached;
			if (loadingDate == null) {
				boolean cached = false;
				loadingDate = Long.valueOf(file.lastModified());
			} else {
				cached = true;
			}

			if (System.currentTimeMillis() - loadingDate.longValue() > this.maxFileAge) {
				file.delete();
				this.loadingDates.remove(file);
			} else if (!cached) {
				this.loadingDates.put(file, loadingDate);
			}
		}
		return file;
	}
}