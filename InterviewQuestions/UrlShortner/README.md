# URL Shortener (TinyURL / Bitly) - Low-Level Design & Architecture

A production-grade, thread-safe, and extensible Low-Level Design (LLD) for a URL Shortener system in Java.

---

## 1. System Requirements & Scope

### Functional Requirements
1. **URL Shortening:** Convert a valid long URL into a compact, unique 7-character Base62 string.
2. **URL Redirection:** Resolve a short code or short URL back to the original destination URL.
3. **Custom Alias Support:** Allow users to define custom short aliases (e.g., `https://short.it/railway`).
4. **Time-To-Live (TTL) & Expiration:** Support optional link expiration dates. Expired links are invalidated and cleaned up.
5. **Click Analytics:** Real-time, thread-safe access click tracking for each short link.
6. **URL Validation:** Validate incoming URLs for proper protocol schemes (`http`, `https`) and syntax.

### Non-Functional Requirements
1. **High Concurrency & Thread Safety:** Lock-free, non-blocking atomic operations with zero race conditions on concurrent reads and writes.
2. **SOLID Principles & Extensibility:** Strategy Pattern for short code generation algorithms.
3. **Clean Domain Errors:** Typed domain exceptions (`UrlNotFoundException`, `UrlExpiredException`, `AliasAlreadyExistException`, `UrlInvalidException`).

---

## 2. Architecture & Design Patterns

The architecture is divided into clean, decoupled layers:

```
┌─────────────────────────────────────────────────────────────┐
│                       Controller / API                      │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                         UrlService                          │
│  - URL validation & sanitization                            │
│  - TTL & Expiration enforcement                             │
│  - Collision retry loop                                     │
│  - Analytics tracking                                       │
└───────────────┬─────────────────────────────┬───────────────┘
                │                             │
┌───────────────▼──────────────┐  ┌───────────▼───────────────┐
│     ShortCodeGenerator       │  │       UrlRepository       │
│     << Strategy Interface >> │  │ << Repository Interface >>│
├──────────────────────────────┤  ├───────────────────────────┤
│ - Base62Generator (Counter)  │  │ - InMemoryUrlRepository   │
│ - MD5/Murmur3 HashGenerator  │  │ - RedisUrlRepository      │
│ - SnowflakeGenerator (KGS)   │  │ - SqlUrlRepository        │
└──────────────────────────────┘  └───────────────────────────┘
```

### Design Patterns Used
* **Strategy Pattern:** `ShortCodeGenerator` interface allows swapping code generation strategies at runtime without touching `UrlService`.
* **Repository Pattern:** `UrlRepository` abstracts storage, allowing seamless transition from in-memory maps to Redis, MongoDB, or PostgreSQL.
* **Dependency Injection (DIP):** `UrlService` depends solely on abstractions, enabling mock unit testing.

---

## 3. Concurrency Handling: In-Depth Analysis

### Single-Node / In-Memory Concurrency
1. **Lock-Free Deduplication (`putIfAbsent`):**
   * When multiple threads attempt to shorten the same long URL simultaneously, `longUrlMap.putIfAbsent(longUrl, newDetails)` ensures only one thread succeeds.
   * Competing threads receive the existing `UrlDetails` without blocking or creating duplicate short codes.
2. **Lock-Free Collision Rollback:**
   * When storing `shortCodeMap.putIfAbsent(shortCode, newDetails)`, if a collision occurs (e.g., from custom alias or hash collision), the repository atomically rolls back the `longUrlMap` entry and signals `UrlService` to retry.
3. **Atomic Click Tracking:**
   * `AtomicLong.incrementAndGet()` in `UrlDetails` guarantees lock-free, thread-safe counter increments across millions of concurrent redirects.

### Multi-Node / Distributed Concurrency
1. **Distributed ID Generation:**
   * **Twitter Snowflake ID / Central KGS:** Replaces local `AtomicLong` with 64-bit distributed IDs (Timestamp + Datacenter ID + Machine ID + Sequence).
2. **Database Level Constraints:**
   * `UNIQUE INDEX idx_short_code (short_code)` and `UNIQUE INDEX idx_original_url (original_url)`.
   * Atomic inserts via `INSERT INTO urls (...) VALUES (...) ON CONFLICT (original_url) DO NOTHING;`.
3. **Distributed Caching (Redis):**
   * Cache-Aside or Write-Through caching with Redis keys: `url:short:{shortCode}` and `url:long:{hash(longUrl)}`.

---

## 4. Supporting Multiple Short Codes for the Same Long URL

In real-world platforms (e.g. Bitly, marketing campaigns), **different users or campaigns often need distinct short links for the exact same destination URL** (e.g. user tracking, A/B testing).

### What Changes in the Design?

