package InterviewQuestions.Splitwise.factory;

import InterviewQuestions.Splitwise.enums.SplitType;
import InterviewQuestions.Splitwise.strategy.EqualSplitStrategy;
import InterviewQuestions.Splitwise.strategy.ExactSplitStrategy;
import InterviewQuestions.Splitwise.strategy.PercentageSplitStrategy;
import InterviewQuestions.Splitwise.strategy.SplitStrategy;

public class SplitStrategyFactory {
    public SplitStrategy getSplitStrategy(SplitType splitType) {
        return switch(splitType) {
            case EQUAL ->  new EqualSplitStrategy();
            case EXACT ->  new ExactSplitStrategy();
            case PERCENTAGE -> new PercentageSplitStrategy();
        };
    }
}
