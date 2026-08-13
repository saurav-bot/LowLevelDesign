package InterviewQuestions.logger.enums;

public enum LogLevel {
    DEBUG(10),
    INFO(20),
    WARN(30),
    ERROR(40),
    FATAL(50);

    private final int rank;

     LogLevel(int rank) {
        this.rank = rank;
    }

    public int getRank() {
         return this.rank;
    }
}
