package InterviewQuestions.logger.appender;

import InterviewQuestions.logger.formatter.Formatter;
import InterviewQuestions.logger.model.LogMessage;

public class ConsoleAppender implements LogAppender{
    private final Formatter formatter;

    public ConsoleAppender(Formatter formatter) {
        this.formatter = formatter;
    }

    public void append(LogMessage logMessage) {
        System.out.println(formatter.format(logMessage));
    }
}
