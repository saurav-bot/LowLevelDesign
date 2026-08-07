package InterviewQuestions.RateLimiter;

public class RateLimitResult {
    private boolean allowed=true;
    private long tokensRemaining;
    private long retryAfterSeconds;
    private String violatedRuleId;

//    public RateLimitResult(boolean b, String s) {
//    }
    public RateLimitResult(boolean allowed, long retryAfterSeconds, String violatedRuleId) {
        this.allowed = allowed;
        this.retryAfterSeconds = retryAfterSeconds;
        this.violatedRuleId = violatedRuleId;
    }

    public RateLimitResult() {

    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public String getViolatedRuleId() {
        return violatedRuleId;
    }
}
