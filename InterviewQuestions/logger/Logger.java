package InterviewQuestions.logger;

import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.handler.LogHandler;
import InterviewQuestions.logger.model.LogMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final ArrayBlockingQueue<LogMessage> queue =  new ArrayBlockingQueue<>(100000);

    private final LogHandler handlerChain;

    private Logger() {
        handlerChain = LogHandlerConfiguration.build();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(this::handle);

    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void log(LogLevel logLevel, String message) {
        LogMessage msg = new LogMessage(message, logLevel);
        queue.offer(msg);
    }

    public void handle() {
        while (true) {
            try {
                LogMessage logMessage = queue.take();
                handlerChain.handel(logMessage);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println("Error occurred while waiting: " + ex.getMessage());
                break;
            }

        }
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
