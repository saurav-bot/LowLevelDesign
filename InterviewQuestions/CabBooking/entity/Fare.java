package InterviewQuestions.CabBooking.entity;

import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.entity.Rider;
import InterviewQuestions.CabBooking.enums.VehicleType;

import java.time.Instant;

public class Fare {
    private VehicleType vehicleType;
    private double fee;
    private Location pickupLocation;
    private Location dropLocation;
    private Rider rider;
    private Instant expiresAt;


    public Fare(VehicleType vehicleType, double fee, Location pickupLocation, Location dropLocation, Rider rider, Instant expiresAt){
        this.vehicleType = vehicleType;
        this.fee = fee;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.rider = rider;
        this.expiresAt = expiresAt;
    }


    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public double getFee() {
        return fee;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public Rider getRider() {
        return rider;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

}
