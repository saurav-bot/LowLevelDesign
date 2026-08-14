package InterviewQuestions.CabBooking.entity;

import java.util.UUID;
import InterviewQuestions.CabBooking.entity.Location;

public class Rider {
    private String name;
    private Location location;
    private String riderId;

    public Rider(String name, Location location){
        this.name = name;
        this.location = location;
        this.riderId = UUID.randomUUID().toString();
    }

    public String getRiderId() {
        return riderId;
    }

    public Location getLocation() {
        return location;
    }
}

