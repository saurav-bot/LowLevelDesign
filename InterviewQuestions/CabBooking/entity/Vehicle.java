package InterviewQuestions.CabBooking.entity;

import java.util.UUID;
import InterviewQuestions.CabBooking.enums.VehicleType;

public class Vehicle {
    private String vehicleId;
    private String registrationNumber;
    private String vehicleNumber;
    private VehicleType vehicleType;

    public Vehicle(String vehicleNumber, String registerationNumber, VehicleType vehicleType ) {
        this.vehicleNumber = vehicleNumber;
        this.registrationNumber = registerationNumber;
        this.vehicleType = vehicleType;
        this.vehicleId = UUID.randomUUID().toString();
    }
}
