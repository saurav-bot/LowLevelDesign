package InterviewQuestions.CabBooking.strategy.pricingStrategy;

import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.enums.VehicleType;

public class NormalPricingStrategy implements PricingStrategy{

    public double calculatePrice(Location pickupLocation, Location dropLocation, VehicleType vehicleType){
        double distance = pickupLocation.distanceTo(dropLocation);
        double finalFee = vehicleType.getBaseFare() + vehicleType.getPerKmRate()*distance;

        return finalFee;

    }
}
