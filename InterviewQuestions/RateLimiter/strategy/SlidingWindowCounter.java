package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowCounter implements RateLimitStrategy{
//    private long getWindowMillis;

    static class Window{
        String key;
        long currentWindow;
        long currentCount;
        long previousCount;

        private final ReentrantLock lock = new ReentrantLock();

        public Window(String key) {
            this.key = key;
            this.currentCount = 0;
            this.currentWindow = 0;
            this.previousCount = 0;
        }


        public boolean tryConsume(long required, long windowMillis, long limit) {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                long currWindow = now / windowMillis;

                long windowDiff = currWindow - currentWindow;

                if (windowDiff == 1) {
                    previousCount = currentCount;
                    currentCount = 0;
                    currentWindow = currWindow;
                } else if (windowDiff > 1) {
                    previousCount = 0;
                    currentCount = 0;
                    currentWindow = currWindow;
                }

                double elapsedTimePercentageInCurrentWindow = (double) (now %  windowMillis) / windowMillis;
                double effectiveCount = previousCount * (1-elapsedTimePercentageInCurrentWindow) + currentCount;
//                System.out.println(effectiveCount + " " + previousCount + " " + currentCount + " " + required);
                if (effectiveCount + required <= limit) {
                    currentCount += required;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public void rollback(long required) {
            lock.lock();
            try {
                currentCount = Math.max(0, currentCount-required);
            } finally {
                lock.unlock();
            }
        }
    }


    private final long windowMillis;

    private final ConcurrentHashMap<String, Window> map = new ConcurrentHashMap<>();

    public SlidingWindowCounter(long windowMillis){
        this.windowMillis = windowMillis;
    }


    public RateLimitResult isValid(RequestMetadata request, List<RateLimitRule> rateLimitRuleList) {
        List<MatchedRule<Window>> matchedPairs = getMatchingWindows(request, rateLimitRuleList);
        List<Window> consumedWindows = new ArrayList<>();

        for(MatchedRule<Window> matchedPair : matchedPairs) {
            Window window = matchedPair.getLimiter();
            RateLimitRule rule = matchedPair.getRule();
            if (window.tryConsume(request.getTokenRequested(), windowMillis, rule.getCapacity())) {
                consumedWindows.add(window);
            } else {
                for (Window consumedWindow : consumedWindows) {
                    consumedWindow.rollback(request.getTokenRequested());
                }

                long remainingMillis = windowMillis - (System.currentTimeMillis() % windowMillis);
                long retryAfter = Math.max(1, (long) Math.ceil(remainingMillis / 1000.0));
                return new RateLimitResult(false, retryAfter, window.key);
//                return new RateLimitResult(false, 0, window.key);
            }

        }

        return new RateLimitResult();
    }

    private List<MatchedRule<Window>> getMatchingWindows(RequestMetadata request, List<RateLimitRule> rateLimitRules) {
        List<MatchedRule<Window>> matchedPairs = new ArrayList<>();

        for(RateLimitRule rule : rateLimitRules) {
            String key = generateKey(request, rule);

            if (key != null){
                Window  window = map.computeIfAbsent(key, k -> new Window(key));
                matchedPairs.add(new MatchedRule<>(window, rule));
            }
        }

        return matchedPairs;
    }

    private String generateKey(RequestMetadata requestMetadata, RateLimitRule rateLimitRule) {
        switch(rateLimitRule.getRuleType()) {
            case IP:
                return requestMetadata.getUserIp() != null ? "ip#" + requestMetadata.getUserIp() : null;
            case USER:
                return requestMetadata.getUserId() != null ? "user#" + requestMetadata.getUserId() : null;
            case GLOBAL:
                return "global";
            case RESOURCE:
                    return  requestMetadata.getResourcePath() != null && requestMetadata.getResourcePath().equals(rateLimitRule.getTargetPattern()) ? "resource#" + requestMetadata.getResourcePath() : null;
            default:
                return null;
        }
    }
}
