package InterviewQuestions.logger;

import InterviewQuestions.logger.appender.LogAppender;
import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.handler.*;

public class LogHandlerConfiguration {

    private static final LogHandler debug = new DebugHandler();
    private static final LogHandler info = new InfoHandler();
    private static final LogHandler warn = new WarnHandler();
    private static final LogHandler error = new ErrorHandler();
    private static final LogHandler fatal = new FatalHandler();


    public static LogHandler build() {
        debug.setNext(info);
        info.setNext(warn);
        warn.setNext(error);
        error.setNext(fatal);

        return debug;
    }

    public static void addAppenderForLevel(LogLevel logLevel, LogAppender logAppender) {
        switch (logLevel) {
            case DEBUG -> debug.subscribe(logAppender);
            case INFO -> info.subscribe(logAppender);
            case WARN -> warn.subscribe(logAppender);
            case ERROR -> error.subscribe(logAppender);
            case FATAL -> fatal.subscribe(logAppender);
        }
    }
}
