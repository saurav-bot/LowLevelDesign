package InterviewQuestions.UrlShortner.entities;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;


public class UrlDetails{
    private final String shortCode;
    private final String originalUrl;
    private final Instant createdAt;
    private final Instant expiredAt;
    private final AtomicLong clickedCount;

    public UrlDetails(String shortCode, String originalUrl, Instant expiredAt){
        this.createdAt = Instant.now();
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.expiredAt = expiredAt;
        this.clickedCount = new AtomicLong(0);
    }

    public long getClickCount() {
        return clickedCount.get();
    }

    public String getShortCode() {
        return this.shortCode;
    }
    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getExpiredAt() {
        return this.expiredAt;
    }

    public void incrementClickCount() {
        this.clickedCount.incrementAndGet();
    }

    public boolean isExpired(){
        return this.expiredAt != null && this.expiredAt.isBefore(Instant.now());
    }

    public String toString() {
        return this.shortCode + ": : " + this.getOriginalUrl();
    }
}