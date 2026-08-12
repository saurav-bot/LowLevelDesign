package InterviewQuestions.Splitwise.entity;

import java.beans.BeanInfo;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AggregateBalanceSheetView {

    private BigDecimal totalOwed;
    private BigDecimal totalReceivable;
    private Map<String, BigDecimal> toPayUserWise;
    private Map<String, BigDecimal> toReceiveUserWise;

    public AggregateBalanceSheetView() {
        totalOwed = BigDecimal.ZERO;
        totalReceivable = BigDecimal.ZERO;
        toPayUserWise = new HashMap<>();
        toReceiveUserWise = new HashMap<>();
    }

    public BigDecimal getTotalOwed() {
        return this.totalOwed;
    }

    public BigDecimal getTotalReceivable(){
        return this.totalReceivable;
    }

    public void addToOwed(BigDecimal other) {
        this.totalOwed = this.totalOwed.add(other.abs()) ;
    }

    public void addToReceivable(BigDecimal other) {
        this.totalReceivable = this.totalReceivable.add(other.abs());
    }

    public Map<String, BigDecimal> getToPayUserWise() {
        return this.toPayUserWise;
    }

    public Map<String, BigDecimal> getToReceiveUserWise() {
        return this.toReceiveUserWise;
    }

    public void setToPayUserWise(Map<String, BigDecimal> toPayUserWise) {
        this.toPayUserWise = toPayUserWise;
    }

    public void setToReceiveUserWise(Map<String, BigDecimal> toReceiveUserWise) {
        this.toReceiveUserWise = toReceiveUserWise;
    }
}
