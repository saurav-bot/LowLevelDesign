package InterviewQuestions.Splitwise;

import InterviewQuestions.Splitwise.entity.*;
import InterviewQuestions.Splitwise.enums.SplitType;
import InterviewQuestions.Splitwise.factory.SplitStrategyFactory;
import InterviewQuestions.Splitwise.service.BalanceSheetService;
import InterviewQuestions.Splitwise.service.ExpanseService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplitWiseDemo {
    public static void main(String[] args) {
        User user = new User("saurav");
        User user1 = new User("prince");
        User user2 = new User("gaurav");


        List<User> users = new ArrayList<>(List.of(user, user1, user2));

        BalanceSheetService balanceSheetService = new BalanceSheetService();

        balanceSheetService.addUser(user);
        balanceSheetService.addUser(user1);
        balanceSheetService.addUser(user2);

        ExpanseService expanseService = new ExpanseService(new SplitStrategyFactory(), balanceSheetService);

        List<Split> splitList = List.of(new Split(BigDecimal.valueOf(10), user, 0), new Split(BigDecimal.valueOf(20), user1, 0));
        Expense exact = new Expense(splitList, user, BigDecimal.valueOf(30), SplitType.EXACT);

        expanseService.createExpense(exact);
        checkBalanceOfAllUser(users, balanceSheetService);

//        Transaction transaction = new Transaction(BigDecimal.valueOf(20), user1, user);
//        balanceSheetService.recordTransactionAgainstExpense(transaction);


        List<Split> splitList2 = List.of(new Split(BigDecimal.valueOf(10), user1, 0), new Split(BigDecimal.valueOf(20), user2, 0), new Split(null, user, 0 ));
        Expense exact2 = new Expense(splitList2, user1, BigDecimal.valueOf(7), SplitType.EQUAL);

        expanseService.createExpense(exact2);
        checkBalanceOfAllUser(users, balanceSheetService);


        List<Split> splitList3 = List.of(new Split(BigDecimal.valueOf(10), user1, 40), new Split(BigDecimal.valueOf(20), user2, 30), new Split(null, user, 30));
        Expense percentage = new Expense(splitList3, user1, BigDecimal.valueOf(37), SplitType.PERCENTAGE);

        expanseService.createExpense(percentage);
        checkBalanceOfAllUser(users, balanceSheetService);

        balanceSheetService.simplifyDebt();

        System.out.println("\nAfter simplifying transaction\n");
        checkBalanceOfAllUser(users, balanceSheetService);



    }

    private static void checkBalanceOfAllUser(List<User> users, BalanceSheetService balanceSheetService) {
        System.out.println("############################### PRINTING ALL BALANCE #########################################");
        for (User user: users) {
            AggregateBalanceSheetView balanceSheet = balanceSheetService.getBalanceSheetOfUser(user);
            System.out.println("\n BalanceSheet of " + user.getUserName() + " to pay " + balanceSheet.getTotalOwed() + " to receive " + balanceSheet.getTotalReceivable());
            System.out.println("Pat To Users");
            for (Map.Entry<String, BigDecimal> balance : balanceSheet.getToPayUserWise().entrySet()) {
                System.out.println(balanceSheetService.getUser(balance.getKey()).getUserName() + " -> " + balance.getValue());
            }

            System.out.println("Receive From Users");
            for (Map.Entry<String, BigDecimal> balance : balanceSheet.getToReceiveUserWise().entrySet()) {
                System.out.println(balanceSheetService.getUser(balance.getKey()).getUserName() + " -> " + balance.getValue() + "\n");
            }
        }
    }
}
