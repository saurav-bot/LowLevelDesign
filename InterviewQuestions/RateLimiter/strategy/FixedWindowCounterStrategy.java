package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.MatchedRule;
import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowCounterStrategy implements RateLimitStrategy {

    static class FixedWindow {
        private final String key;
        private long currentWindowId;
        private long currentCount;
        private final ReentrantLock lock = new ReentrantLock();

        public FixedWindow(String key) {
            this.key = key;
        }

        public boolean tryConsume(long now, long windowMillis, long limit, int required) {
            lock.lock();
            try {
                long windowId = now / windowMillis;

                // 1. Reset if time crossed into a new window
                if (windowId > currentWindowId) {
                    currentWindowId = windowId;
                    currentCount = 0;
                } else if (windowId < currentWindowId) {
                    // Safety guard for clock skew (NTP jitter)
                    currentWindowId = windowId;
                }

                // 2. Check capacity
                if (currentCount + required <= limit) {
                    currentCount += required;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public void rollback(int required) {
            lock.lock();
            try {
                currentCount = Math.max(0, currentCount - required);
            } finally {
                lock.unlock();
            }
        }

        public long calculateRetryAfterSeconds(long now, long windowMillis) {
            lock.lock();
            try {
                long nextWindowStartTime = (currentWindowId + 1) * windowMillis;
                long waitMillis = nextWindowStartTime - now;
                return Math.max(1, (long) Math.ceil(waitMillis / 1000.0));
            } finally {
                lock.unlock();
            }
        }

        public String getKey() {
            return key;
        }
    }

    private final long windowMillis;
    private final ConcurrentHashMap<String, FixedWindow> map = new ConcurrentHashMap<>();

    public FixedWindowCounterStrategy(long windowMillis) {
        this.windowMillis = windowMillis;
    }

    @Override
    public RateLimitResult isValid(RequestMetadata request, List<RateLimitRule> rules) {
        long now = System.currentTimeMillis();
        int required = request.getTokenRequested();

        List<MatchedRule<FixedWindow>> matchedRules = getMatchedRules(request, rules);
        List<MatchedRule<FixedWindow>> consumedRules = new ArrayList<>();

        for (MatchedRule<FixedWindow> matched : matchedRules) {
            FixedWindow window = matched.getLimiter();
            RateLimitRule rule = matched.getRule();

            if (window.tryConsume(now, windowMillis, rule.getCapacity(), required)) {
                consumedRules.add(matched);
            } else {
                // Rollback consumed
                for (MatchedRule<FixedWindow> consumed : consumedRules) {
                    consumed.getLimiter().rollback(required);
                }

                long retryAfter = window.calculateRetryAfterSeconds(now, windowMillis);
                return new RateLimitResult(false, retryAfter, window.getKey());
            }
        }

        return new RateLimitResult(true, 0, null);
    }

    private List<MatchedRule<FixedWindow>> getMatchedRules(RequestMetadata request, List<RateLimitRule> rules) {
        List<MatchedRule<FixedWindow>> matched = new ArrayList<>();
        for (RateLimitRule rule : rules) {
            String key = generateKey(request, rule);
            if (key != null) {
                FixedWindow window = map.computeIfAbsent(key, FixedWindow::new);
                matched.add(new MatchedRule<>(window, rule));
            }
        }
        return matched;
    }

    private String generateKey(RequestMetadata request, RateLimitRule rule) {
        switch (rule.getRuleType()) {
            case GLOBAL: return "global";
            case USER: return request.getUserId() != null ? "user#" + request.getUserId() : null;
            case IP: return request.getUserIp() != null ? "ip#" + request.getUserIp() : null;
            case RESOURCE:
                return request.getResourcePath() != null && request.getResourcePath().equals(rule.getTargetPattern())
                        ? "resource#" + request.getResourcePath() : null;
            default: return null;
        }
    }
}
