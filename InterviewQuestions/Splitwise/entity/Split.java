package InterviewQuestions.Splitwise.entity;

import java.math.BigDecimal;

public class Split {
    private BigDecimal splitAmount;
    private final User to;
    private final int percentage;

    public Split(BigDecimal splitAmount, User to, int percentage) {
        this.splitAmount = splitAmount;
        this.percentage = percentage;
        this.to = to;
    }

    public BigDecimal getSplitAmount() {
        return this.splitAmount;
    }

    public void setSplitAmount(BigDecimal splitAmount) {
        this.splitAmount = splitAmount;
    }

    public User getTo() {
        return to;
    }

    public int getPercentage() {
        return this.percentage;
    }
}
