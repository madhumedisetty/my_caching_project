package com.example.Usercase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Product implements java.io.Serializable {
    private String id;
    private String name;
    private double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters and setters omitted for brevity

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + "}";
    }
}

class DatabaseSimulator {
    private Map<String, Product> products = new HashMap<>();
    private Random random = new Random();

    public DatabaseSimulator(int numProducts) {
        for (int i = 0; i < numProducts; i++) {
            String id = "PROD" + i;
            products.put(id, new Product(id, "Product " + i, 10 + random.nextDouble() * 90));
        }
    }

    public Product getProduct(String id) {
        // Simulate database access delay
        try {
            Thread.sleep(100); // 100ms delay to simulate DB access
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return products.get(id);
    }
}
