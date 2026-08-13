package InterviewQuestions.logger.appender;

import InterviewQuestions.logger.model.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
}
