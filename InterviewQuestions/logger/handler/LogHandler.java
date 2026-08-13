package InterviewQuestions.logger.handler;

import InterviewQuestions.logger.appender.LogAppender;
import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.model.LogMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LogHandler {
    protected LogHandler next;
    protected final List<LogAppender> appenders = new CopyOnWriteArrayList<>();

    public void setNext(LogHandler handler) {
        this.next = handler;
    }

    public void subscribe(LogAppender logAppender) {
        appenders.add(logAppender);
    }

    public void notifyObservers(LogMessage logMessage) {
        for (LogAppender appender: appenders) {
            appender.append(logMessage);
        }
    }

    public void handel(LogMessage logMessage) {
        if (canHandle(logMessage.getLogLevel())) {
            notifyObservers(logMessage);
        } else if (next != null) {
            next.handel(logMessage);
        }
    }

    protected abstract boolean canHandle(LogLevel logLevel);
}
