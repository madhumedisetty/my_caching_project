package com.example;
import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K,V> {
    private final Map<K,V> cache;
    public SimpleCache(int capacity){
        this.cache=new HashMap<>(capacity);
    }
    public void put(K key, V value){
        cache.put(key,value);
    }
    public V get(K key){
        return cache.get(key);
    }
    public V remove(K key){
        return cache.remove(key);
    }
    public void clear(){    
        cache.clear();
    }
    public int size(){
        return cache.size();
    }

    public static void main(String[] args) {
        SimpleCache<String, String> cache = new SimpleCache<>(3);
        cache.put("1", "one");
        cache.put("2", "two");
        System.out.println(cache.get("1")); //one
        cache.put("3", "three");
        System.out.println(cache.size()); //3
        cache.remove("2");
        System.out.println(cache.get("2")); //null
        System.out.println(cache.size()); //2
        cache.clear();
        System.out.println(cache.size()); //0
    }
}
