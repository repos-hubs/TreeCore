package com.treecore.cache.disc;

import java.io.File;

public abstract interface TIDiscCacheAware {
	public abstract void put(String paramString, File paramFile);

	public abstract File get(String paramString);

	public abstract void clear();
}