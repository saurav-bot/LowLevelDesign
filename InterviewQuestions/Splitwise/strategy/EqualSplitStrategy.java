package InterviewQuestions.Splitwise.strategy;


import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EqualSplitStrategy implements SplitStrategy {

    public void split(Expense expense){
        int n = expense.getSplits().size();
        BigDecimal share = expense.getAmount().divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmountOfSplit = BigDecimal.ZERO;
        for (Split split : expense.getSplits()) {
            split.setSplitAmount(share);
            totalAmountOfSplit = totalAmountOfSplit.add(share);
            System.out.println(split.getTo().getUserName() + " split amount: " + split.getSplitAmount() + " percentage: "  + split.getPercentage() + " total amount " + expense.getAmount());

        }

        BigDecimal difference = expense.getAmount().subtract(totalAmountOfSplit);
        System.out.println("difference: " + difference);
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            Split firstSplit = expense.getSplits().getLast();
            firstSplit.setSplitAmount(firstSplit.getSplitAmount().add(difference));
        }
    }
}
