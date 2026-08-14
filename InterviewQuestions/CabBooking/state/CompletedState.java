package InterviewQuestions.CabBooking.state;

import java.lang.RuntimeException;
import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;

public class CompletedState implements RideState{

    public void acceptRide(Ride rider, Driver driver){
        throw new RuntimeException("Cannot accept completed ride");
    }

    public void startRide(Ride ride){
        throw new RuntimeException("Cannot start completed ride");
    }

    public void completeRide(Ride ride){
        throw new RuntimeException("Cannot complete completed ride");
    }

    public void cancelRide(Ride ride){
        throw new RuntimeException("Cannot cancle already completed ride");
    }
}
