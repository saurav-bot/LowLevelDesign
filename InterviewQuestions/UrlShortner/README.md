# URL Shortener (TinyURL / Bitly) - Low-Level Design & Architecture

A production-grade, thread-safe, and extensible Low-Level Design (LLD) for a URL Shortener system in Java.

---

## 1. System Requirements & Scope

### Functional Requirements
1. **URL Shortening:** Convert a valid long URL into a compact, unique 7-character Base62 string.
2. **URL Redirection:** Resolve a short code or short URL back to the original destination URL in $O(1)$ time.
3. **Custom Alias Support:** Allow users to define custom short aliases (e.g., `https://short.it/railway`).
4. **Time-To-Live (TTL) & Expiration:** Support optional link expiration dates. Expired links are invalidated and cleaned up.
5. **Click Analytics:** Real-time, thread-safe access click tracking for each short link.
6. **URL Validation & Security:** Validate incoming URLs for proper protocol schemes (`http`, `https`), valid hosts, and SSRF protection.

### Non-Functional Requirements
1. **High Concurrency & Thread Safety:** Lock-free, non-blocking atomic operations with zero race conditions on concurrent reads and writes.
2. **SOLID Principles & Extensibility:** Strategy Pattern for short code generation algorithms and Repository Pattern for persistence.
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
│  - URL validation & SSRF sanitization                       │
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

### Single-Node / In-Memory Concurrency (Lock-Free)
1. **Lock-Free Deduplication (`putIfAbsent`):**
   * When multiple threads attempt to shorten the same long URL simultaneously, `longUrlMap.putIfAbsent(longUrl, newDetails)` ensures only one thread succeeds.
   * Competing threads receive the existing `UrlDetails` without blocking or creating duplicate short codes.
2. **Lock-Free Collision Rollback:**
   * When storing `shortCodeMap.putIfAbsent(shortCode, newDetails)`, if a collision occurs (e.g., from custom alias or hash collision), the repository atomically rolls back the `longUrlMap` entry using the two-argument `longUrlMap.remove(originalUrl, newDetails)` and signals `UrlService` to retry.
3. **Dedicated Custom Alias Pipeline:**
   * `saveCustomAlias` operates atomically on `shortCodeMap` without blocking on existing `longUrlMap` entries, updating the canonical pointer with `put()`.
4. **Atomic Click Tracking:**
   * `AtomicLong` / `LongAdder` guarantees thread-safe counter increments across millions of concurrent redirects without locking.

### Multi-Node / Distributed Concurrency
1. **Distributed ID Generation:**
   * **Twitter Snowflake ID / Central KGS:** Replaces local counters with 64-bit distributed IDs (Timestamp + Datacenter ID + Machine ID + Sequence).
2. **Database Level Constraints:**
   * `UNIQUE INDEX idx_short_code (short_code)` and `UNIQUE INDEX idx_original_url (original_url)`.
   * Atomic inserts via `INSERT INTO urls (...) VALUES (...) ON CONFLICT (short_code) DO NOTHING;`.
3. **Distributed Caching (Redis):**
   * Cache-Aside caching with Redis keys: `url:short:{shortCode}`.

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

## 5. Comprehensive Senior & Staff Interview Questions

---

### 🔹 Q1: Why Base62 instead of Base64 or Hexadecimal?

* **Base64** contains `+`, `/`, and `=` characters. In URLs, these are reserved special characters requiring URL encoding (`%2B`, `%2F`, `%3D`), which makes links longer, uglier, and prone to parsing errors across email/chat clients.
* **Hexadecimal (Base16)** only uses `[0-9a-f]`. A 7-character Hex string only yields $16^7 \approx 268 \text{ million}$ combinations.
* **Base62** uses `[0-9a-zA-Z]`, is 100% URL-safe without encoding, and provides:
  $$\text{Capacity} = 62^7 = 3,521,614,606,208 \approx 3.52 \text{ Trillion Unique URLs}$$
  At 1,000 URLs generated per second, 7 characters will last over **110 years** without collisions.

---

### 🔹 Q2: How do you prevent Link Enumeration & Scraping with Sequential IDs?

