package com.treecore.cache.disc.naming;

public class THashCodeFileNameGenerator implements TIFileNameGenerator {
	public String generate(String imageUri) {
		return String.valueOf(imageUri.hashCode());
	}
}