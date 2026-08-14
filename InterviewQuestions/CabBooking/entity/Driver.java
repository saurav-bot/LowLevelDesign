package InterviewQuestions.CabBooking.entity;

import InterviewQuestions.CabBooking.entity.Location;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Driver {
    private String driverId;
    private String name ;
    private AtomicBoolean available;
    private Location location;

    public Driver(String name, Location location, boolean available){
        this.name = name;
        this.location = location;
        this.available = new AtomicBoolean(available);
        this.driverId = UUID.randomUUID().toString();
    }

    public String getName(){
        return name;
    }

    public boolean isAvailable(){
        return this.available.get();
    }

    public boolean tryAssign(){
        return available.compareAndSet(true, false);
    }

    public boolean free() {
        return available.compareAndSet(false, true);

    }

    public String getDriverId() {
        return driverId;
    }

    public Location getLocation () {
        return location;
    }
}