* **The Problem:** If `Base62(1000000)` produces `4c93`, an attacker can easily query `4c92`, `4c94`, etc., to scrape all company links, private documents, or unlisted URLs.
* **The Solution (Bijective Permutation / Feistel Cipher):**
  Instead of hashing (which introduces collisions and retry loops) or encrypting (which increases code length), pass the sequential 64-bit integer through a **reversible, bijective bit-mixing function (Feistel Cipher / MurmurHash3 finalizer)** before Base62 encoding:
  ```java
  public long permute(long id) {
      id ^= (id >>> 33);
      id *= 0xff51afd7ed558ccdL;
      id ^= (id >>> 33);
      id *= 0xc4ceb9fe1a85ec53L;
      id ^= (id >>> 33);
      return id & 0x7FFFFFFFFFFFFFFFL; // Keep positive
  }
  ```
  * **Result:** Guaranteed **zero collisions** (1-to-1 mathematical mapping) while IDs appear completely random and non-sequential to external users (`1 -> 7xK9pQ`, `2 -> 2mN3vL`).

---

### 🔹 Q3: Concurrency & Rollback Safety in `saveIfAbsent`

* **The Problem:** In a lock-free repository with dual maps (`longUrlMap` and `shortCodeMap`), if Thread 1 fails at `shortCodeMap.putIfAbsent`, it must roll back its entry in `longUrlMap`.
* **The Danger:** Calling `longUrlMap.remove(originalUrl)` might remove an entry inserted or updated concurrently by another thread (e.g. a custom alias or concurrent request).
* **The Solution:** Use the **two-argument atomic CAS `remove(key, value)`**:
  ```java
  public UrlDetails saveIfAbsent(UrlDetails urlDetails) {
      UrlDetails existing = longUrlMap.putIfAbsent(urlDetails.getOriginalUrl(), urlDetails);
      if (existing != null) return existing;

      UrlDetails shortExisting = shortCodeMap.putIfAbsent(urlDetails.getShortCode(), urlDetails);
      if (shortExisting != null) {
          // Atomically remove ONLY if the map still points to THIS exact urlDetails instance:
          longUrlMap.remove(urlDetails.getOriginalUrl(), urlDetails);
          return null; // Signals retry
      }
      return urlDetails;
  }
  ```

---

### 🔹 Q4: Click Analytics Bottleneck Under 50,000 Clicks/Second

* **Contention with `AtomicLong`:**
  Under high concurrency across multi-core CPUs, hundreds of threads spin in CAS loops on the same memory address, causing CPU cache-line bouncing (MESI protocol invalidation) and severe latency degradation.
* **Java Solution:** Substitute `AtomicLong` with **`java.util.concurrent.atomic.LongAdder`**:
  * `LongAdder` maintains an array of cell counters across CPU cores, eliminating contention during writes. Sums are computed on-demand during read.
* **Distributed Scalable Solution:** Decouple redirect latency ($O(1)$ read) from analytics writes using an **Event-Driven Asynchronous Pipeline**:
  ```
  User Click ──> [ Redirect 302 ] (Returns in < 5ms)
                       │
                       ▼ (Async Fire-and-Forget)
                 [ Kafka Topic: url_clicks ]
                       │
                       ▼
              [ Flink / Kafka Consumer ] ──> Batched increments to Redis / ClickHouse
  ```
* **Unique Visitors Tracking:** Use **HyperLogLog** probabilistic data structures in Redis (`PFADD short_code:visitors ip_hash`) to track millions of unique visitors with only 12KB of fixed memory per link.

---

### 🔹 Q5: Cache Stampede (Thundering Herd) & Invalidation Strategy

* **The Problem:** When a viral short URL expires in Redis, thousands of concurrent requests miss the cache simultaneously and slam the primary database.
* **Mitigation 1: Request Coalescing (Single-Flight Pattern):**
  Use a concurrent mutex / `CompletableFuture` map so only **one** thread queries the database for the missing key, while all other concurrent requests wait for that single result and read from cache.
* **Mitigation 2: Probabilistic Early Expiration (XFetch Algorithm):**
  Compute a probability to refresh the cache in the background *before* the key hard-expires:
  $$\Delta - \beta \cdot \ln(\text{random}()) \cdot \text{computeTime} > \text{TTL}$$
* **Cache Invalidation Ordering:**
  Always **Delete from Database first, then Delete from Cache** (Cache-Aside pattern). Deleting from cache before DB allows a concurrent reader to fetch stale data from the DB and refill the cache with outdated info.

---

