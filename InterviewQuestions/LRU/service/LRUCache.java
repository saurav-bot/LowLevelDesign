package InterviewQuestions.LRU.service;

import InterviewQuestions.LRU.repositories.CacheRepository;
import InterviewQuestions.LRU.repositories.InMemoryLRUCacheRepository;


public class LRUCache<K, V> {
    private final CacheRepository<K, V> cacheRepository;

    public LRUCache(CacheRepository<K, V> cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public LRUCache(int capacity){
        this(new InMemoryLRUCacheRepository<K, V>(capacity));
    }

    public LRUCache(){
        this(new InMemoryLRUCacheRepository<K, V>(100));
    }

    public V getValue(K key) {
        return cacheRepository.getVal(key);
    }

    public void putVal(K key, V val) {
        cacheRepository.putKey(key, val);
    }

}
