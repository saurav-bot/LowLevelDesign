package InterviewQuestions.Splitwise.strategy;

import InterviewQuestions.Splitwise.entity.Expense;

public interface SplitStrategy {
    void verifySplit(Expense expense);
}
