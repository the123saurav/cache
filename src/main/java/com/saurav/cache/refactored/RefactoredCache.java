package com.saurav.cache.refactored;

import com.saurav.cache.AbstractCache;
import com.saurav.cache.Loader;
import java.util.concurrent.ConcurrentHashMap;

public class RefactoredCache<K, V> extends AbstractCache<K, V> {

  public RefactoredCache(final long ttl, final Loader<K, V> l) {
    super(ttl, l, new ConcurrentHashMap<>());
  }

  @Override
  public V get(final K key) throws Exception {
    final Value value = map.get(key);
    if (value != null && !value.isExpired()) {
      return value.getVal();
    }
    return fetch(key);
  }

  @Override
  protected V fetch(K key) throws Exception {
    final V loaded = loader.load(key);
    map.put(key, new Value(loaded));
    return loaded;
  }
}
