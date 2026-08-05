# ⚡ Production-Grade High-Performance In-Memory Cache (LRU / TTL / Segmented Concurrency)

A complete, low-latency, thread-safe, generic in-memory cache implementation designed for high-concurrency systems.

---

## 🏛️ 1. Architecture Overview

The system follows a strict 3-tier enterprise architecture separating business time semantics, thread-safe storage abstractions, and constant-time pointer manipulation data structures.

```
┌────────────────────────────────────────────────────────┐
│               CacheService<K, V>                       │  (Service Layer)
│  - Human-friendly Duration APIs (e.g. Duration.ofMin)  │
│  - Manages Daemon ScheduledExecutorService lifecycle   │
└──────────────────────────┬─────────────────────────────┘
                           │ delegates to
┌──────────────────────────▼─────────────────────────────┐
│          <<interface>> CacheRepository<K, V>           │
├────────────────────────────────────────────────────────┤
│  + getVal(K key) : V                                   │
│  + putKey(K key, V val)                                │
│  + putKey(K key, V val, Instant expiresAt)             │
│  + removeKey(K key)                                    │
│  + cleanupExpiredEntries()                             │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
┌──────────────▼─────────────┐ ┌───────────▼─────────────┐
│ InMemoryLRUCacheRepository │ │   SegmentCache (Sharded)│
│  - ConcurrentHashMap       │ │   - Striped Concurrency │
│  - Custom DoublyLinkedList │ │   - 16 Independent Locks│
│  - ReentrantLock           │ │   - 16x Throughput      │
└────────────────────────────┘ └─────────────────────────┘
```

---

## ⏱️ 2. Time & Space Complexity

| Operation | Time Complexity | Space Complexity | Description |
|---|:---:|:---:|---|
| **`getValue(key)`** | **$O(1)$** | $O(1)$ | Hash lookup + Move node to tail of DoublyLinkedList |
| **`putVal(key, val)`** | **$O(1)$** | $O(1)$ | Hash insert/update + Move to tail + $O(1)$ eviction |
| **`removeKey(key)`** | **$O(1)$** | $O(1)$ | Hash deletion + $O(1)$ unlinking of node pointers |
| **Lazy Expiration** | **$O(1)$** | $O(1)$ | Evaluates `isExpired()` on read and evicts if stale |
| **Background Sweeper** | **$O(N)$** | $O(E)$ | Lock-free scan of map values; locks only on deletion ($E$ = expired) |

---

## 💡 3. Core Design Decisions & Engineering Trade-offs

### A. Why Custom Doubly Linked List instead of Java's `LinkedList`?
* `java.util.LinkedList` encapsulates its `Node` class privately (`private static class Node<E>`).
* To remove an item from `java.util.LinkedList`, you must call `list.remove(object)`, which performs a linear search from head to tail $\rightarrow$ **$O(N)$ latency bottleneck**.
* With a **Custom Doubly Linked List**, our `ConcurrentHashMap` stores direct pointers to `CacheEntry<K, V>`. Unlinking a node is strictly **$O(1)$** (`node.prev.next = node.next; node.next.prev = node.prev;`).

### B. Why Dummy `head` and `tail` Sentinel Nodes?
* Sentinel nodes eliminate all `null` pointer checks during `addLast`, `removeNode`, and `removeFirst`.
* Edge cases (empty list, 1-item list, head/tail insertion/deletion) are handled seamlessly without conditional branches.

### C. Dedicated Repositories vs. Pluggable `EvictionPolicy` Strategy
* **Academic Strategy Pattern (`EvictionPolicy<K>`)**: Separates data storage (`Map<K, V>`) from key tracking (`Map<K, Node>`). Requires **two separate hash lookups** and duplicate key memory on every read/write.
* **Dedicated Repository (Our Choice)**: Integrates value storage and list pointers directly into `CacheEntry<K, V>`. Guarantees a **single hash lookup** and zero memory duplication (matches Redis and Caffeine implementations).

---

## 🔒 4. Concurrency & Thread-Safety Deep Dive

### ⚠️ The LRU Concurrency Trap: "Read is a Write"
In a standard cache, `get(key)` is read-only. **In an LRU Cache, `get(key)` is a WRITE operation** because it mutates the Doubly Linked List (`moveToLast`).
* **Why `ReentrantReadWriteLock` fails naively:** If multiple threads acquire a `readLock()` to read different keys, both threads will mutate `dll` pointers concurrently, leading to corrupted pointer loops, memory leaks, or $100\%$ CPU infinite loops.

### 🛡️ Layer 1: Fine-Grained `ReentrantLock`
Each `InMemoryLRUCacheRepository` guards its internal Doubly Linked List mutations with a `ReentrantLock`.

### ⚡ Layer 2: Segmented / Striped Cache (`SegmentCache`)
Instead of a single global lock, `SegmentCache` stripes data across $N = 16$ independent mini-caches:
* **$16\times$ Throughput:** 16 threads writing/reading different keys run fully parallel with zero lock contention.
* **Hash Bit Masking:** Uses `(key.hashCode() & 0x7fffffff) % segmentCount` to protect against negative hash codes and `Integer.MIN_VALUE`.