### 🔹 Q6: Database Schema, Indexes, and Zero-Lock Inserts

```sql
CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(16) NOT NULL,
    original_url TEXT NOT NULL,
    user_id VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP WITH TIME ZONE,
    click_count BIGINT DEFAULT 0,
    CONSTRAINT uq_short_code UNIQUE (short_code)
);

-- Hot path index for redirection:
CREATE INDEX idx_urls_short_code ON urls(short_code);

-- Partial index for background cleanup of expired links:
CREATE INDEX idx_urls_expired_at ON urls(expired_at) WHERE expired_at IS NOT NULL;
```

#### Lock-Free SQL Inserts:
* **Custom Alias Insert:**
  ```sql
  INSERT INTO urls (short_code, original_url, expired_at)
  VALUES ('railway', 'https://destination.com', '2026-12-31')
  ON CONFLICT (short_code) DO NOTHING;
  -- If rows_affected == 0 -> Alias is already taken (Return 409 Conflict)
  ```
* **Auto-Generated Deduplication Insert:**
  ```sql
  INSERT INTO urls (short_code, original_url, expired_at)
  VALUES ('4c93', 'https://destination.com', NULL)
  ON CONFLICT (original_url) DO UPDATE SET original_url = EXCLUDED.original_url
  RETURNING short_code;
  ```

---

### 🔹 Q7: Security: SSRF, Phishing, and Infinite Redirect Loops

1. **Infinite / Self-Referencing Loop Prevention:**
   * Validate that the destination URL's host is not our own domain (`short.it`) or any known URL shortener affiliate.
2. **SSRF (Server-Side Request Forgery) Protection:**
   * Resolve host DNS before shortening.
   * Reject all loopback (`127.0.0.0/8`, `::1`), private RFC 1918 networks (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), and cloud link-local metadata endpoints (`169.254.169.254`).
3. **Phishing & Malware Protection:**
   * Integrate an asynchronous check against the **Google Safe Browsing API** or **PhishTank** blocklists.

---

### 🔹 Q8: Distributed Key Generation Service (KGS)

* **Architecture:** A dedicated service pre-generates random 7-character Base62 keys offline and maintains two tables: `available_keys` and `used_keys`.
* **Range Allocation:** Application servers request a batch of keys (e.g. 5,000 keys) upon startup and store them in an in-memory queue (`ConcurrentLinkedQueue`).
* **Zero DB Contention:** When shortening URLs, app servers pop keys directly from memory in $O(1)$ time with 0ms DB latency. If an app server crashes, unallocated keys from its local buffer are marked lost or reclaimed by a reaper process.

---

### 🔹 Q9: URL Expiration & Cleanup Strategies

1. **Lazy Deletion (On-Read):**
   * During redirection, check `urlDetails.isExpired()`. If expired, delete and throw `UrlAlreadyExpiredException`.
2. **Scheduled Batch Sweeper:**
   * Periodic cron job (e.g. every hour) runs chunked deletions using index:
     ```sql
     DELETE FROM urls WHERE id IN (
         SELECT id FROM urls WHERE expired_at < NOW() LIMIT 5000
     );
     ```
3. **Redis TTL:**
   * If cached in Redis, set key TTL = `expiredAt - now()` so Redis evicts expired keys automatically.

---

### 🔹 Q10: HTTP 301 vs HTTP 302 / 307 Redirection Trade-offs

| HTTP Status | Browser Behavior | Server Load | Click Analytics |
|---|---|---|---|
| **301 Moved Permanently** | Browser caches redirect locally | Lowest (Subsequent clicks bypass shortener) | ❌ Broken (Clicks never hit server) |
| **302 Found (Temporary)** | Browser queries shortener on every click | Higher | ✅ 100% Accurate Real-Time Analytics |
| **307 Temporary Redirect** | Guarantees HTTP method preservation (POST remains POST) | Higher | ✅ Accurate Analytics + Safe for APIs |

**Verdict:** URL shortener platforms (Bitly, TinyURL) always use **HTTP 302 / 307** to ensure every click is logged for analytics and expired links can be stopped immediately.

---

## 6. How to Run the Demo

```bash
# 1. Compile all Java files:
javac -d out $(find InterviewQuestions/UrlShortner -name "*.java")

# 2. Run the executable Demo:
java -cp out InterviewQuestions.UrlShortner.TinyUrlDemo
```
