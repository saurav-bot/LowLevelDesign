package InterviewQuestions.Splitwise.service;

import InterviewQuestions.Splitwise.entity.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceSheetService {
    private final ConcurrentHashMap<String, BalanceSheet> userBalanceSheetMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();


    public BalanceSheetService() {
    }

    public void updateBalancesOfUser(User from, User to, BigDecimal amount) {
        BalanceSheet fromBalanceSheet = userBalanceSheetMap.computeIfAbsent(from.getUserId(), k -> new BalanceSheet());
        BalanceSheet toBalanceSheet = userBalanceSheetMap.computeIfAbsent(to.getUserId(), k -> new BalanceSheet());

        fromBalanceSheet.addToUserBalance(to.getUserId(), amount);
        toBalanceSheet.addToUserBalance(from.getUserId(), amount.negate());
    }

    public AggregateBalanceSheetView getBalanceSheetOfUser(User user) {
        AggregateBalanceSheetView aggregateBalanceSheetView = new AggregateBalanceSheetView();
        Map<String, BigDecimal> toPayUserWise = new HashMap<>();
        Map<String, BigDecimal> toReceiveUserWise = new HashMap<>();

        for (Map.Entry<String, BigDecimal> balance : userBalanceSheetMap.getOrDefault(user.getUserId(), new BalanceSheet()).getUserBalances().entrySet()) {
            if (balance.getValue().compareTo(BigDecimal.ZERO) < 0) {
                aggregateBalanceSheetView.addToOwed(balance.getValue());
                toPayUserWise.put(balance.getKey(), balance.getValue().negate());
            } else {
                aggregateBalanceSheetView.addToReceivable(balance.getValue());
                toReceiveUserWise.put(balance.getKey(), balance.getValue());
            }
        }

        aggregateBalanceSheetView.setToPayUserWise(toPayUserWise);
        aggregateBalanceSheetView.setToReceiveUserWise(toReceiveUserWise);

        return aggregateBalanceSheetView;
    }

    public void recordTransactionAgainstExpense(Transaction transaction) {
        validateTransaction(transaction);
        transactionMap.put(transaction.getTransactionId(), transaction);
        updateBalancesOfUser(transaction.getFrom(), transaction.getTo(), transaction.getAmount());
    }


    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount cannot be null or less then zero");
        }

        if (transaction.getFrom() == null) {
            throw new RuntimeException("From cannot be null");
        }

        if (transaction.getTo() == null) {
            throw new RuntimeException("To cannot be null");
        }

    }
}

