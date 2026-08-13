package InterviewQuestions.logger.model;

import InterviewQuestions.logger.enums.LogLevel;


public class LogMessage {
    private String logMessage;
    private LogLevel logLevel;
    private long timestamp;

    public LogMessage(String logMessage, LogLevel logLevel) {
        this.logMessage = logMessage;
        this.logLevel = logLevel;
        this.timestamp = System.currentTimeMillis();
    }

    public String getLogMessage(){
        return this.logMessage;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public LogLevel getLogLevel() {
        return this.logLevel;
    }

}
