package com.treecore.utils;

import android.graphics.Bitmap;
import com.treecore.cache.memory.TIMemoryCacheAware;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TMemoryCacheUtil {
	private static final String URI_AND_SIZE_SEPARATOR = "_";
	private static final String WIDTH_AND_HEIGHT_SEPARATOR = "x";

	public static String generateKey(String imageUri, int width, int height) {
		return imageUri + "_" + width + "x" + height;
	}

	public static Comparator<String> createFuzzyKeyComparator() {
		return new Comparator() {
			public int compare(String key1, String key2) {
				String imageUri1 = key1.substring(0, key1.lastIndexOf("_"));
				String imageUri2 = key2.substring(0, key2.lastIndexOf("_"));
				return imageUri1.compareTo(imageUri2);
			}
		};
	}

	public static List<Bitmap> findCachedBitmapsForImageUri(String imageUri,
			TIMemoryCacheAware<String, Bitmap> memoryCache) {
		List values = new ArrayList();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add((Bitmap) memoryCache.get(key));
			}
		}
		return values;
	}

	public static List<String> findCacheKeysForImageUri(String imageUri,
			TIMemoryCacheAware<String, Bitmap> memoryCache) {
		List values = new ArrayList();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				values.add(key);
			}
		}
		return values;
	}

	public static void removeFromCache(String imageUri,
			TIMemoryCacheAware<String, Bitmap> memoryCache) {
		List keysToRemove = new ArrayList();
		for (String key : memoryCache.keys()) {
			if (key.startsWith(imageUri)) {
				keysToRemove.add(key);
			}
		}
		for (String keyToRemove : keysToRemove)
			memoryCache.remove(keyToRemove);
	}
}