package InterviewQuestions.LRU.entities;

import java.time.Instant;

public class CacheEntry<K,V> {
    K key;
    V val;
    Instant expiresAt;
    CacheEntry<K, V> prev;
    CacheEntry<K, V> next;

    public CacheEntry(K key, V val){
        this.key = key;
        this.val = val;
    }

    public CacheEntry(K key, V val, Instant expiresAt){
        this.key = key;
        this.val = val;
        this.expiresAt = expiresAt;
    }

    public K getKey(){
        return this.key;
    }

    public V getVal() {
        return this.val;
    }

    public void setVal(V val){
        this.val = val;
    }

    public boolean isExpired(){
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
