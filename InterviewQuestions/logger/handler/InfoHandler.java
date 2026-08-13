package InterviewQuestions.logger.handler;

import InterviewQuestions.logger.enums.LogLevel;

public class InfoHandler extends LogHandler{

    public boolean canHandle(LogLevel logLevel) {
        return logLevel.equals(LogLevel.INFO);
    }
}
