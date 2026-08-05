package InterviewQuestions.LRU.repositories;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SegmentCache<K, V> implements CacheRepository<K, V>{
    private final int capacity;
    private final int segmentCount = 16;
    private final List<CacheRepository<K, V>> segments = new ArrayList<>();

    public SegmentCache(int capacity){
        this.capacity = capacity;
        for (int i=0; i<segmentCount; i++){
            segments.add(new InMemoryLRUCacheRepository<>(Math.max(1, this.capacity/segmentCount)));
        }
    }

    public V getVal(K key){
        return getSegment(key).getVal(key);
    }

    public void putKey(K key, V val){
        getSegment(key).putKey(key, val);
    }

    public void putKey(K key, V val, Instant expiresAt) {
        getSegment(key).putKey(key, val, expiresAt);
    }

    public void removeKey(K key){
        getSegment(key).removeKey(key);
    }

    public void cleanupExpiredEntries(){
        for (int i=0; i<segmentCount; i++){
            segments.get(i).cleanupExpiredEntries();
        }
    }

    private CacheRepository<K, V> getSegment(K key) {
        int index = Math.abs(key.hashCode())%segmentCount;
        return segments.get(index);
    }

}
