package InterviewQuestions.RateLimiter.strategy;

import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;

import java.util.List;

public interface RateLimitStrategy {
    RateLimitResult isValid(RequestMetadata requestMetadata, List<RateLimitRule> rateLimitRules);
}
