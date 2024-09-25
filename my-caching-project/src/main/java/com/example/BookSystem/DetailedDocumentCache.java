package com.example.BookSystem;

import java.util.concurrent.ConcurrentHashMap; // For thread-safe caching
import java.util.concurrent.atomic.AtomicInteger; // For atomic counters
import java.io.IOException; // For handling IO exceptions
import java.nio.file.Files; // For file operations
import java.nio.file.Paths; // For path operations
import java.io.Serializable; // For serializing documents
import java.io.ObjectInputStream; // For reading serialized objects
import java.io.ObjectOutputStream; // For writing serialized objects
import java.nio.file.Path; // For file paths
import java.util.concurrent.ExecutorService; // For managing threads
import java.util.concurrent.Executors; // For creating thread pools
import java.util.concurrent.TimeUnit; // For time operations

public class DetailedDocumentCache {

    // Cache to store documents with thread-safe access
    private final ConcurrentHashMap<String, Document> cache;

    // Path for disk storage of documents
    private final String diskStoragePath;
    // Maximum size of the cache
    private final int maxCacheSize;

    // Counters for cache hits and misses
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    // Constructor to initialize the cache and disk storage path
    public DetailedDocumentCache(int maxCacheSize, String diskStoragePath) {
        this.maxCacheSize = maxCacheSize; // Set max cache size
        this.diskStoragePath = diskStoragePath; // Set disk storage path
        this.cache = new ConcurrentHashMap<>(maxCacheSize); // Initialize cache

        // Create directories for disk storage if they do not exist
        try {
            Files.createDirectories(Paths.get(diskStoragePath));
        } catch (IOException e) {
            e.printStackTrace(); // Handle errors in directory creation
        }
    }

    // Method to retrieve a document by its ID
    public Document getDocument(String documentId) throws IOException, ClassNotFoundException {
        Document cachedDocument = cache.get(documentId); // Check cache for document
        if (cachedDocument != null) {
            cacheHits.incrementAndGet(); // Increment cache hits
            return cachedDocument; // Return cached document
        } else {
            cacheMisses.incrementAndGet(); // Increment cache misses
            Document document = loadDocumentFromDisk(documentId); // Load from disk if not in cache
            if (document != null) {
                addToCache(documentId, document); // Add loaded document to cache
            }
            return document; // Return the document (or null if not found)
        }
    }

    // Method to save a document to the cache and disk
    public void saveDocument(Document document) throws IOException {
        cache.put(document.getDocumentId(), document); // Add to cache
        evictCacheIfNecessary(); // Evict if cache exceeds maximum size
        saveToDisk(document); // Save document to disk
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
        System.out.println("Cache Hits: " + cacheHits.get());
        System.out.println("Cache Misses: " + cacheMisses.get());
        System.out.println("Cache Size: " + cache.size());
        System.out.println("Cache Capacity: " + maxCacheSize);
        System.out.println("Cache Efficiency: " + ((double) cacheHits.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%");
        System.out.println("Cache Hit Ratio: " + ((double) cacheHits.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%");
        System.out.println("Cache Miss Ratio: " + ((double) cacheMisses.get() / (cacheHits.get() + cacheMisses.get()) * 100) + "%");
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
        while (cache.size() > maxCacheSize) {
            String oldestDocument = cache.keySet().iterator().next(); // Get an arbitrary key (oldest in this case)
            cache.remove(oldestDocument); // Remove the document from cache
        }
    }

    // Document class that implements Serializable
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
        DetailedDocumentCache cache = new DetailedDocumentCache(20, "C:\\test\\my_caching_project\\my-caching-project\\src\\main\\java\\com\\example\\BookSystem\\diskStorage");
        
        // Create an ExecutorService for saving documents
        ExecutorService saveExecutor = Executors.newFixedThreadPool(2);
        System.err.println("Saving documents to cache...");
        
        // Prepopulate the cache with documents
        for (int i = 0; i < 50; i++) {
            final int index = i;
            saveExecutor.execute(() -> {
                try {
                    String id = "Document" + index;
                    Document newDocument = new Document(id, "Content for " + id, System.currentTimeMillis(), System.currentTimeMillis());
                    cache.saveDocument(newDocument);
                    System.out.println("Saved document: " + id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown the save executor after all tasks are submitted
        saveExecutor.shutdown();
        try {
            if (!saveExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                saveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            saveExecutor.shutdownNow();
        }

        // Now create an ExecutorService for retrieving documents
        ExecutorService retrieveExecutor = Executors.newFixedThreadPool(2);
        System.err.println("Retrieving documents from cache...");
        
        for (int i = 0; i < 50; i++) {
            final int index = i;
            retrieveExecutor.execute(() -> {
                try {
                    String id = "Document" + index;
                    if(index%2==0){
                        Document document = cache.getDocument(id);
                        System.out.println("Retrieved document: " + id);
                    }
                     else {
                        System.out.println("Document not found: " + id);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown the retrieve executor after all tasks are submitted
        retrieveExecutor.shutdown();
        try {
            if (!retrieveExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                retrieveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            retrieveExecutor.shutdownNow();
        }

        // Print final cache statistics after all tasks have been completed
        cache.printCacheStatistics();
        System.err.println("----------------------------------");
    }
}
