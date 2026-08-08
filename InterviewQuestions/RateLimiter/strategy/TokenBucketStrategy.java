package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.MatchedRule;
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
        List<MatchedRule<TokenBucket>> bucketList = getMatchedBuckets(requestMetadata, ruleList);
        List<TokenBucket> consumedBucket = new ArrayList<>();

        for (MatchedRule<TokenBucket> matchedRule: bucketList){
            TokenBucket bucket = matchedRule.getLimiter();
            RateLimitRule rule = matchedRule.getRule();

            if (bucket.tryConsume(requestMetadata.getTokenRequested(), rule.getRefillRatePerSec(), rule.getCapacity())){
                consumedBucket.add(bucket);
            } else {
                for (TokenBucket bucket1: consumedBucket) {
                    bucket1.rollback(requestMetadata.getTokenRequested(), rule.getCapacity());
                }
                return new RateLimitResult(false, bucket.getRetryAfterSeconds(requestMetadata.getTokenRequested(), rule.getRefillRatePerSec()), bucket.getKey());
            }
        }

        return new RateLimitResult();
    }

    private List<MatchedRule<TokenBucket>> getMatchedBuckets(RequestMetadata request, List<RateLimitRule> rules) {
        List<MatchedRule<TokenBucket>> matchedBuckets = new ArrayList<>();

        for(RateLimitRule rule : rules){
            String key = getKey(request, rule);
            if (key != null){
                TokenBucket bucket = bucketMap.computeIfAbsent(key, k -> new TokenBucket(k, rule.getCapacity()));
                matchedBuckets.add(new MatchedRule<>(bucket, rule));
            }
        }

        return matchedBuckets;
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
