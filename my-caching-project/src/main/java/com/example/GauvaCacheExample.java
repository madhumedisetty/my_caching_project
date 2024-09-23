package com.example;
import com.google.common.cache.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GauvaCacheExample {
    public static void main(String[] args) {
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(200000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return "Value for " + key;
                    }
                });
        try{
              String[] genres= {"Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller"};
        //Write code to generate 1000 movies with random genres and push in popularMoviewCache
                for(int i=0; i<200000; i++){
                    String movie = "Movie" + i;
                    String genre = genres[new Random().nextInt(genres.length)];
                    cache.put(movie, genre);
                }
                //Thread.sleep(1000);
                for(int i=0;i<100;i++){
                long startTime = System.nanoTime();
                String movie = cache.get("Movie92600");
                long endTime = System.nanoTime();
                System.out.println("Time taken to fetch the movie: " + (endTime - startTime) + " nanoseconds");
                }
                
                // It would be retrieved from cache
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}