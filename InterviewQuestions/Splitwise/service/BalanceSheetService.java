package InterviewQuestions.Splitwise.service;

import InterviewQuestions.Splitwise.entity.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceSheetService {
    private final ConcurrentHashMap<String, BalanceSheet> userBalanceSheetMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

    public void addUser(User user) {
        userMap.put(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return userMap.get(userId);
    }

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

    static class Pair{
        public String userId;
        public BigDecimal amount;

        Pair(String userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

//    public List<Piar>

    public void simplifyDebt() {
//        List<Pair> debitors = new ArrayList<>();
//        List<Pair> creditors = new ArrayList<>();

        PriorityQueue<Pair> debitors = new PriorityQueue<>((p1, p2) -> p2.amount.compareTo(p1.amount));
        PriorityQueue<Pair> creditors = new PriorityQueue<>((p1, p2) -> p1.amount.compareTo(p2.amount));
        for (Map.Entry<String, BalanceSheet> userBalance : userBalanceSheetMap.entrySet()) {
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> user : userBalance.getValue().getUserBalances().entrySet()) {
                total = total.add(user.getValue());
            }

            if (total.compareTo(BigDecimal.ZERO) < 0) {
                debitors.add(new Pair(userBalance.getKey(), total));
            } else if (total.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new Pair(userBalance.getKey(), total));
            }
        }

        userBalanceSheetMap.clear();
        while (!debitors.isEmpty() && !creditors.isEmpty()) {
            Pair debitor = debitors.poll();
            Pair creditor = creditors.poll();

            BigDecimal debtAmount = debitor.amount.abs();
            BigDecimal creditAmount = creditor.amount;

            BigDecimal settledAmount = creditAmount.min(debtAmount);


            BalanceSheet debtorSheet = userBalanceSheetMap.computeIfAbsent(debitor.userId, k -> new BalanceSheet());
            BalanceSheet creditorSheet = userBalanceSheetMap.computeIfAbsent(creditor.userId, k -> new BalanceSheet());
// 1. The Creditor is owed money, so they get a POSITIVE balance against the debtor
            creditorSheet.addToUserBalance(debitor.userId, settledAmount);
// 2. The Debtor owes money, so they get a NEGATIVE balance against the creditor
            debtorSheet.addToUserBalance(creditor.userId, settledAmount.negate());

            System.out.println("Settled amount between:  " + userMap.get(debitor.userId).getUserName() + " " + userMap.get(creditor.userId).getUserName() + " amount: " + settledAmount);
            BigDecimal remainingDebt = debtAmount.subtract(settledAmount);
            BigDecimal remainingCredit = creditAmount.subtract(settledAmount);

            if (remainingCredit.compareTo(BigDecimal.ZERO) > 0) {
                creditors.offer(new Pair(creditor.userId, remainingCredit));
            }

            if (remainingDebt.compareTo(BigDecimal.ZERO) > 0) {
                debitors.offer(new Pair(debitor.userId, remainingDebt));
            }
        }


    }
}

