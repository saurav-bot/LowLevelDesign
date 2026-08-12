package InterviewQuestions.Splitwise.entity;

import InterviewQuestions.Splitwise.enums.ExpenseStatus;
import InterviewQuestions.Splitwise.enums.SplitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Expense {
    private final String expenseId;
    private final List<Split> splits;
    private final User paidBy;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;
    private final SplitType splitType;

    public Expense(List<Split> splits, User paidBy, BigDecimal amount, SplitType splitType) {
        this.splits = splits;
        this.paidBy = paidBy;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.expenseId = UUID.randomUUID().toString();
        this.splitType = splitType;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public User getPaidBy() {
        return this.paidBy;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public SplitType getSplitType() {
        return this.splitType;
    }

    public String getExpenseId() {
        return this.expenseId;
    }

}
