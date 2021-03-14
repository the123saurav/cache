package com.saurav.cache;

public interface Loader<K, V> {
  V load(K key) throws Exception;
}
