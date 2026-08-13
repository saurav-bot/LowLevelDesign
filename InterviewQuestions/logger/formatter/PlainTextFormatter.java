package InterviewQuestions.logger.formatter;

import InterviewQuestions.logger.model.LogMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PlainTextFormatter implements Formatter{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String format(LogMessage logMessage) {
        String formattedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(logMessage.getTimestamp()), ZoneId.systemDefault())
                .format(FORMATTER);

        return String.format("%s [%s] %s", formattedTime, logMessage.getLogLevel(), logMessage.getLogMessage());
    }
}
