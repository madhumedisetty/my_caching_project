package com.example.BookSystem;

import java.util.concurrent.ConcurrentHashMap; // Importing ConcurrentHashMap for thread-safe caching
import java.util.concurrent.atomic.AtomicInteger; // Importing AtomicInteger for atomic counters
import java.io.IOException; // Importing IOException for handling IO exceptions
import java.nio.file.Files; // Importing Files for file operations
import java.nio.file.Path; // Importing Path for file path operations
import java.nio.file.Paths; // Importing Paths for creating paths
import java.io.Serializable; // Importing Serializable for serializing documents
import java.io.ObjectInputStream; // Importing ObjectInputStream for reading serialized objects
import java.io.ObjectOutputStream; // Importing ObjectOutputStream for writing serialized objects
import java.util.concurrent.ExecutorService; // Importing ExecutorService for managing threads
import java.util.concurrent.Executors; // Importing Executors for creating thread pools
import java.util.concurrent.TimeUnit; // Importing TimeUnit for time operations

// This class manages a cache of documents with disk storage capabilities
public class DetailDocumentCache {
    // ConcurrentHashMap is used instead of HashMap because it is thread-safe.
    // It allows multiple threads to access and modify the cache concurrently without issues.
    // Unlike Collections.synchronizedMap, it does not lock the entire map during write operations,
    // providing better performance and scalability in multi-threaded environments.
    private final ConcurrentHashMap<String, Document> cache;

    private final String diskStoragePath; // Path for disk storage of documents
    private final int maxCacheSize; // Maximum size of the cache

    // AtomicInteger is used instead of int because it provides atomic operations on the integer value.
    // AtomicInteger ensures that the integer value is updated atomically, which means the value is updated in a single step without interruptions.
    // This is useful in concurrent environments where multiple threads may access and update the integer value simultaneously.
    private final AtomicInteger cacheHits = new AtomicInteger(0); // Counter for cache hits
    private final AtomicInteger cacheMisses = new AtomicInteger(0); // Counter for cache misses

    // Constructor to initialize the cache and disk storage path
    public DetailDocumentCache(int maxCacheSize, String diskStoragePath) {
        this.maxCacheSize = maxCacheSize; // Set max cache size
        this.diskStoragePath = diskStoragePath; // Set disk storage path
        this.cache = new ConcurrentHashMap<>(maxCacheSize); // Initialize cache

        // Create directories for disk storage if they do not exist
        try {
            Files.createDirectories(Paths.get(diskStoragePath)); // Create the directory path
        } catch (IOException e) {
            e.printStackTrace(); // Handle errors in directory creation
        }
    }

    // Method to retrieve a document by its ID or save it if it doesn't exist
    public Document getDocumentIfExistElseSave(String documentId, DetailDocumentCache cache) throws IOException, ClassNotFoundException {
        Document cachedDocument = this.get(documentId); // Use 'this' instead of 'cache'
        if (cachedDocument != null) {
            cacheHits.incrementAndGet(); // Increment cache hits if found
            return cachedDocument; // Return cached document
        } else {
            cacheMisses.incrementAndGet(); // Increment cache misses if not found
            
            Document document = loadDocumentFromDisk(documentId); // Load document from disk
            if (document == null) { // If not found on disk
                Document newDocument = new Document(documentId, "Content for " + documentId, System.currentTimeMillis(), System.currentTimeMillis());
                cache.saveDocument(newDocument); // Save new document to cache
                System.out.println("Saved document: " + documentId); // Log saving action
                return newDocument; // Return the newly created document
            } else {
                return document; // Return the loaded document from disk
            }
        }
    }

    // Method to save a document to the cache and disk
    public void saveDocument(Document document) throws IOException {
        evictCacheIfNecessary(); // Evict if cache exceeds maximum size
        saveToDisk(document); // Save document to disk
        addToCache(document.getDocumentId(), document); // Add document to cache
    }

