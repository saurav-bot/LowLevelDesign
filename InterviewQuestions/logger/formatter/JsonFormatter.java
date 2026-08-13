package InterviewQuestions.logger.formatter;

import InterviewQuestions.logger.model.LogMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JsonFormatter implements Formatter {
    private final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String format(LogMessage logMessage) {
        String formattedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(logMessage.getTimestamp()),
                ZoneId.systemDefault()
        ).format(FORMATTER);

        return String.format("{\"timestamp\": %s, \"logMessage\" : %s, \"logLevel\" : %s}", formattedTime, logMessage.getLogMessage(), logMessage.getLogLevel());
    }
}