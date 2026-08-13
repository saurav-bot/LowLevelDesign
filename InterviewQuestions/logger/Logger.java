package InterviewQuestions.logger;

import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.handler.LogHandler;
import InterviewQuestions.logger.model.LogMessage;

public class Logger {
    private static final Logger INSTANCE = new Logger();

    private final LogHandler handlerChain;

    private Logger() {
        handlerChain = LogHandlerConfiguration.build();
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void log(LogLevel logLevel, String message) {
        LogMessage msg = new LogMessage(message, logLevel);
        handlerChain.handel(msg);
    }

    public void debug(String msg) {
        log(LogLevel.DEBUG, msg);
    }

    public void info(String msg) {
        log(LogLevel.INFO, msg);
    }

    public void warn(String msg) {
        log(LogLevel.WARN, msg);
    }

    public void error(String msg) {
        log(LogLevel.ERROR, msg);
    }

    public void fatal(String msg) {
        log(LogLevel.FATAL, msg);
    }
}
