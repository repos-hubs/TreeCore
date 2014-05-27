package com.treecore.cache;

import android.graphics.Bitmap;
import android.text.TextUtils;
import com.treecore.cache.disc.TIDiscCacheAware;
import com.treecore.cache.disc.impl.TFileCountLimitedDiscCache;
import com.treecore.cache.disc.impl.TTotalSizeLimitedDiscCache;
import com.treecore.cache.disc.impl.TUnlimitedDiscCache;
import com.treecore.cache.disc.naming.THashCodeFileNameGenerator;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import com.treecore.cache.memory.TIMemoryCacheAware;
import com.treecore.cache.memory.impl.TFIFOLimitedMemoryCache;
import com.treecore.cache.memory.impl.TLRULimitedMemoryCache;
import com.treecore.cache.memory.impl.TLargestLimitedMemoryCache;
import com.treecore.cache.memory.impl.TLruMemoryCache;
import com.treecore.cache.memory.impl.TUsingFreqLimitedMemoryCache;
import com.treecore.utils.TAndroidVersionUtils;
import java.io.File;

public class TCacheManager {
	public static TIFileNameGenerator createFileNameGenerator() {
		return new THashCodeFileNameGenerator();
	}

	public static TIDiscCacheAware createTotalSizeLimitedDiscCache(
			int discCacheSize, String cachePath) throws Exception {
		if (discCacheSize < 1)
			throw new Exception("size low¡­");
		if (TextUtils.isEmpty(cachePath))
			throw new Exception("null by cachePath¡­");
		File file = new File(cachePath);
		if (!file.exists())
			file.mkdirs();
		return new TTotalSizeLimitedDiscCache(file, createFileNameGenerator(),
				discCacheSize);
	}

	public static TIDiscCacheAware createFileCountLimitedDiscCache(
			int discCacheFileCount, String cachePath) throws Exception {
		if (discCacheFileCount < 1)
			throw new Exception("file count low¡­");
		if (TextUtils.isEmpty(cachePath))
			throw new Exception("null by cachePath¡­");
		File file = new File(cachePath);
		if (!file.exists())
			file.mkdirs();
		return new TFileCountLimitedDiscCache(file, createFileNameGenerator(),
				discCacheFileCount);
	}

	public static TIDiscCacheAware createUnlimitedDiscCache(String cachePath)
			throws Exception {
		if (TextUtils.isEmpty(cachePath))
			throw new Exception("null by cachePath¡­");
		File file = new File(cachePath);
		if (!file.exists())
			file.mkdirs();
		return new TUnlimitedDiscCache(file, createFileNameGenerator());
	}

	public static TIDiscCacheAware createReserveDiscCache(String cachePath,
			String cacheName) throws Exception {
		if (TextUtils.isEmpty(cachePath))
			throw new Exception("null by cachePath¡­");
		if (TextUtils.isEmpty(cacheName))
			throw new Exception("null by cacheName¡­");
		File file = new File(cachePath, cacheName);
		if (!file.exists()) {
			file.mkdir();
		}

		return new TTotalSizeLimitedDiscCache(file, 2097152);
	}

	public static TIMemoryCacheAware<String, Bitmap> createLruMemoryCache(
			int memoryCacheSize) {
		if (memoryCacheSize <= 0)
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8L);
		TIMemoryCacheAware memoryCache;
		TIMemoryCacheAware memoryCache;
		if (TAndroidVersionUtils.hasGingerbread())
			memoryCache = new TLruMemoryCache(memoryCacheSize);
		else {
			memoryCache = new TLRULimitedMemoryCache(memoryCacheSize);
		}
		return memoryCache;
	}

	public static TIMemoryCacheAware<String, Bitmap> createLruMemoryCache() {
		return createLruMemoryCache(0);
	}

	public static TIMemoryCacheAware<String, Bitmap> createFIFOLimitedMemoryCache(
			int memoryCacheSize) {
		if (memoryCacheSize <= 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8L);
		}
		return new TFIFOLimitedMemoryCache(memoryCacheSize);
	}

	public static TIMemoryCacheAware<String, Bitmap> createFIFOLimitedMemoryCache() {
		return createFIFOLimitedMemoryCache(0);
	}

	public static TIMemoryCacheAware<String, Bitmap> createLargestLimitedMemoryCache(
			int memoryCacheSize) {
		if (memoryCacheSize <= 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8L);
		}
		return new TLargestLimitedMemoryCache(memoryCacheSize);
	}

	public static TIMemoryCacheAware<String, Bitmap> createLargestLimitedMemoryCache() {
		return createLargestLimitedMemoryCache(0);
	}

	public static TIMemoryCacheAware<String, Bitmap> createUsingFreqLimitedMemoryCache(
			int memoryCacheSize) {
		if (memoryCacheSize <= 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8L);
		}
		return new TUsingFreqLimitedMemoryCache(memoryCacheSize);
	}

	public static TIMemoryCacheAware<String, Bitmap> createUsingFreqLimitedMemoryCache() {
		return createLargestLimitedMemoryCache(0);
	}
}