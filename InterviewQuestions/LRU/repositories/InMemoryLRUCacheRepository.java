package InterviewQuestions.LRU.repositories;

import InterviewQuestions.LRU.entities.CacheEntry;
import InterviewQuestions.LRU.entities.DoublyLinkedList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryLRUCacheRepository<K, V> implements CacheRepository<K, V> {

    private final int capacity;
    private final Map<K, CacheEntry<K, V>> map;
    private final DoublyLinkedList<K, V> dll;
    private final Lock lock = new ReentrantLock();

    public InMemoryLRUCacheRepository(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    @Override
    public V getVal(K key) {
        lock.lock();
        try {
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
        } catch(Exception ex) {
            System.out.println("Exception occurred while fetching query");
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public void removeKey(K key) {
        lock.lock();
        try {
            CacheEntry<K, V> node = map.remove(key);
            if (node != null){
                dll.removeNode(node);
            }
        } catch (Exception ex) {
            System.out.println("Exception occurred while removing key: "+ex.getMessage());
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void putKey(K key, V val){
        putKey(key, val, null);
    }

    @Override
    public void putKey(K key, V val, Instant expiresAt) {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }

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
