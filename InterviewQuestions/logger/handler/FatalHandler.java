package InterviewQuestions.logger.handler;

import InterviewQuestions.logger.enums.LogLevel;

public class FatalHandler extends LogHandler{

    public boolean canHandle(LogLevel logLevel) {
        return logLevel == LogLevel.FATAL;
    }
}
