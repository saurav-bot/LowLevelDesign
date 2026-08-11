package InterviewQuestions.BookMyShow.entities;

import InterviewQuestions.BookMyShow.enums.BookingStatus;

import java.util.List;
import java.util.UUID;

public class Booking {
    private final User user;
    private final Theatre theatre;
    private final String bookingId;
    private final Screen screen;
    private final Show show;
    private final List<ShowSeat> showSeatList;
    private BookingStatus bookingStatus;

    public Booking(Theatre theatre, Screen screen, Show show, List<ShowSeat> showSeats, User user) {
        this.bookingId = UUID.randomUUID().toString();
        this.theatre = theatre;
        this.screen = screen;
        this.show = show;
        this.showSeatList = showSeats;
        this.bookingStatus = BookingStatus.INITIATED;
        this.user = user;
    }

    public BookingStatus getBookingStatus() {
        return this.bookingStatus;
    }

    public void updateBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public List<ShowSeat> getShowSeatList() {
        return this.showSeatList;
    }
}
