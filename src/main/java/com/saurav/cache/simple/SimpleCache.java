package com.saurav.cache.simple;

import com.saurav.cache.Cache;
import com.saurav.cache.Loader;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleCache<K, V> implements Cache<K, V> {

  private final Map<K, Pair<V, Long>> map;
  private final long ttlSeconds;
  private final Loader<K, V> loader;

  public SimpleCache(final long ttl, final Loader<K, V> l) {
    ttlSeconds = ttl;
    map = new ConcurrentHashMap<>();
    loader = l;
  }

  public V get(final K key) throws Exception {
    final Pair<V, Long> value = map.get(key);
    if (value != null && !notExpired(value.getRight())) {
      return value.getLeft();
    }
    final V loaded = loader.load(key);
    map.put(key, new ImmutablePair<>(loaded, ttlSeconds + Instant.now().getEpochSecond()));
    return loaded;
  }

  private boolean notExpired(final long expireAt) {
    return expireAt <= Instant.now().getEpochSecond();
  }
}