---

## 🧹 5. Expiration (TTL) & Memory Management

The cache implements a **Hybrid Two-Tier Expiration Model**:

### 1. Passive / Lazy Expiration (On-Read — $O(1)$)
When `getVal(key)` is invoked:
* Checks `node.isExpired()`.
* If expired, immediately unlinks and removes the key and returns `null`.

### 2. Active / Background Periodic Sweeper (Zero "Stop-The-World")
* Managed by a `ScheduledExecutorService` using `scheduleWithFixedDelay`.
* **Lock-Free Map Iteration:** Because `map` is a `ConcurrentHashMap`, the cleaner iterates over `map.values()` **without acquiring a lock**, avoiding application freezes.
* **Micro-Locks on Eviction:** Acquires `lock.lock()` solely when unlinking an expired node ($< 1\,\mu\text{s}$).
* **Daemon Thread (`setDaemon(true)`):** Ensures background cleanup never prevents the JVM from shutting down cleanly.

---

## ⚠️ 6. Critical Bugs & Gotchas (What Can Go Wrong)

| Pitfall / Bug | What Happens | How We Prevented It |
|---|---|---|
| **Negative Hash Code** | `key.hashCode() % 16` returns negative numbers (e.g. `-14`), throwing `ArrayIndexOutOfBoundsException`. | Mask sign bit: `(key.hashCode() & 0x7fffffff) % 16`. |
| **`Integer.MIN_VALUE` Overflow** | `Math.abs(Integer.MIN_VALUE)` is still negative (`-2147483648`) due to 32-bit integer overflow. | Use bitwise `& 0x7fffffff` instead of `Math.abs()`. |
| **Fail-Fast Iterator Crash** | Iterating over a standard `HashMap` while another thread calls `put()` throws `ConcurrentModificationException`. | Use `ConcurrentHashMap` with weakly consistent iterators. |
| **Stop-the-World Cleaner** | Locking the entire repository during background scan freezes all user requests for milliseconds. | Scan `ConcurrentHashMap.values()` lock-free; lock only on `removeKey()`. |
| **Silent Scheduler Death** | An unhandled exception in `cleanupExpiredEntries()` causes `ScheduledExecutorService` to permanently cancel future runs. | Wrap cleanup routine in a comprehensive `try-catch(Throwable t)`. |
| **Non-Daemon Cleaner** | Scheduler thread prevents JVM from exiting when `main()` completes. | Set `t.setDaemon(true)` in `ThreadFactory`. |
| **Hot Segment Problem** | Non-uniform traffic can fill 1 segment and trigger early evictions while other segments sit empty. | Use high-entropy hash mixing (e.g. `ConcurrentHashMap.spread()`). |

---

## 🎯 7. Top Interview Questions & Answers

### Q1: How does Java's built-in `LinkedHashMap` implement an LRU Cache?
**Answer:** `LinkedHashMap` has a constructor with `accessOrder = true` (orders entries by access rather than insertion) and an overridable hook:
```java
Map<K, V> lru = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
};
```

---

### Q2: Why is `scheduleWithFixedDelay` preferred over `scheduleAtFixedRate` for TTL cleanup?
**Answer:** 
* `scheduleAtFixedRate` attempts to execute tasks at fixed intervals. If a cleanup task takes longer than the interval (e.g. due to heavy GC or system load), subsequent executions will fire back-to-back with **0ms delay (catch-up storm)**, starving the CPU.
* `scheduleWithFixedDelay` guarantees a mandatory rest period *after* the completion of the previous task before triggering the next run.

---

### Q3: How do modern high-performance caches (like Caffeine) achieve Lock-Free Reads?
**Answer:** Caffeine avoids locking on `get()` by implementing **Read Buffering**:
1. `get(key)` reads directly from `ConcurrentHashMap` in a **100% lock-free** manner.
2. It writes the access event (`key`) into a lock-free **MPSC (Multi-Producer Single-Consumer) RingBuffer**.
3. A background maintenance worker asynchronously drains the buffer in batches to reorder the eviction data structures.

---

### Q4: How would you extend this architecture to support LFU (Least Frequently Used)?
**Answer:** Create an `InMemoryLFUCacheRepository<K, V> implements CacheRepository<K, V>`:
1. Maintain a `Map<K, CacheEntry<K, V>>` for values and frequency counters.
2. Maintain a `Map<Integer, DoublyLinkedList<K>>` mapping each frequency count to its list of keys.
3. Track `minFrequency`.
4. On `get()`: increment frequency, move node to `freq + 1` list, and update `minFrequency`.
5. On eviction: pop the oldest element from `countToKeysMap.get(minFrequency)`.

---

## 🚀 8. How to Run Demo

```bash
# Compile
javac -d out $(find InterviewQuestions/LRU -name "*.java")

# Run Demo
java -cp out InterviewQuestions.LRU.LRUCacheDemo
```
