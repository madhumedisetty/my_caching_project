package com.example.Usercase.Usercase2.Usecase3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachingDemo {
    // Simulated "database"
    private static final Map<Integer, String> database = new HashMap<>();
    
    // Simple cache
    private static final Map<Integer, String> simpleCache = new HashMap<>();
    
    // LRU cache
    private static final int LRU_CAPACITY = 100;
    private static final LinkedHashMap<Integer, String> lruCache = new LinkedHashMap<Integer, String>(LRU_CAPACITY, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
            return size() > LRU_CAPACITY;
        }
    };
    
    // Two-level cache
    private static final int L1_CAPACITY = 10;
    private static final int L2_CAPACITY = 100;
    private static final Map<Integer, String> l1Cache = new HashMap<>();
    private static final Map<Integer, String> l2Cache = new HashMap<>();
    
    // Thread-safe cache
    private static final Map<Integer, String> concurrentCache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Populate the "database"
        for (int i = 0; i < 1000; i++) {
            database.put(i, "Value" + i);
        }

        // Test different caching techniques
        testSimpleCache();
        testLRUCache();
        testTwoLevelCache();
        testConcurrentCache();
    }

    private static void testSimpleCache() {
        System.out.println("\nTesting Simple Cache:");
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int key = i % 200; // This will cause some cache hits
            getWithSimpleCache(key);
        }
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");
        System.out.println("Cache size: " + simpleCache.size());
    }

    private static void testLRUCache() {
        System.out.println("\nTesting LRU Cache:");
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int key = i % 200; // This will cause some cache hits and evictions
            getWithLRUCache(key);
        }
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");
        System.out.println("Cache size: " + lruCache.size());
    }

    private static void testTwoLevelCache() {
        System.out.println("\nTesting Two-Level Cache:");
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int key = i % 200; // This will cause hits in both levels and some misses
            getWithTwoLevelCache(key);
        }
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");
        System.out.println("L1 Cache size: " + l1Cache.size() + ", L2 Cache size: " + l2Cache.size());
    }

    private static void testConcurrentCache() {
        System.out.println("\nTesting Concurrent Cache:");
        long startTime = System.nanoTime();
        // Simulate concurrent access with multiple threads
        Thread[] threads = new Thread[10];
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    int key = i % 200;
                    getWithConcurrentCache(key);
                }
            });
            threads[t].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");
        System.out.println("Cache size: " + concurrentCache.size());
    }

    private static String getWithSimpleCache(int key) {
        if (!simpleCache.containsKey(key)) {
            simpleCache.put(key, database.get(key));
        }
        return simpleCache.get(key);
    }

    private static String getWithLRUCache(int key) {
        if (!lruCache.containsKey(key)) {
            lruCache.put(key, database.get(key));
        }
        return lruCache.get(key);
    }

    private static String getWithTwoLevelCache(int key) {
        // Check L1 Cache
        if (l1Cache.containsKey(key)) {
            return l1Cache.get(key);
        }
        // Check L2 Cache
        if (l2Cache.containsKey(key)) {
            String value = l2Cache.get(key);
            // Move to L1 cache
            if (l1Cache.size() >= L1_CAPACITY) {
                // If L1 is full, remove the first entry (simulating LRU)
                int oldestKey = l1Cache.keySet().iterator().next();
                l1Cache.remove(oldestKey);
            }
            l1Cache.put(key, value);
            return value;
        }
        // Not in cache, get from database
        String value = database.get(key);
        // Add to L2 cache
        if (l2Cache.size() >= L2_CAPACITY) {
            // If L2 is full, remove the first entry (simulating LRU)
            int oldestKey = l2Cache.keySet().iterator().next();
            l2Cache.remove(oldestKey);
        }
        l2Cache.put(key, value);
        return value;
    }

    private static String getWithConcurrentCache(int key) {
        return concurrentCache.computeIfAbsent(key, k -> database.get(k));
    }
}