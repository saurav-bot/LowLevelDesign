package InterviewQuestions.LRU.repositories;

import InterviewQuestions.LRU.entities.CacheEntry;
import InterviewQuestions.LRU.entities.DoublyLinkedList;

import java.util.HashMap;
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
    public void putKey(K key, V val) {
        CacheEntry<K, V> existing = map.get(key);
        if (existing != null){
            existing.setVal(val);
            dll.moveToLast(existing);
            return;

        }
        if (map.size() >= this.capacity) {
            CacheEntry<K,V> node = dll.removeFirst();
            if (node != null) {
                map.remove(node.getKey());
            }
        }
        CacheEntry<K, V> newNode = new CacheEntry<>(key, val);
        map.put(key, newNode);
        dll.addLast(newNode);
    }

}
