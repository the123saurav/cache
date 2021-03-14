package com.saurav.cache.optimised;

import com.saurav.cache.AbstractCache;
import com.saurav.cache.Loader;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * An OptimisedCache refreshes entries in background.
 *
 * @param <K>
 * @param <V>
 */
public class OptimisedCache<K, V> extends AbstractCache<K, V> {

  private final class Refresher implements Runnable {

    private final long runIntervalMillis;

    Refresher(final long r) {
      runIntervalMillis = r;
    }

    @Override
    public void run() {
      System.out.println("Refresher started with run interval ms: " + runIntervalMillis);
      while (!Thread.currentThread().isInterrupted()) {
        while (refreshChain.size() > 0) {
          Pair<K, Long> e = refreshChain.peekFirst();
          if (e == null || e.getRight() > Instant.now().toEpochMilli()) {
            break;
          }
          System.out.println("refreshing key: " + e.getLeft());
          try {
            fetch(e.getLeft());
          } catch (Exception ex) {
            // Swallow exceptions and move forward in the chain
            ex.printStackTrace();
          }
          refreshChain.pollFirst();
        }
        try {
          Thread.sleep(runIntervalMillis);
        } catch (final InterruptedException e) {
          return;
        }
      }
    }
  }

  private final long refreshIntervalMills;
  private final ConcurrentLinkedDeque<Pair<K, Long>> refreshChain;
  private final Thread refresher;
  private volatile boolean refresherStarted = false;

  public OptimisedCache(final long ttl, final Loader<K, V> l) {
    super(ttl, l, new ConcurrentHashMap<>());
    refreshIntervalMills = (ttl * 1000) / 3;
    refreshChain = new ConcurrentLinkedDeque<>();
    refresher = new Thread(new Refresher(Math.min(1000, refreshIntervalMills)));
  }

  @Override
  public V get(K key) throws Exception {
    ensureRefresh();
    final Value value = map.get(key);
    if (value != null && !value.isExpired()) {
      return value.getVal();
    }
    System.out.println("Forcing refresh");
    return fetch(key);
  }

  @Override
  protected V fetch(final K key) throws Exception {
    final V loaded = loader.load(key);
    final long now = Instant.now().toEpochMilli();
    map.put(key, new Value(loaded));
    refreshChain.add(new ImmutablePair<>(key, now + refreshIntervalMills));
    System.out.println("Key " + key + " created at " + Instant.ofEpochMilli(now) + " will be refreshed at " + Instant.ofEpochMilli(now + refreshIntervalMills));
    return loaded;
  }

  @Override
  public void cleanup() {
    refresher.interrupt();
  }

  private void ensureRefresh() {
    if (!refresherStarted) {
      synchronized (this) {
        if (!refresherStarted) {
          refresher.setDaemon(true);
          refresher.start();
          refresherStarted = true;
        }
      }
    }
  }
}
