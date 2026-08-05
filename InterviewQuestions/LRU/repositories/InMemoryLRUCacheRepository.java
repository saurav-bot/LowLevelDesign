package InterviewQuestions.LRU.repositories;

import InterviewQuestions.LRU.entities.CacheEntry;
import InterviewQuestions.LRU.entities.DoublyLinkedList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryLRUCacheRepository<K, V> implements CacheRepository<K, V> {

    private final int capacity;
    private final Map<K, CacheEntry<K, V>> map;
    private final DoublyLinkedList<K, V> dll;

    public InMemoryLRUCacheRepository(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    @Override
    public V getVal(K key) {
        CacheEntry<K, V> node = map.get(key);
        if (node == null){
            return null;
        }

        if (node.isExpired()) {
            removeKey(node.getKey());
            return null;
        }

        dll.moveToLast(node);
        return node.getVal();
    }

    @Override
    public void removeKey(K key) {
        CacheEntry<K, V> node = map.remove(key);
        if (node != null){
            dll.removeNode(node);
        }
    }

    @Override
    public void putKey(K key, V val){
        putKey(key, val, null);
    }

    @Override
    public void putKey(K key, V val, Instant expiresAt) {
        CacheEntry<K, V> existing = map.get(key);
        if (existing != null){
            existing.setVal(val);
            existing.setExpiresAt(expiresAt);
            dll.moveToLast(existing);
            return;

        }
        if (map.size() >= this.capacity) {
            CacheEntry<K,V> node = dll.removeFirst();
            if (node != null) {
                map.remove(node.getKey());
            }
        }
        CacheEntry<K, V> newNode = new CacheEntry<>(key, val, expiresAt);
        map.put(key, newNode);
        dll.addLast(newNode);
    }

    public void cleanupExpiredEntries() {
        List<K> expiredKeys = new ArrayList<>();

        for (CacheEntry<K, V> entries : map.values()) {
            if (entries.isExpired()){
                expiredKeys.add(entries.getKey());
            }
        }

        for(K key : expiredKeys){
            System.out.println("Key " + key + " is expired and removed");
            removeKey(key);
        }
    }


}
