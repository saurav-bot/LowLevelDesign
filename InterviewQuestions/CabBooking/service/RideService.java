package InterviewQuestions.CabBooking.service;

import InterviewQuestions.CabBooking.strategy.rideMatchingStrategy.RideMatchingStrategy;
import InterviewQuestions.CabBooking.strategy.rideMatchingStrategy.NearestRideMatchingStrategy;

import InterviewQuestions.CabBooking.entity.Fare;
import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.entity.Location;

import InterviewQuestions.CabBooking.enums.RideStatus;


import java.util.List;
import java.util.ArrayList;

import java.lang.Exception;

public class RideService {
    private final RideMatchingStrategy matchingStrategy;
    private final List<RideStatus> CANCELLABLE_STATUS = new ArrayList<>(List.of(RideStatus.REQUESTED, RideStatus.ACCEPTED));


    public RideService(RideMatchingStrategy riderMatchingStrategy) {
        this.matchingStrategy = riderMatchingStrategy;
    }
    public Ride requestRide(Fare fare) throws Exception {
        Ride ride = new Ride(fare.getPickupLocation(), fare.getDropLocation(), fare.getRider(), null);

        List<Driver> matchDrivers = matchingStrategy.matchDrivers(ride);

        Driver candidate = null;
        for (Driver driver : matchDrivers) {
            if (driver.tryAssign()) {
                candidate = driver;
                break;
            }
        }
        if (candidate == null) {
            throw new Exception("No driver available");
        }
        ride.acceptRide(candidate);

        return ride;
    }

    public void startRide(Ride ride)  throws Exception{
        ride.startRide();
    }

    public void completeRide(Ride ride) throws Exception {
        ride.completeRide();
        ride.getDriver().free();

    }

    public void cancelRide(Ride ride)  throws Exception {
//        if (!CANCELLABLE_STATUS.contains(ride.getStatus())){
//            throw new Exception("ride is not in cancellable status");
//
//        }
//        if (!ride.getStatus().equals(RideStatus.REQUESTED)) {
//            ride.getDriver().free();
//        }
        ride.cancelRide();

    }

}
