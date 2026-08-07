package InterviewQuestions.RateLimiter;

public interface RateLimitStrategy {
    boolean isValid(String userId);
}
