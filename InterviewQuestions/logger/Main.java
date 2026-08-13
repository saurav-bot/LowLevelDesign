package InterviewQuestions.logger;

import InterviewQuestions.logger.appender.ConsoleAppender;
import InterviewQuestions.logger.appender.FileAppender;
import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.formatter.PlainTextFormatter;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

//        LogHandlerConfiguration.addAppenderForLevel(
//                LogLevel.INFO,
//                new ConsoleAppender(new PlainTextFormatter())
//        );
//
//        LogHandlerConfiguration.addAppenderForLevel(LogLevel.ERROR,
//                new ConsoleAppender(new PlainTextFormatter()));
//
//        LogHandlerConfiguration.addAppenderForLevel(LogLevel.ERROR,
//                new FileAppender(new PlainTextFormatter(), "logs.txt"));

//        logger.addAppenders(
//                new ConsoleAppender(new PlainTextFormatter())
//        );

        logger.addAppenders(
                new ConsoleAppender(new PlainTextFormatter(), level -> level.getRank() >= LogLevel.INFO.getRank()));

        logger.addAppenders(
                new FileAppender(new PlainTextFormatter(), "logs.txt", level->level.getRank() >= LogLevel.ERROR.getRank()));


        logger.info("hellow log info");
        logger.error("Error logs");

        logger.shutdown();
    }
}
