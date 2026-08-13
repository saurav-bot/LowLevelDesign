package InterviewQuestions.logger;

import InterviewQuestions.logger.appender.LogAppender;
import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.model.LogMessage;

import java.util.List;
import java.util.concurrent.*;

public class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final ArrayBlockingQueue<LogMessage> queue =  new ArrayBlockingQueue<>(100000);

    private static final List<LogAppender> appenders = new CopyOnWriteArrayList<>();
    private LogLevel SystemLogLevel = LogLevel.INFO;
    private final ExecutorService executorService;

    private Logger() {
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("Async-Logger");
            return thread;
        };

        executorService = Executors.newSingleThreadExecutor(threadFactory);
        Future<?> future = executorService.submit(this::handle);

    }

    public void addAppenders(LogAppender appender) {
        appenders.add(appender);
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void log(LogLevel logLevel, String message) {
        if (logLevel.getRank() >= SystemLogLevel.getRank()) {
            LogMessage msg = new LogMessage(message, logLevel);
            queue.offer(msg);
        }
    }

    public void handle() {
        while (true) {
            try {
                LogMessage logMessage = queue.take();
                for (LogAppender appender : appenders) {
                    appender.append(logMessage);
                }
//                handlerChain.handel(logMessage);
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

    public void shutdown() {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException ex) {

            }

            executorService.shutdown();
        }
    }
}
