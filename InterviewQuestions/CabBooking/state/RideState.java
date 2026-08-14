package InterviewQuestions.CabBooking.state;

import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;

public interface RideState {
    void acceptRide(Ride ride, Driver driver);
    void startRide(Ride ride);
    void cancelRide(Ride ride);
    void completeRide(Ride ride);
}
