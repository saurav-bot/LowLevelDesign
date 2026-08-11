package InterviewQuestions.BookMyShow.entities;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Theatre {
    private final String theatreId;
    private Address address;
    private String name;
    private List<Screen> screens;

    public Theatre(Address address, String name, List<Screen> screens) {
        this.address = address;
        this.name = name;
        this.theatreId = UUID.randomUUID().toString();
        this.screens = screens;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public Address getAddress() {
        return address;
    }

    public  String getName() {
        return name;
    }

    public List<Screen> getScreens() {
        return screens;
    }
}

