package InterviewQuestions.BookMyShow.entities;

import java.util.List;
import java.util.UUID;

public class Screen {
    private final String screenId;
    private final List<Seat> seats;

    public Screen(List<Seat> seats) {
        this.screenId = UUID.randomUUID().toString();
        this.seats = seats;
    }

    public String getScreenId(){
        return this.screenId;
    }

    public List<Seat> getSeats() {
        return this.seats;
    }

}
