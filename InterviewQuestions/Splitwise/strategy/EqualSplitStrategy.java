package InterviewQuestions.Splitwise.strategy;


import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EqualSplitStrategy implements SplitStrategy {

    public void verifySplit(Expense expense){
        int n = expense.getSplits().size();
        BigDecimal share = expense.getAmount().divide(BigDecimal.valueOf(n), RoundingMode.valueOf(2));

        BigDecimal errorMargin = BigDecimal.valueOf(0.11);

        BigDecimal totalAmountOfSplit = BigDecimal.ZERO;
        for (Split split : expense.getSplits()) {
            if (share.subtract(split.getSplitAmount()).compareTo(errorMargin) > 0) {
                throw new RuntimeException("Equal split is violated");
            }
            totalAmountOfSplit = totalAmountOfSplit.add(split.getSplitAmount());
        }

        if (totalAmountOfSplit.subtract(expense.getAmount()).compareTo(errorMargin) > 0 ) {
            throw new RuntimeException("Equal split is violated");
        }
    }
}
