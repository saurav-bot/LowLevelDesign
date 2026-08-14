package InterviewQuestions.CabBooking.strategy.rideMatchingStrategy;

import java.util.List;
import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;


public interface RideMatchingStrategy {
    List<Driver> matchDrivers(Ride ride);
}
