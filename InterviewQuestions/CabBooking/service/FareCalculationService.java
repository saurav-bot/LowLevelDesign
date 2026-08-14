package InterviewQuestions.CabBooking.service;

import java.util.List;
import InterviewQuestions.CabBooking.strategy.pricingStrategy.PricingStrategy;
import InterviewQuestions.CabBooking.entity.Fare;
import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.entity.Rider;

import InterviewQuestions.CabBooking.enums.VehicleType;



import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;


public class FareCalculationService {
    private final PricingStrategy pricingStrategy;
    private final Duration VALID_FOR = Duration.ofSeconds(100);

    public FareCalculationService(PricingStrategy pricingStrategy){
        this.pricingStrategy = pricingStrategy;
    }

    public List<Fare> calculateFares(Location pickupLocation, Location dropLocation, Rider rider){
        List<Fare> fares = new ArrayList<>();
        for (VehicleType type : VehicleType.values()){
            double price = pricingStrategy.calculatePrice(pickupLocation, dropLocation, type);

            Fare fare = new Fare(type, price, pickupLocation, dropLocation, rider, Instant.now().plus(VALID_FOR));

            fares.add(fare);

        }

        return fares;
    }
}
