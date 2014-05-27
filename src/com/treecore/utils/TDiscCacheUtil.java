package com.treecore.utils;

import com.treecore.cache.disc.TIDiscCacheAware;
import java.io.File;

public final class TDiscCacheUtil {
	public static File findInCache(String url, TIDiscCacheAware discCache) {
		File file = discCache.get(url);
		return file.exists() ? file : null;
	}

	public static boolean removeFromCache(String url, TIDiscCacheAware discCache) {
		File file = discCache.get(url);
		return file.delete();
	}
}