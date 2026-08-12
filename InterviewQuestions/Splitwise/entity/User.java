package InterviewQuestions.Splitwise.entity;

import java.util.UUID;

public class User {
    private final String userId;
    private final String userName;

    public User(String userName) {
        this.userName = userName;
        this.userId = UUID.randomUUID().toString();
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserId() {
        return this.userId;
    }
}
