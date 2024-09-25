package com.example.Usercase;

import java.util.Random;

public class CachingPerformanceTest {
    public static void main(String[] args) throws Exception {
        testPerformance(10_000, 1_000); // 10,000 products, 1,000 cache size
        testPerformance(1_000_000, 100_000); // 1 million products, 100,000 cache size
    }

    private static void testPerformance(int numProducts, int cacheSize) throws Exception {
        ProductService service = new ProductService(numProducts, cacheSize);
        Random random = new Random();

        System.out.println("\nTesting with " + numProducts + " products and cache size " + cacheSize);

        // Warm up the cache
        for (int i = 0; i < 1000; i++) {
            service.getProduct("PROD" + random.nextInt(numProducts));
        }

        // Test performance
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            service.getProduct("PROD" + random.nextInt(numProducts));
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Time taken for 10,000 random product retrievals: " + (endTime - startTime) + "ms");
        service.printCacheStats();
    }
}