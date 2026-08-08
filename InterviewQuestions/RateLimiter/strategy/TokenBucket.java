package InterviewQuestions.RateLimiter.strategy;

import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final String key;
    private final long capacity;
    private final double refillRatePerSec;
    private double currentTokens;
    private long lastRefillTimestampNano;
    private final ReentrantLock lock;

    public TokenBucket(String key, long capacity, double refillRatePerSec) {
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
        this.key = key;
        this.lock = new ReentrantLock();
        this.currentTokens = capacity;
        this.lastRefillTimestampNano = System.nanoTime();
    }


    public boolean tryConsume(int tokensNeeded) {
        lock.lock();
        try {
            long now = System.nanoTime();
            double elapsed = (now-lastRefillTimestampNano)/1_000_000_000.0;
            currentTokens = Math.min(capacity, currentTokens + elapsed*refillRatePerSec);

            lastRefillTimestampNano = now;

            if (currentTokens >= tokensNeeded){
                currentTokens -= tokensNeeded;
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Error occurred while refilling "+ex.getMessage());
        } finally {
            lock.unlock();
        }

        return false;
    }

    public void rollback(int tokensConsumed) {
        lock.lock();
        try {
            currentTokens = Math.min(capacity, currentTokens + tokensConsumed);
        } finally {
            lock.unlock();
        }
    }

    public long getRetryAfterSeconds(int tokenRequested){
        lock.lock();
        try {
            if (currentTokens >= tokenRequested) {
                return 0;
            }

            double missingTokens = tokenRequested-currentTokens;
            double secondsRequired = missingTokens/refillRatePerSec;

            return Math.max(1, (long) Math.ceil(secondsRequired));
        } finally {
            lock.unlock();
        }
    }

    public String getKey() {
        return key;
    }
}