    // Method to save a document to disk
    private void saveToDisk(Document document) throws IOException {
        Path filePath = Paths.get(diskStoragePath, document.getDocumentId()); // Create file path
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(document); // Serialize and save the document
        }
    }

    // Method to print cache statistics
    public void printCacheStatistics() {
        System.out.println("Cache Hits: " + cacheHits.get()); // Print number of cache hits
        System.out.println("Cache Misses: " + cacheMisses.get()); // Print number of cache misses
        System.out.println("Cache Size: " + cache.size()); // Print current cache size
        System.out.println("Cache Capacity: " + maxCacheSize); // Print maximum cache capacity
        System.out.println("Cache Efficiency: " + ((double) cacheHits.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%"); // Calculate and print cache efficiency
        System.out.println("Cache Hit Ratio: " + ((double) cacheHits.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%"); // Print cache hit ratio
        System.out.println("Cache Miss Ratio: " + ((double) cacheMisses.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%"); // Print cache miss ratio
    }

    // Method to add a document to the cache
    private void addToCache(String documentId, Document document) {
        if (cache.size() > maxCacheSize) {
            evictCacheIfNecessary(); // Evict if necessary
        }
        cache.put(documentId, document); // Add document to cache
    }

    // Method to load a document from disk
    private Document loadDocumentFromDisk(String documentId) throws IOException, ClassNotFoundException {
        Path filePath = Paths.get(diskStoragePath, documentId); // Create file path
        if (Files.exists(filePath)) { // Check if the file exists
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
                return (Document) in.readObject(); // Deserialize and return the document
            }
        }
        return null; // Return null if not found
    }

    // Method to evict documents from the cache if it exceeds maximum size
    private void evictCacheIfNecessary() {
        while (cache.size() > maxCacheSize-1) { // While cache size exceeds max size
            String oldestDocument = cache.keySet().iterator().next(); // Get an arbitrary key (oldest in this case)
            cache.remove(oldestDocument); // Remove the document from cache
        }
    }

    // Static class that implements Serializable for document representation
    public static class Document implements Serializable {
        private final String documentId; // Document ID
        private final String content; // Document content
        private final long timestamp; // Timestamp of creation
        private final long lastModifiedTime; // Last modified time

        // Constructor to initialize a Document
        public Document(String documentId, String content, long timestamp, long lastModifiedTime) {
            this.documentId = documentId; // Set document ID
            this.content = content; // Set content
            this.timestamp = timestamp; // Set creation timestamp
            this.lastModifiedTime = lastModifiedTime; // Set last modified time
        }

        // Getter for document ID
        public String getDocumentId() {
            return documentId;
        }

        // Getter for content
        public String getContent() {
            return content;
        }

        // Getter for timestamp
        public long getTimestamp() {
            return timestamp;
        }

        // Getter for last modified time
        public long getLastModifiedTime() {
            return lastModifiedTime;
        }

        // Override toString for document representation
        @Override
        public String toString() {
            return "Document{" + "documentId='" + documentId + '\'' + ", content='" + content + '\'' + ", timestamp=" + timestamp + ", lastModifiedTime=" + lastModifiedTime + '}';
        }
    }

    // Main method to simulate document caching
    public static void main(String[] args) {
        DetailDocumentCache cache = new DetailDocumentCache(200, "C:\\Codebase\\my-caching-project\\my-caching-project\\src\\main\\java\\com\\cache\\boooksystem\\diskStorage2");
        
        // ExecutorService is better than creating threads by extending the Thread class because it provides a more efficient and flexible way to manage threads.
        // ExecutorService provides a pool of threads that can be reused to execute multiple tasks,
        // allowing tasks to be executed concurrently and providing a way to control the number of threads in the pool.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Start the simulation
        System.err.println("Starting the simulation");

        // Simulate multiple threads accessing and modifying the document
        for (int i = 0; i < 20; i++) {
            final int index = i; // Final variable for use in the lambda function
            executor.execute(() -> {
                try {
                    // Simulate a document with a unique ID based on the index
                    String id = "Document" + index; // Unique document ID
                    if (index % 4 == 0) { // Condition to create or load a document
                        if (cache.loadDocumentFromDisk(id) != null) { // Check if document exists on disk
                            System.out.println("Document already exists: " + id); // Log existence
                            System.out.println("Document: " + cache.loadDocumentFromDisk(id)); // Print document details
                        } else { // If document does not exist
                            Document newDocument = new Document(id, "Content for " + id, System.currentTimeMillis(), System.currentTimeMillis());
                            cache.saveDocument(newDocument); // Save new document to cache
                            System.out.println("Saved document: " + id); // Log saving action
                        }
                    } else { // For other indices
                        Document document = cache.getDocumentIfExistElseSave(id, cache); // Retrieve or save document
                        if (document != null) {
                            System.out.println("Retrieved document: " + id); // Log retrieval
                        } else {
                            System.out.println("Document not found: " + id); // Log missing document
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace(); // Handle exceptions
                } finally {
                    System.out.println("Thread " + index + " completed"); // Log thread completion
                }
            });
        }

        // Shutdown the executor after all tasks are submitted
        executor.shutdown();
        try {
            // Wait until all threads are finished
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow(); // Force shutdown if tasks take too long
            }
        } catch (InterruptedException e) {
            e.printStackTrace(); // Handle interruption
        }
    }

    // Method to retrieve a document by its ID
    public Document get(String documentId) {
        // Implement cache retrieval logic here
        return null; // Placeholder return, replace with actual implementation
    }
}
