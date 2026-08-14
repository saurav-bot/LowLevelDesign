package InterviewQuestions.CabBooking.state;

import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.enums.RideStatus;

import java.lang.RuntimeException;

public class AcceptedState implements RideState {

    public void acceptRide(Ride ride, Driver driver){
        throw new RuntimeException("Ride cannot be accepted in accepted state");
    }

    public void startRide(Ride ride) {
        ride.setStatus(RideStatus.STARTED);
        ride.setState(new StartedState());
    }

    public void completeRide(Ride ride){
        throw new RuntimeException("Ride cannot be completed in accepted state");
    }

    public void cancelRide(Ride ride){
        ride.setStatus(RideStatus.CANCELLED);
        ride.getDriver().free();
        ride.setState(new CancelledState());
    }
}
