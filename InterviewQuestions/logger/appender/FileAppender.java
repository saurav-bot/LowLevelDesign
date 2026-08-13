package InterviewQuestions.logger.appender;

import InterviewQuestions.logger.enums.LogLevel;
import InterviewQuestions.logger.formatter.Formatter;
import InterviewQuestions.logger.model.LogMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Predicate;

public class FileAppender implements LogAppender{

    private final Formatter formatter;
    private final BufferedWriter writer;
    private Predicate<LogLevel> predicate;

    public FileAppender(Formatter formatter, String filename, Predicate<LogLevel> predicate){
        this.formatter = formatter;
        this.predicate = predicate;

        try {
            this.writer = new BufferedWriter(new FileWriter(filename, true));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open log file ", ex);
        }
    }

    public synchronized void append(LogMessage logMessage) {

        if (!predicate.test(logMessage.getLogLevel())) {
            return;
        }
        try {
            writer.write(formatter.format(logMessage));
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void close() {
        try {
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
