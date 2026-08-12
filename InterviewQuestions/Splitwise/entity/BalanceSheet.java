package InterviewQuestions.Splitwise.entity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceSheet {

    private final Map<String, BigDecimal> userBalances = new ConcurrentHashMap<>();

    public void addToUserBalance(String userId, BigDecimal amount) {
        this.userBalances.merge(userId, amount, BigDecimal::add);
    }

    public Map<String, BigDecimal> getUserBalances() {
        return this.userBalances;
    }

}
