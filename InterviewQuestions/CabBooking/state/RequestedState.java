package InterviewQuestions.CabBooking.state;



import java.lang.RuntimeException;
import InterviewQuestions.CabBooking.entity.Rider;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.enums.RideStatus;
import InterviewQuestions.CabBooking.state.CancelledState;
import InterviewQuestions.CabBooking.state.AcceptedState;

import InterviewQuestions.CabBooking.entity.Ride;

public class RequestedState implements RideState {
    public void acceptRide(Ride ride, Driver driver) {
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setDriver(driver);
        ride.setState(new AcceptedState());
    }

    public void startRide(Ride ride){
        throw new RuntimeException("Ride cannot be started in requested state");
    }

    public void completeRide(Ride ride){
        throw new RuntimeException("Ride cannot be completed in requested state");
    }

    public void cancelRide(Ride ride){
        ride.setStatus(RideStatus.CANCELLED);
        ride.setState(new CancelledState());
    }
}
