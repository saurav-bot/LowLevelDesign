package InterviewQuestions.logger.handler;

import InterviewQuestions.logger.enums.LogLevel;

public class DebugHandler extends LogHandler{
    public boolean canHandle(LogLevel logLevel) {
        return logLevel == LogLevel.DEBUG;
    }
}
