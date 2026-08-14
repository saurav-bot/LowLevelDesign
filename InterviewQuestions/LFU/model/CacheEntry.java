package InterviewQuestions.LFU.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class CacheEntry<K,V> {
    protected K key;
    protected V val;
    protected AtomicLong counter;
    protected Instant expiresAt;

    protected CacheEntry<K, V> next;
    protected CacheEntry<K, V> prev;

    public CacheEntry(K key, V val) {
        this.key = key;
        this.val = val;
        this.counter = new AtomicLong(0);
        this.expiresAt = null;
    }

    public CacheEntry() {
        this(null, null);
    }

    public V getVal() {
        return this.val;
    }

    public K getKey() {
        return this.key;
    }

    public long getCounter() {
        return  this.counter.get();
    }

    public void incrementCounter() {
        counter.incrementAndGet();
    }

    public void setVal(V val){
        this.val = val;
    }

    public boolean isExpired(){
        return this.expiresAt != null && this.expiresAt.isBefore(Instant.now());
    }
}
