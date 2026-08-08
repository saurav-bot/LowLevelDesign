package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimitStrategy{
    Map<String, TokenBucket> bucketMap = new ConcurrentHashMap<>();

    public RateLimitResult isValid(RequestMetadata requestMetadata, List<RateLimitRule> ruleList) {
        List<TokenBucket> bucketList = getMatchedBuckets(requestMetadata, ruleList);
        List<TokenBucket> consumedBucket = new ArrayList<>();

        for (TokenBucket bucket: bucketList){
            if (bucket.tryConsume(requestMetadata.getTokenRequested())){
                consumedBucket.add(bucket);
            } else {
                for (TokenBucket bucket1: consumedBucket) {
                    bucket1.rollback(requestMetadata.getTokenRequested());
                }
                return new RateLimitResult(false, bucket.getRetryAfterSeconds(requestMetadata.getTokenRequested()), bucket.getKey());
            }
        }

        return new RateLimitResult();
    }

    private List<TokenBucket> getMatchedBuckets(RequestMetadata request, List<RateLimitRule> rules) {
        List<TokenBucket> buckets = new ArrayList<>();

        for(RateLimitRule rule : rules){
            String key = getKey(request, rule);
            if (key != null){
                TokenBucket bucket = bucketMap.computeIfAbsent(key, k -> new TokenBucket(k, rule.getCapacity(), rule.getRefillRatePerSec()));
                buckets.add(bucket);
            }
        }

        return buckets;
    }

    private String getKey(RequestMetadata requestMetadata, RateLimitRule rule) {
        switch (rule.getRuleType()) {
            case GLOBAL:
                return "global";
            case USER:
                return requestMetadata.getUserId() != null ? "user#" + requestMetadata.getUserId() : null;
            case IP:
                return requestMetadata.getUserIp() != null ? "ip#" + requestMetadata.getUserIp() : null;
            case RESOURCE:
                return requestMetadata.getResourcePath() != null && requestMetadata.getResourcePath().equals(rule.getTargetPattern()) ?
                        "resource#" + requestMetadata.getResourcePath() : null;
            default:
                return null;
        }
    }
}
