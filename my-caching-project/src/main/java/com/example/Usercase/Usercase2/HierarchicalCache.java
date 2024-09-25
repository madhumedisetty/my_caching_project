package com.example.Usercase.Usercase2;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import com.example.Usercase.Product;

public class HierarchicalCache {
    private final Cache<String, Product> l1Cache;
    private final Cache<String, Product> l2Cache;
    private final Path l3CacheDir;

    public HierarchicalCache(int l1Size, int l2Size, String l3Path) throws IOException {
        this.l1Cache = CacheBuilder.newBuilder()
                .maximumSize(l1Size)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.l2Cache = CacheBuilder.newBuilder()
                .maximumSize(l2Size)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.l3CacheDir = Paths.get(l3Path);
        Files.createDirectories(l3CacheDir);
    }

    public Product get(String key) throws IOException, ClassNotFoundException {
        // Try L1 Cache
        Product product = l1Cache.getIfPresent(key);
        if (product != null) {
            return product;
        }

        // Try L2 Cache
        product = l2Cache.getIfPresent(key);
        if (product != null) {
            l1Cache.put(key, product);
            return product;
        }

        // Try L3 Cache
        Path filePath = l3CacheDir.resolve(key);
        if (Files.exists(filePath)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
                product = (Product) ois.readObject();
                l2Cache.put(key, product);
                l1Cache.put(key, product);
                return product;
            }
        }

        return null;
    }

    public void put(String key, Product value) throws IOException {
        l1Cache.put(key, value);
        l2Cache.put(key, value);

        // Write to L3 Cache
        Path filePath = l3CacheDir.resolve(key);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(value);
        }
    }

    public void printStats() {
        System.out.println("L1 Cache Stats: " + l1Cache.stats());
        System.out.println("L2 Cache Stats: " + l2Cache.stats());
    }
}