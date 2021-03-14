package com.saurav.cache;

/**
 * A cache is an in-memory store for key-values
 *
 * @param <K> Key
 * @param <V> Value
 */
public interface Cache<K, V> {

  int MIN_EXPIRY_SECONDS = 60 * 1000;

  /**
   * Get value in cache
   *
   * @param key
   * @return
   * @throws Exception
   */
  V get(K key) throws Exception;

  /**
   * Cleanup if required
   */
  default void cleanup() {
  }
}
