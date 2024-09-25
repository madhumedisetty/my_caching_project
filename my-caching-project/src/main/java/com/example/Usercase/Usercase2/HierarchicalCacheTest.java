package com.example.Usercase.Usercase2;

import com.example.Usercase.Product;

import java.io.IOException;

public class HierarchicalCacheTest {
    private static final int TOTAL_PRODUCTS = 100_000;
    private static final int TEST_ITERATIONS = 1_000_000;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        HierarchicalCache cache = new HierarchicalCache(100, 1000, "C:\\test\\my_caching_project\\my-caching-project\\src\\main\\java\\com\\example\\Usercase\\Usercase2\\l3cache");

        // Populate cache
        System.out.println("Populating cache...");
        for (int i = 0; i < TOTAL_PRODUCTS; i++) {
            String id = "PROD" + i;
            cache.put(id, DataGenerator.generateProduct(id));
        }

        // Test random access
        System.out.println("Testing random access...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String randomId = DataGenerator.generateRandomId(TOTAL_PRODUCTS);
            Product product = cache.get(randomId);
            if (product == null) {
                System.out.println("Product not found: " + randomId);
            }

            if (i % 100000 == 0) {
                System.out.println("Completed " + i + " iterations");
                cache.printStats();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total time for " + TEST_ITERATIONS + " random accesses: " + (endTime - startTime) + "ms");
        cache.printStats();
    }
}