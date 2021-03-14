package com.saurav.cache;

public final class DumbLoader<K, V> implements Loader<K, V> {
  @Override
  public V load(K key) throws Exception {
    return (V) key;
  }
}
