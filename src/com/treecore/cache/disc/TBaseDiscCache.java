package com.treecore.cache.disc;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import java.io.File;

public abstract class TBaseDiscCache implements TIDiscCacheAware {
	private static final String ERROR_ARG_NULL = "\"%s\" argument must be not null";
	protected File cacheDir;
	private TIFileNameGenerator fileNameGenerator;

	public TBaseDiscCache(File cacheDir) {
		this(cacheDir, TCacheManager.createFileNameGenerator());
	}

	public TBaseDiscCache(File cacheDir, TIFileNameGenerator fileNameGenerator) {
		if (cacheDir == null) {
			throw new IllegalArgumentException(
					"cacheDir\"%s\" argument must be not null");
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException(
					"fileNameGenerator\"%s\" argument must be not null");
		}

		this.cacheDir = cacheDir;
		this.fileNameGenerator = fileNameGenerator;
	}

	public File get(String key) {
		String fileName = this.fileNameGenerator.generate(key);
		return new File(this.cacheDir, fileName);
	}

	public void clear() {
		File[] files = this.cacheDir.listFiles();
		if (files != null)
			for (File f : files)
				f.delete();
	}
}