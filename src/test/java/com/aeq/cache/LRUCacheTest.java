package com.aeq.cache;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gopal on 04/08/17.
 */
public class LRUCacheTest {

    @Test
    public void testWithoutEvictionEvent() throws InterruptedException {

        LRUCache<String, String> lruCache = new LRUCache(1000*60l);
        lruCache.put("key", "value");
        Thread.sleep(1000);
        Assert.assertEquals("Cache size should be 1", 1, lruCache.size());
    }

    @Test
    public void testWithEvictionEvent() throws InterruptedException {

        LRUCache<String, String> lruCache = new LRUCache(800l);
        lruCache.put("key", "value");
        Thread.sleep(1500);
        Assert.assertEquals("Cache size should be 0", 0, lruCache.size());
    }


}
