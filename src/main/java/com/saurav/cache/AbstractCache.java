package com.saurav.cache;

import java.time.Instant;
import java.util.Map;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

  public final class Value {
    private final V val;
    private final long expireAt;

    public Value(final V v) {
      val = v;
      expireAt = Instant.now().getEpochSecond() + ttlSeconds;
    }

    public boolean isExpired() {
      return expireAt <= Instant.now().getEpochSecond();
    }

    public V getVal() {
      return val;
    }
  }

  protected final Map<K, Value> map;
  protected final long ttlSeconds;
  protected final Loader<K, V> loader;

  public AbstractCache(final long ttl, final Loader<K, V> l, final Map<K, Value> m) {
    ttlSeconds = ttl;
    loader = l;
    map = m;
  }

  public abstract V get(final K key) throws Exception;

  protected abstract V fetch(final K key) throws Exception;
}