package InterviewQuestions.LRU.repositories;

public interface CacheRepository<K, V> {
    V getVal(K key);
    void removeKey(K key);
    void putKey(K key, V val);
}
