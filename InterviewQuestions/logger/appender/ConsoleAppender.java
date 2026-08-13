package InterviewQuestions.logger.appender;

import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.formatter.Formatter;
import InterviewQuestions.logger.model.LogMessage;

import java.util.function.Predicate;

public class ConsoleAppender implements LogAppender{
    private final Formatter formatter;
    private Predicate<LogLevel> predicate;

    public ConsoleAppender(Formatter formatter, Predicate<LogLevel> predicate) {
        this.formatter = formatter;
        this.predicate = predicate;
    }

    public void append(LogMessage logMessage) {
        if (!predicate.test(logMessage.getLogLevel())) {
            return;
        }
        System.out.println(formatter.format(logMessage));
    }
}
