package InterviewQuestions.RateLimiter.models;

public class MatchedRule<T> {
    T limiter;
    RateLimitRule rule;

    public MatchedRule(T limiter, RateLimitRule rule) {
        this.limiter = limiter;
        this.rule = rule;
    }

    public T getLimiter() {
        return limiter;
    }

    public RateLimitRule getRule(){
        return rule;
    }


}
