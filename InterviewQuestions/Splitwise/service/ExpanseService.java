package InterviewQuestions.Splitwise.service;

import InterviewQuestions.Splitwise.entity.Expense;
import InterviewQuestions.Splitwise.entity.Split;
import InterviewQuestions.Splitwise.entity.Transaction;
import InterviewQuestions.Splitwise.factory.SplitStrategyFactory;
import InterviewQuestions.Splitwise.strategy.SplitStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ExpanseService {
    private final ConcurrentHashMap<String, Expense> expenseMap = new ConcurrentHashMap<>();

    private final SplitStrategyFactory splitStrategyFactory;
    private final BalanceSheetService balanceSheetService;

    public ExpanseService(SplitStrategyFactory splitStrategyFactory, BalanceSheetService balanceSheetService) {
        this.splitStrategyFactory = splitStrategyFactory;
        this.balanceSheetService = balanceSheetService;
    }

    public Expense createExpense(Expense expense) {
        validateExpense(expense);

        SplitStrategy splitStrategy = splitStrategyFactory.getSplitStrategy(expense.getSplitType());
        splitStrategy.split(expense);

        expenseMap.put(expense.getExpenseId(), expense);

        for (Split split : expense.getSplits()) {
            if (!(split.getTo().equals(expense.getPaidBy()))) {
                balanceSheetService.updateBalancesOfUser(expense.getPaidBy(), split.getTo(), split.getSplitAmount());
            }
        }

        return expense;
    }


    private void validateExpense(Expense expense) {
        if (expense.getAmount() == null) {
            throw new RuntimeException("Expense amount cannot be null");
        }

        if (expense.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("expense amount cannot be negative");
        }

        if (expense.getPaidBy() == null) {
            throw new RuntimeException("Paid by cannot be null");
        }

        if (expense.getSplits() == null || expense.getSplits().isEmpty()) {
            throw new RuntimeException("Expense splits cannot be null or empty");
        }

        if (expense.getSplitType() == null) {
            throw new RuntimeException("SplitType Cannot be empty");
        }
    }


}
