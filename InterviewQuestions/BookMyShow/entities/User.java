package InterviewQuestions.BookMyShow.entities;

import java.util.UUID;

public class User {
    private final String name;
    private final String userId;

    public User(String name) {
        this.name = name;
        this.userId = UUID.randomUUID().toString();
    }

    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.userId;
    }

}
