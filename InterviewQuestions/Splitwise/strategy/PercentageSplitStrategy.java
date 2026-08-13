package InterviewQuestions.Splitwise.strategy;

import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageSplitStrategy implements SplitStrategy{
    public void split(Expense expense) {
        int totalPercentage = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Split split: expense.getSplits()) {
            totalPercentage += split.getPercentage();
        }

        if (totalPercentage != 100) {
            throw new RuntimeException("Total Percentage is not equal to 100");
        }

        for (Split split : expense.getSplits()) {
            split.setSplitAmount(
                    expense.getAmount()
                            .multiply(BigDecimal.valueOf(split.getPercentage()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            System.out.println(split.getTo().getUserName() + " split amount: " + split.getSplitAmount() + " percentage: "  + split.getPercentage() + " total amount " + expense.getAmount());
            totalAmount = totalAmount.add(split.getSplitAmount());
        }

        // We are doing this because sometime accurate division is not possible so during rounding we loose or gain some extra
        BigDecimal difference = expense.getAmount().subtract(totalAmount);
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            Split firstsplit = expense.getSplits().getFirst();

            firstsplit.setSplitAmount(firstsplit.getSplitAmount().add(difference));
        }


    }
}
