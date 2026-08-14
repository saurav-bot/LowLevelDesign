package InterviewQuestions.CabBooking;

import InterviewQuestions.CabBooking.service.RideService;
import InterviewQuestions.CabBooking.service.FareCalculationService;
import InterviewQuestions.CabBooking.service.RiderService;
import InterviewQuestions.CabBooking.service.DriverService;

import InterviewQuestions.CabBooking.strategy.pricingStrategy.NormalPricingStrategy;


import InterviewQuestions.CabBooking.entity.Rider;
import InterviewQuestions.CabBooking.entity.Ride;

import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.entity.Location;
import InterviewQuestions.CabBooking.entity.Fare;
import InterviewQuestions.CabBooking.strategy.rideMatchingStrategy.NearestRideMatchingStrategy;
import InterviewQuestions.CabBooking.strategy.rideMatchingStrategy.RideMatchingStrategy;

import java.util.List;

public class CabBookingDemo {

    public static void main(String[] args)  throws Exception {
        DriverService driverService = new DriverService();

        RideMatchingStrategy matchingStrategy = new NearestRideMatchingStrategy(driverService);

        RideService rideService = new RideService(matchingStrategy);
        FareCalculationService fareService = new FareCalculationService(new NormalPricingStrategy());
        RiderService riderService = new RiderService();

        Location location = new Location(1,2);
        Rider rider1 = new Rider("saurav", location);

        Driver driver1 = new Driver("prince", new Location(2,3), true);
        Driver driver2 = new Driver("ram", new Location(3,4), true);

        driverService.addDriver(driver2);
        driverService.addDriver(driver1);

        List<Fare> fares = fareService.calculateFares(location, new Location(10,12), rider1);

        for (Fare fare: fares) {
            System.out.println("Fare: " + fare.getVehicleType() + " fee " + fare.getFee());
        }

//        System.out.println("Ride status; " + ride.getStatus());
        Ride ride = rideService.requestRide(fares.get(0));

        System.out.println("Ride status; " + ride.getStatus());

//        ride.acceptRide()
        rideService.startRide(ride);
        System.out.println("Ride status; " + ride.getStatus());

        rideService.completeRide(ride);
        System.out.println("Ride status; " + ride.getStatus());

        rideService.cancelRide(ride);

    }
}
