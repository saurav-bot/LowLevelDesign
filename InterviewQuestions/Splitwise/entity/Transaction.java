package InterviewQuestions.Splitwise.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final BigDecimal amount;
    private final User from;
    private final User to;
    private final LocalDateTime createdAt;
    private final String transactionId;

    public Transaction(BigDecimal amount, User from, User to) {
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.createdAt = LocalDateTime.now();
        this.transactionId = UUID.randomUUID().toString();
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public User getFrom() {
        return this.from;
    }

    public User getTo() {
        return this.to;
    }

    public String getTransactionId() {
        return this.transactionId;
    }
}
