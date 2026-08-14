package InterviewQuestions.CabBooking.state;


import java.lang.RuntimeException;
import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;


public class CancelledState implements RideState {
    public void acceptRide(Ride rider, Driver driver){
        throw new RuntimeException("Cannot accept cancelled ride");
    }

    public void startRide(Ride ride){
        throw new RuntimeException("Cannot start cancelled ride");
    }

    public void completeRide(Ride ride){
        throw new RuntimeException("Cannot complete cancelled ride");
    }

    public void cancelRide(Ride ride){
        throw new RuntimeException("Cannot cancle already cancelled ride");
    }
}
