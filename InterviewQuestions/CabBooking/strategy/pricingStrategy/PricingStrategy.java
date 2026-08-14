package InterviewQuestions.CabBooking.strategy.pricingStrategy;

import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.enums.VehicleType;

import java.lang.Math;

public interface PricingStrategy {
    double calculatePrice(Location pickupLocation, Location dropLocation, VehicleType vehicleType);
}
