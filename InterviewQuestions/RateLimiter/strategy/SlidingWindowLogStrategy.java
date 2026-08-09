package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.MatchedRule;
import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLogStrategy implements RateLimitStrategy{
    static class LogWindow {
        String key;
        Deque<Long> logs;
        private final ReentrantLock lock;

        LogWindow(){
            logs = new ArrayDeque<>();
            lock = new ReentrantLock();
        }

        public boolean tryConsume(long required, long limit, long windowMillis, long now) {
            lock.lock();
            try {
                long windowStart = now - windowMillis;

                while (!logs.isEmpty() && logs.peekFirst() < windowStart) {
                    logs.pollFirst();
                }

                if (logs.size() + required <= limit) {
                    while (required > 0) {
                        logs.add(now);
                        required -= 1;
                    }
                    return true;
                }

                return false;
            } finally {
                lock.unlock();
            }

        }

        public void rollback(long required, long now) {
            lock.lock();
            try {
                while (!logs.isEmpty() && logs.peekLast() == now && required > 0){
                    logs.pollLast();
                    required -= 1;
                }
            } finally {
                lock.unlock();
            }
        }

        public long calculateRetryAfterSecs(long now, long windowMillis){
            lock.lock();
            try {
                if (logs.isEmpty()) return 1;

                long oldest = logs.peekFirst();
                long expirationTime = oldest + windowMillis;
                long waitMillis = expirationTime - now;

                return Math.max(1, (long) waitMillis/1000);
            } finally {
                lock.unlock();
            }
        }

    }

    ConcurrentHashMap<String, LogWindow> map = new ConcurrentHashMap<>();
    private final long windowMillis;

    public SlidingWindowLogStrategy(long windowMillis) {
        this.windowMillis = windowMillis;
    }

    public RateLimitResult isValid(RequestMetadata requestMetadata, List<RateLimitRule> rules){
        List<MatchedRule<LogWindow>> matchedRules = getMatchedRules(requestMetadata, rules);
        List<MatchedRule<LogWindow>> consumed = new ArrayList<>();

        long now = System.currentTimeMillis();
        for (MatchedRule<LogWindow> matchedRule : matchedRules) {
            LogWindow window = matchedRule.getLimiter();
            RateLimitRule rule = matchedRule.getRule();

            if (window.tryConsume(requestMetadata.getTokenRequested(), rule.getCapacity(), windowMillis, now)) {
                consumed.add(matchedRule);
            } else {
                for (MatchedRule<LogWindow> consume : consumed) {
                    consume.getLimiter().rollback(requestMetadata.getTokenRequested(), now);
                }

                return new RateLimitResult(false, window.calculateRetryAfterSecs(now, windowMillis), rule.getRuleId());
            }
        }

        return new RateLimitResult();
    }

    private List<MatchedRule<LogWindow>> getMatchedRules(RequestMetadata requestMetadata, List<RateLimitRule> rules){
        List<MatchedRule<LogWindow>> matched = new ArrayList<>();
        for (RateLimitRule rule: rules) {
            String key = generateKey(requestMetadata, rule);

            if (key != null){
                LogWindow window = map.computeIfAbsent(key, k -> new LogWindow());
                matched.add(new MatchedRule<>(window, rule));
            }
        }

        return matched;
    }

    private String generateKey(RequestMetadata requestMetadata, RateLimitRule rule) {
        switch(rule.getRuleType()){
            case USER:
                return requestMetadata.getUserId() != null ? "user#" + requestMetadata.getUserId() : null;
            case IP:
                return requestMetadata.getUserIp() != null ? "ip#" + requestMetadata.getUserIp() : null;
            case RESOURCE:
                return requestMetadata.getResourcePath() != null && requestMetadata.getResourcePath().equals(rule.getTargetPattern()) ? "resource#" + requestMetadata.getResourcePath() : null;
            case GLOBAL:
                return "global";
            default:
                return null;
        }
    }
}
