package com.treecore.utils.stl;

import java.util.ArrayList;

public class TMapArrayList<T> extends ArrayList<THashMap<T>> {
	private static final long serialVersionUID = 1L;

	public boolean add(THashMap<T> taHashMap) {
		if (taHashMap != null) {
			return super.add(taHashMap);
		}
		return false;
	}
}