package InterviewQuestions.BookMyShow.entities;

import InterviewQuestions.BookMyShow.enums.BookingStatus;

import java.util.List;
import java.util.UUID;

public class Booking {
    private Theatre theatre;
    private String bookingId;
    private Screen screen;
    private Show show;
    private List<ShowSeat> showSeatList;
    private BookingStatus bookingStatus;

    public Booking(Theatre theatre, Screen screen, Show show, List<ShowSeat> showSeats) {
        this.bookingId = UUID.randomUUID().toString();
        this.theatre = theatre;
        this.screen = screen;
        this.show = show;
        this.showSeatList = showSeats;
        this.bookingStatus = BookingStatus.INITIATED;
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
}
