package InterviewQuestions.CabBooking.state;



import java.lang.RuntimeException;
import InterviewQuestions.CabBooking.entity.Ride;
import InterviewQuestions.CabBooking.entity.Driver;
import InterviewQuestions.CabBooking.enums.RideStatus;
import InterviewQuestions.CabBooking.state.CompletedState;

public class StartedState implements RideState {

    public void acceptRide(Ride ride, Driver driver){
        throw new RuntimeException("Cannot accept ride which is already started");
    }

    public void startRide(Ride ride){
        throw new RuntimeException("Cannot start ride which is already started");
    }

    public void completeRide(Ride ride){
        ride.setStatus(RideStatus.COMPLETED);
        ride.setState(new CompletedState());
    }

    public void cancelRide(Ride ride){
        throw new RuntimeException("Ride cannot be cancelled once started");
    }
}
