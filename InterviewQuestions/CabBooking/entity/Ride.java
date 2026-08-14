package InterviewQuestions.CabBooking.entity;

import InterviewQuestions.CabBooking.entity.Rider;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.enums.RideStatus;
import InterviewQuestions.CabBooking.state.RideState;
import InterviewQuestions.CabBooking.state.RequestedState;

import  java.util.UUID;
import java.time.Instant;


public class Ride {
    private String rideId;
    private Location pickupLocation;
    private Location dropLocation;
    private Rider rider;
    private Driver driver;
    private Instant requestedAt;
    private RideStatus status;

    private RideState state = new RequestedState();


    public Ride(Location pickupLocation, Location DropLocation, Rider rider, Driver driver){
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.rider = rider;
        this.driver = driver;
        this.requestedAt = Instant.now();
        this.rideId = UUID.randomUUID().toString();
        this.status = RideStatus.REQUESTED;
    }


    public Driver getDriver(){
        return driver;
    }

    public void setStatus(RideStatus status){
        this.status = status;
    }


    public RideStatus getStatus() {
        return status;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setState(RideState state) {
        this.state = state;
    }

    public void setDriver(Driver driver){
        this.driver = driver;
    }

    public void acceptRide(Driver driver) {
        state.acceptRide(this, driver);
    }
    public void startRide(){state.startRide(this);
    }
    public void completeRide(){state.completeRide(this);
    }
    public void cancelRide() {state.cancelRide(this);
    }
}
