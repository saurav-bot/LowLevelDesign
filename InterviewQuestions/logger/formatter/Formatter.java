package InterviewQuestions.logger.formatter;

import InterviewQuestions.logger.model.LogMessage;

public interface Formatter {
    String format(LogMessage logMessage);
}
