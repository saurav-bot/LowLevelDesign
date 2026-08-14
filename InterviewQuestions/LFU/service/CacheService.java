package InterviewQuestions.LFU.service;

import InterviewQuestions.LFU.repository.CacheRepository;
import InterviewQuestions.LFU.repository.InMemoryLfuRepository;

public class CacheService<K, V> {
    private final CacheRepository<K,V> cacheRepository;
    private int MAX_CAPCITY = 100;

    public CacheService() {
        this.cacheRepository = new InMemoryLfuRepository<>(MAX_CAPCITY);
    }

    public void put(K key, V val) {
        cacheRepository.put(key, val);
    }

    public V get(K key){
        return cacheRepository.get(key);
    }

    public void remove(K key){
        cacheRepository.remove(key);
    }

}
