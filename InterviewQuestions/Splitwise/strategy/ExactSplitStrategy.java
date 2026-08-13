package InterviewQuestions.Splitwise.strategy;

import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;

import java.math.BigDecimal;

public class ExactSplitStrategy implements SplitStrategy{

    public void split(Expense expense) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Split split : expense.getSplits()) {
            totalAmount = totalAmount.add(split.getSplitAmount());
        }

        if (totalAmount.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("Split amount does not match with expense amount");
        }
    }
}
