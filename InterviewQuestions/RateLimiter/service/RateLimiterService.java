package InterviewQuestions.RateLimiter.service;

import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;
import InterviewQuestions.RateLimiter.strategy.RateLimitStrategy;
import InterviewQuestions.RateLimiter.strategy.TokenBucketStrategy;

import java.util.ArrayList;
import java.util.List;

public class RateLimiterService {
    List<RateLimitRule> rateLimitRules = new ArrayList<>();
    private final RateLimitStrategy rateLimitStrategy;


    public void addRateLimitRules(RateLimitRule rateLimitRule) {
        this.rateLimitRules.add(rateLimitRule);
    }

    public RateLimiterService(RateLimitStrategy rateLimitStrategy) {
        this.rateLimitStrategy = rateLimitStrategy;
    }

    public RateLimiterService() {
        this(new TokenBucketStrategy());
    }

    public RateLimitResult isValid(RequestMetadata requestMetadata) {
        return rateLimitStrategy.isValid(requestMetadata, rateLimitRules);
    }
}
