package InterviewQuestions.LRU.service;

import InterviewQuestions.LRU.repositories.CacheRepository;
import InterviewQuestions.LRU.repositories.InMemoryLRUCacheRepository;
import InterviewQuestions.LRU.repositories.SegmentCache;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheService<K, V> implements AutoCloseable{
    private final CacheRepository<K, V> cacheRepository;
    private final ScheduledExecutorService scheduler;

    public CacheService(CacheRepository<K, V> cacheRepository, Duration cleanupInterval) {
        this.cacheRepository = cacheRepository;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "lru-ttl-cleaner");
            t.setDaemon(true);
            return t;
        });

        if (cleanupInterval != null && !cleanupInterval.isZero()) {
            this.scheduler.scheduleWithFixedDelay(
                    this::cleanupExpired,
                    cleanupInterval.toMillis(),
                    cleanupInterval.toMillis(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public CacheService(int capacity){
        this(new InMemoryLRUCacheRepository<K, V>(capacity),  Duration.ofMillis(100));
    }

//    public CacheService(){
//        this(new InMemoryLRUCacheRepository<K, V>(100),  Duration.ofMillis(100));
//    }

    public CacheService(){
        this(new SegmentCache<>(100), Duration.ofMillis(100));
    }

    public V getValue(K key) {
        return cacheRepository.getVal(key);
    }

    public void putVal(K key, V val, Duration ttl){
        Instant expiresAt = ttl != null ? Instant.now().plus(ttl) : null;
        cacheRepository.putKey(key, val, expiresAt);
    }

    public void putVal(K key, V val) {
        cacheRepository.putKey(key, val);
    }

    public void cleanupExpired(){
        try {
            cacheRepository.cleanupExpiredEntries();
        } catch (Exception ex) {
            System.out.println("Cleanup process is terminated due to error: " + ex.getMessage());
        }
    }

    public void close() {
        scheduler.shutdown();
    }

}
