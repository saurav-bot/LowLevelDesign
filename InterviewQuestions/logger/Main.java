package InterviewQuestions.logger;

import InterviewQuestions.logger.appender.ConsoleAppender;
import InterviewQuestions.logger.appender.FileAppender;
import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.formatter.PlainTextFormatter;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

        LogHandlerConfiguration.addAppenderForLevel(
                LogLevel.INFO,
                new ConsoleAppender(new PlainTextFormatter())
        );

        LogHandlerConfiguration.addAppenderForLevel(LogLevel.ERROR,
                new ConsoleAppender(new PlainTextFormatter()));

        LogHandlerConfiguration.addAppenderForLevel(LogLevel.ERROR,
                new FileAppender(new PlainTextFormatter(), "logs.txt"));


        logger.info("hellow log info");
        logger.error("Error logs");
    }
}
