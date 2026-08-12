package InterviewQuestions.Splitwise.entity;

import java.math.BigDecimal;

public class Split {
    private final BigDecimal splitAmount;
    private final User from;
    private final User to;
    private final int percentage;

    public Split(BigDecimal splitAmount, User from, User to, int percentage) {
        this.splitAmount = splitAmount;
        this.from = from;
        this.percentage = percentage;
        this.to = to;
    }

    public BigDecimal getSplitAmount() {
        return this.splitAmount;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public int getPercentage() {
        return this.percentage;
    }
}
