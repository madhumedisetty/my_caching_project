package com.example.Usercase.Usercase2.Usecase3;
import com.example.Usercase.Product;
import java.util.HashMap;
import java.util.Map;

public class TwoLevelCache {
    private final Map<String, Product> l1Cache;
    private final Map<String, Product> l2Cache;
    private final int l1Capacity;
    private int l1Hits = 0, l2Hits = 0, misses = 0;

    public TwoLevelCache(int l1Capacity, int l2Capacity) {
        this.l1Cache = new HashMap<>(l1Capacity);
        this.l2Cache = new HashMap<>(l2Capacity);
        this.l1Capacity = l1Capacity;
    }

    public Product get(String key) {
        // Check L1 Cache
        if (l1Cache.containsKey(key)) {
            l1Hits++;
            return l1Cache.get(key);
        }

        // Check L2 Cache
        if (l2Cache.containsKey(key)) {
            l2Hits++;
            Product product = l2Cache.get(key);
            // Move to L1 cache
            if (l1Cache.size() >= l1Capacity) {
                // If L1 is full, remove the first entry (simulating LRU)
                String oldestKey = l1Cache.keySet().iterator().next();
                l1Cache.remove(oldestKey);
            }
            l1Cache.put(key, product);
            return product;
        }

        misses++;
        return null; // Not found in either cache
    }

    public void put(String key, Product value) {
        if (l1Cache.size() >= l1Capacity) {
            // If L1 is full, move the first entry to L2 (simulating LRU)
            String oldestKey = l1Cache.keySet().iterator().next();
            l2Cache.put(oldestKey, l1Cache.remove(oldestKey));
        }
        l1Cache.put(key, value);
    }

    public void printStats() {
        int totalRequests = l1Hits + l2Hits + misses;
        System.out.println("Cache Stats:");
        System.out.println("L1 Hits: " + l1Hits + " (" + (l1Hits * 100.0 / totalRequests) + "%)");
        System.out.println("L2 Hits: " + l2Hits + " (" + (l2Hits * 100.0 / totalRequests) + "%)");
        System.out.println("Misses: " + misses + " (" + (misses * 100.0 / totalRequests) + "%)");
    }
}