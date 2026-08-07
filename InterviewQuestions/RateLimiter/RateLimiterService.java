package InterviewQuestions.RateLimiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    Map<String, TokenBucket> bucketMap = new ConcurrentHashMap<>();
    List<RateLimitRule> rateLimitRules = new ArrayList<>();

    public void addRateLimitRules(RateLimitRule rateLimitRule) {
        this.rateLimitRules.add(rateLimitRule);
    }

    public RateLimitResult isValid(RequestMetadata requestMetadata) {
        List<TokenBucket> bucketList = getMatchedBuckets(requestMetadata);
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

    private List<TokenBucket> getMatchedBuckets(RequestMetadata requestMetadata) {
        List<TokenBucket> bucketList = new ArrayList<>();

        for(RateLimitRule rateLimitRule: rateLimitRules){
            String key = generateKey(rateLimitRule, requestMetadata);

            if (key != null) {
                TokenBucket bucket = bucketMap.computeIfAbsent(key, r ->
                        new TokenBucket(r, rateLimitRule.getCapacity(), rateLimitRule.getRefillRatePerSec()));

                bucketList.add(bucket);
            }
        }
        return bucketList;
    }

    private String generateKey(RateLimitRule rateLimitRule, RequestMetadata metadata) {
        switch (rateLimitRule.getRuleType()) {
            case USER:
                return metadata.getUserId() != null ? "USER:"+metadata.getUserId() : null;
            case IP:
                return metadata.getUserIp() != null ? "IP:"+metadata.getUserIp() : null;
            case GLOBAL:
                return "GLOBAL";
            case RESOURCE:
                return metadata.getResourcePath() != null && metadata.getResourcePath().equals(rateLimitRule.getTargetPattern()) ? "RESOURCE:" + metadata.getResourcePath() : null;
            default:
                return null;
        }
    }

}