1. **Decouple the 1:1 Global Deduplication:**
   * Instead of a global `longUrlMap: Map<String, UrlDetails>`, introduce **User/Campaign Context** or change the mapping to **1-to-Many**.

2. **Updated Domain Model (`UrlDetails.java`):**
   ```java
   public class UrlDetails {
       private final String shortCode;
       private final String originalUrl;
       private final String userId;      // Or campaignId / organizationId
       private final Instant createdAt;
       private final Instant expiredAt;
       private final AtomicLong clickedCount;
   }
   ```

3. **Updated Repository Data Structures:**
   ```java
   public class MultiLinkUrlRepository implements UrlRepository {
       // 1. Unique shortCode -> UrlDetails (Primary key for redirection)
       private final Map<String, UrlDetails> shortCodeMap = new ConcurrentHashMap<>();

       // 2. User/Org + Long URL -> UrlDetails (Scoped Deduplication)
       // Key: "userId#originalUrl"
       private final Map<String, UrlDetails> userLongUrlMap = new ConcurrentHashMap<>();

       // 3. (Optional) All short codes created for a given long URL
       private final Map<String, Set<String>> longUrlToShortCodes = new ConcurrentHashMap<>();
   }
   ```

4. **Modes Supported in `UrlService`:**
   * **Mode A (Always Create New):** Bypasses `longUrlMap` check entirely; every call generates a new unique short code.
   * **Mode B (Per-User Deduplication):** Deduplicates if the *same user* shortens the same URL, but allows *different users* to get unique short links.

---

## 5. Interview Questions & Deep-Dive Topics

### Low-Level Design (LLD) Questions

#### Q1: Why Base62 instead of Base64 or Hexadecimal?
* **Base64** contains `+`, `/`, and `=` which are reserved/special characters in URLs and require URL encoding (`%2B`, `%2F`, `%3D`).
* **Hexadecimal (Base16)** only uses `[0-9a-f]`, requiring significantly longer short codes to represent the same number of URLs.
* **Base62** uses `[0-9a-zA-Z]`, is 100% URL-safe without encoding, and provides $62^7 \approx 3.52 \text{ trillion}$ combinations in just 7 characters.

#### Q2: How does the system handle hash collisions if using MD5 or Murmur3?
* When a candidate 7-char hash collides with an existing short code, `UrlService` appends an attempt counter or salt (e.g. `longUrl + "#1"`) and re-hashes up to `MAX_COLLISION_RETRIES`.

#### Q3: How do you handle expired URLs efficiently without scanning millions of rows?
1. **Lazy Deletion (On-Read):** When a user requests a short code, check `details.isExpired()`. If expired, delete and throw `UrlExpiredException`.
2. **Scheduled Background Sweeper:** A periodic cron job (`ScheduledExecutorService` / Quartz) cleans up expired records during off-peak hours.
3. **Redis TTL:** If using Redis, configure key TTL so Redis automatically purges expired keys.

#### Q4: Why HTTP 301 vs HTTP 302 / 307 for Redirection?
* **301 (Moved Permanently):** Browser caches the redirect locally. Subsequent clicks never hit the URL shortener server. (Saves server load, but breaks click analytics).
* **302 (Found) / 307 (Temporary Redirect):** Browser always queries the URL shortener first. Essential for accurate real-time analytics and telemetry.

---

### High-Level Design (HLD) & Scalability Questions

#### Q5: How do you design a Key Generation Service (KGS) to avoid distributed database locks?
* A standalone KGS pre-generates billions of unique random 7-character Base62 keys and stores them in a database table with two tables: `UsedKeys` and `AvailableKeys`.
* App servers load blocks of keys (e.g. 5,000 keys at a time) into local memory. When generating a short URL, the app server grabs a key from its local memory queue in $O(1)$ time with zero DB contention.

#### Q6: How do you partition/shard the database at scale?
* **Range-Based Sharding:** Shard by first character of short code (`[0-9]`, `[a-z]`, `[A-Z]`). *Drawback:* Hot partitions.
* **Hash-Based Sharding (Consistent Hashing):** Hash `shortCode` to determine the database partition. Distributes traffic evenly and supports dynamic scaling.

#### Q7: How would you prevent malicious URLs and abuse?
1. **Rate Limiting:** Token Bucket algorithm per user IP / API key (e.g. max 100 shortens/min).
2. **Malware / Phishing Blacklist:** Integrate with Google Safe Browsing API before saving long URLs.
3. **Self-Referencing Guard:** Validate that `longUrl` domain is not the shortener's own domain (`https://short.it/`).

---

## 6. How to Run the Demo

```bash
# Compile all files
javac -d out $(find InterviewQuestions/UrlShortner -name "*.java")

# Run the Demo
java -cp out InterviewQuestions.UrlShortner.TinyUrlDemo
```
