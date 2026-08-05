package InterviewQuestions.LRU.repositories;

import java.time.Instant;

public interface CacheRepository<K, V> {
    V getVal(K key);
    void removeKey(K key);
    void putKey(K key, V val);
    void putKey(K key, V val, Instant expiresAt);
    void cleanupExpiredEntries();
}
