package InterviewQuestions.RateLimiter;

public class RateLimitRule {
    private String ruleId;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public String getTargetPattern() {
        return targetPattern;
    }

    public void setTargetPattern(String targetPattern) {
        this.targetPattern = targetPattern;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public double getRefillRatePerSec() {
        return refillRatePerSec;
    }

    public void setRefillRatePerSec(double refillRatePerSec) {
        this.refillRatePerSec = refillRatePerSec;
    }

    private RuleType ruleType;
    private String targetPattern;
    private long capacity;
    private double refillRatePerSec;

    public RateLimitRule(String ruleId, RuleType ruleType, String targetPattern, long capacity, double refillRatePerSec) {
        this.ruleId = ruleId;
        this.ruleType = ruleType;
        this.targetPattern = targetPattern;
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
    }
}
