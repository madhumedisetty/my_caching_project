package com.example.BookSystem;

import java.util.*; // Importing necessary classes from the java.util package

public class BookLibrayCache {
    // A map to simulate a database of books, using book ID as the key
    private final Map<String, Book> bookDatabase = new HashMap<>();
    
    // Our cache to store recently accessed books
    private final Map<String, Book> cache = new HashMap<>();

    // Maximum size of the cache
    private final int CACHE_SIZE = 5;

    // Counters for cache hits and misses
    private int cacheHits = 0;
    private int cacheMisses = 0;

    // Static inner class representing a Book
    private static class Book {
        private String id; // Book ID
        private String title; // Book title
        private String author; // Book author

        // Constructor to initialize a Book object
        public Book(String id, String title, String author) {
            this.id = id; // Set the book ID
            this.title = title; // Set the book title
            this.author = author; // Set the book author
        }

        // Override toString method to provide a string representation of the book
        @Override
        public String toString() {
            return "Book{id='" + id + "', title='" + title + "', author='" + author + "'}"; // Format book details
        }
    }

    // Constructor for BookLibrayCache
    public BookLibrayCache() {
        // Initialize the book database with some sample books
        bookDatabase.put("1", new Book("1", "Book1", "Author1"));
        bookDatabase.put("2", new Book("2", "Book2", "Author2"));
        bookDatabase.put("3", new Book("3", "Book3", "Author3"));
        bookDatabase.put("4", new Book("4", "Book4", "Author4"));
        bookDatabase.put("5", new Book("5", "Book5", "Author5"));
        bookDatabase.put("6", new Book("6", "Book6", "Author6"));
    }

    // Method to retrieve a book by its ID
    public Book getBook(String bookId) {
        // Check if the book is in the cache
        Book book = cache.get(bookId);
        if (book != null) {
            // If found in cache, increment cache hits and return the book
            cacheHits++;
            System.out.println("Cache Hit for bookId: " + bookId);
            return book;
        } else {
            // If not found in cache, increment cache misses
            cacheMisses++;
            System.out.println("Cache Miss for bookId: " + bookId);
            // Retrieve the book from the database
            book = bookDatabase.get(bookId);
            if (book != null) {
                // If found in database, add it to the cache
                addToCache(bookId, book);
            }
            return book; // Return the book (or null if not found)
        }
    }

    // Method to add a book to the cache
    private void addToCache(String bookId, Book book) {
        // Check if the cache has reached its maximum size
        if (cache.size() >= CACHE_SIZE) {
            // Remove the least recently used book from the cache
            String keyToRemove = cache.keySet().iterator().next(); // Get an arbitrary key (oldest in this case)
            cache.remove(keyToRemove); // Remove the book from the cache
            System.out.println("Cache is full. Removing least recently used bookId: " + keyToRemove);
        }
        // Add the new book to the cache
        cache.put(bookId, book);
        System.out.println("Added to cache: " + bookId); // Log the addition
    }

    // Method to print cache statistics
    public void printCacheStatistics() {
        // Print the number of cache hits and misses
        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        // Calculate and print the cache hit ratio
        System.out.println("Cache Hit Ratio: " + ((double) cacheHits / (cacheHits + cacheMisses)));
        // Print the current size of the cache
        System.out.println("Current Cache Size: " + cache.size());
        // Print the keys (book IDs) currently in the cache
        System.out.println("Books in Cache: " + cache.keySet());
    }

    // Main method to run the cache simulation
    public static void main(String[] args) {
        BookLibrayCache cache = new BookLibrayCache(); // Create an instance of BookLibrayCache
        // Array of requested book IDs to simulate access
        String[] requestedBooks = {"1", "2", "3", "4", "5", "6", "1", "2", "3", "4", "5", "6", "5", "4", "3", "9"};
        
        // Loop through each requested book ID
        for (String bookId : requestedBooks) {
            // Try to retrieve the book from the cache or database
            Book book = cache.getBook(bookId);
            if (book != null) {
                // If the book is found, print its details
                System.out.println("Retrieved book: " + book);
            } else {
                // If the book is not found, print a message
                System.out.println("Book not found: " + book);
            }
            System.out.println("-----------------------------------"); // Separator for readability
        }
        
        // Print cache statistics after all requests
        cache.printCacheStatistics();
    }
}
