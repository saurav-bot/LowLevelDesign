package InterviewQuestions.LFU.repository;

public interface CacheRepository<K, V> {
    void put(K key, V val);
    V get(K key);
    void remove(K key);
}
