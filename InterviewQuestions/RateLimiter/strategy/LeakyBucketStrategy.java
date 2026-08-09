package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.MatchedRule;
import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucketStrategy implements RateLimitStrategy{
    private final ConcurrentHashMap<String, LeakyBucket> map = new ConcurrentHashMap<>();

    static class LeakyBucket{
        double waterLevel;
        long lastRefillTimestampInNano;
        ReentrantLock lock;

        public LeakyBucket() {
            this.lock = new ReentrantLock();
            lastRefillTimestampInNano = System.nanoTime();
            waterLevel = 0;
        }

        public boolean tryConsume(long requiredToken, long capacity, double leakRatePerSec)  {
            lock.lock();

            try {
                long now = System.nanoTime();
                double elapsedSec = (now-lastRefillTimestampInNano) / 1_00_00_00_000.0;

                double leaked = elapsedSec*leakRatePerSec;
                waterLevel = Math.max(0, waterLevel-leaked);
                lastRefillTimestampInNano = now;

                if (waterLevel + requiredToken <= capacity) {
                    waterLevel += requiredToken;
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
                waterLevel = Math.max(0, waterLevel-required);
            } finally {
                lock.unlock();
            }
        }

        public long calculateRetryAfterSecs(long required, long capacity, double leakRatePerSec) {
            lock.lock();
            try {
                double shortFall = required - (capacity-waterLevel);
                double secondsToWait = shortFall / leakRatePerSec;

                return Math.max(1, (long) Math.ceil(secondsToWait));
            } finally {
                lock.unlock();
            }


        }
    }

    public RateLimitResult isValid(RequestMetadata requestMetadata, List<RateLimitRule> rateLimitRuleList) {
        List<MatchedRule<LeakyBucket>> matchedRules = getMatchedRules(requestMetadata, rateLimitRuleList);
        List<MatchedRule<LeakyBucket>> consumedRules = new ArrayList<>();

        for (MatchedRule<LeakyBucket> matchedRule : matchedRules) {
            LeakyBucket bucket = matchedRule.getLimiter();
            RateLimitRule rule = matchedRule.getRule();

            if (bucket.tryConsume(requestMetadata.getTokenRequested(), rule.getCapacity(), rule.getLeakRatePerSec())) {
                consumedRules.add(matchedRule);
            } else {
                for (MatchedRule<LeakyBucket> consumedRule : consumedRules){
                    consumedRule.getLimiter().rollback(requestMetadata.getTokenRequested());
                }

                return new RateLimitResult(false, bucket.calculateRetryAfterSecs(requestMetadata.getTokenRequested(), rule.getCapacity(), rule.getLeakRatePerSec()), rule.getRuleId());
            }
        }
        return new RateLimitResult();
    }

    private List<MatchedRule<LeakyBucket>> getMatchedRules(RequestMetadata requestMetadata, List<RateLimitRule> rateLimitRules) {
        List<MatchedRule<LeakyBucket>> matchedRules = new ArrayList<>();

        for(RateLimitRule rule : rateLimitRules) {
            String key = generateKey(requestMetadata, rule);

            if (key != null) {
                LeakyBucket leakyBucket = map.computeIfAbsent(key, k -> new LeakyBucket());
                matchedRules.add(new MatchedRule<>(leakyBucket, rule));
            }
        }

        return matchedRules;
    }

    private String generateKey(RequestMetadata request, RateLimitRule rule) {
        switch (rule.getRuleType()) {
            case USER:
                return request.getUserId()!=null ? "user#" + request.getUserId() : null;
            case IP:
                return request.getUserIp() != null ? "ip#" + request.getUserIp() : null;
            case RESOURCE:
                return request.getResourcePath() != null && request.getResourcePath().equals(rule.getTargetPattern()) ? "resource#" + request.getResourcePath() : null;
            case GLOBAL:
                return "global";
            default:
                return null;
        }
    }
}
