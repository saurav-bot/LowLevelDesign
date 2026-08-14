package InterviewQuestions.CabBooking.strategy.rideMatchingStrategy;

import InterviewQuestions.CabBooking.service.DriverService;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.entity.Ride;

import java.util.List;

public class NearestRideMatchingStrategy implements RideMatchingStrategy {
    private final DriverService driverService;
    private final int SEARCH_RADIUS_KM = 8;

    public NearestRideMatchingStrategy(DriverService driverService) {
        this.driverService = driverService;
    }

    public List<Driver> matchDrivers(Ride ride) {
        List<Driver> drivers = driverService.getAllNearestDriver(ride.getPickupLocation(), SEARCH_RADIUS_KM);

        return drivers;
    }
}
