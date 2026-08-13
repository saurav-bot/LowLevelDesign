package InterviewQuestions.logger.appender;

import InterviewQuestions.logger.formatter.Formatter;
import InterviewQuestions.logger.model.LogMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileAppender implements LogAppender{

    private final Formatter formatter;
    private final BufferedWriter writer;

    public FileAppender(Formatter formatter, String filename){
        this.formatter = formatter;

        try {
            this.writer = new BufferedWriter(new FileWriter(filename, true));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open log file ", ex);
        }
    }

    public synchronized void append(LogMessage logMessage) {
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
