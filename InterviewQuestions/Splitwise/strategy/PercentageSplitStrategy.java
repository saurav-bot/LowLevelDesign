package InterviewQuestions.Splitwise.strategy;

import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;

import java.math.BigDecimal;

public class PercentageSplitStrategy implements SplitStrategy{
    public void verifySplit(Expense expense) {
        int totalPercentage = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Split split: expense.getSplits()) {
            totalPercentage += split.getPercentage();
            totalAmount = split.getSplitAmount().add(totalAmount);
        }

        if (totalPercentage != 100) {
            throw new RuntimeException("Total Percentage is not equal to 100");
        }

        if (totalAmount.compareTo(expense.getAmount()) > 0) {
            throw new RuntimeException("Total amount across split are greater then expense amount");
        }
    }
}
