package com.saurav;

import com.saurav.cache.Cache;
import com.saurav.cache.DumbLoader;
import com.saurav.cache.optimised.OptimisedCache;

public class App {

  public static void main(String[] args) throws Exception {
    final Cache cache = new OptimisedCache(2, new DumbLoader<String, String>());
    final String[] keys = new String[] {"alpha", "beta", "gamma", "delta"};
    for (int i = 0; i < keys.length; i++) {
      assert cache.get(keys[i]).equals(keys[i]);
    }


    Thread.sleep(10000);
    for (int i = 0; i < keys.length; i++) {
      assert cache.get(keys[i]).equals(keys[i]);
    }
  }
}
