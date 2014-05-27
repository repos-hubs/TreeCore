package com.treecore.cache.memory;

import java.util.Collection;

public abstract interface TIMemoryCacheAware<K, V> {
  public abstract boolean put(K paramK, V paramV);

  public abstract V get(K paramK);

  public abstract void remove(K paramK);

  public abstract Collection<K> keys();

  public abstract void clear();
}