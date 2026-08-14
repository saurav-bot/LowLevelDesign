package InterviewQuestions.LFU.repository;

import InterviewQuestions.LFU.model.CacheEntry;
import InterviewQuestions.LFU.model.DoublyLinkedList;
import InterviewQuestions.LFU.model.FrequencyNode;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryLfuRepository<K, V> implements CacheRepository<K, V>{
    ConcurrentHashMap<K, CacheEntry<K, V>> cache = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    ConcurrentHashMap<Long, FrequencyNode<K, V>> freqMap = new ConcurrentHashMap<>();

    private final int capacity;
    private Long starting = 1L;

    public InMemoryLfuRepository(int capacity){
        this.capacity = capacity;
    }
    public V get(K key) {
        lock.lock();
        try {
            CacheEntry<K, V> entry = cache.get(key);
            if (entry == null) {
                return null;
            }
            rebalance(entry);

            return entry.getVal();
        } finally {
            lock.unlock();
        }

    }

    public void remove(K key){
        lock.lock();
        try {
            CacheEntry<K, V> existing = cache.get(key);

            remove(existing);
        } finally {
            lock.unlock();
        }

    }

    public void put(K key, V val){
        lock.lock();
        try {
            CacheEntry<K, V> entry = cache.get(key);
            if (entry != null){
                entry.setVal(val);
            } else {
                if (cache.size() >= capacity) {
                    removeFirst();
                }
                if (cache.size() < capacity){
                    entry = new CacheEntry<>(key, val);
                }
            }

            if (entry != null) {
                cache.put(key, entry);
                rebalance(entry);
            }
        } finally {
            lock.unlock();
        }

    }

    private void remove(CacheEntry<K, V> removed) {
        lock.lock();
        try {
            FrequencyNode<K, V> curr = freqMap.get(removed.getCounter());
            curr.getDll().remove(removed);
            cache.remove(removed.getKey());
        } finally {
            lock.unlock();
        }


    }
    private void removeFirst(){
        lock.lock();
        try {
            FrequencyNode<K, V> curr = freqMap.get(starting);
            if (!curr.getDll().isEmpty()){
                CacheEntry<K, V> removed = curr.getDll().removeFirst();
                if (removed != null){
                    cache.remove(removed.getKey());
                }
            }
            if (curr.getDll().isEmpty() && Objects.equals(curr.getFreqCount(), starting)) {
                if (curr.getPrev() == null && curr.getNext() == null){
                    starting = 1L;
                } else if (curr.getPrev() == null && curr.getNext() != null) {
                    System.out.println(curr.getFreqCount() + " freq");
                    starting = curr.getFreqCount();
                }
            }
        } finally {
            lock.unlock();
        }

    }


    private void rebalance(CacheEntry<K,V> entry) {
        lock.lock();
        try {
            FrequencyNode<K, V> curr = freqMap.computeIfAbsent(entry.getCounter(), k -> new FrequencyNode<>(k));

            curr.getDll().remove(entry);

            entry.incrementCounter();

            if (curr.getDll().isEmpty() && starting == entry.getCounter() -1) {
                starting = entry.getCounter();
            }

            FrequencyNode<K, V> next = freqMap.computeIfAbsent(entry.getCounter(), k-> new FrequencyNode<>(k));

            next.getDll().addLast(entry);
        } finally {
            lock.unlock();
        }

    }
}
