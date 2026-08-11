package InterviewQuestions.BookMyShow.service;

import InterviewQuestions.BookMyShow.entities.*;
import InterviewQuestions.BookMyShow.enums.BookingStatus;
import InterviewQuestions.BookMyShow.enums.SeatStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BookingService {
    private final ConcurrentHashMap<String, Booking> bookingMap = new ConcurrentHashMap<>();

    public final TheatreService theatreService;

    public BookingService(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    public List<ShowSeat> getAvailableSeatForShow(String showId) {
        List<ShowSeat> seats = theatreService.getAvailableSeat(showId);
        seats = seats.stream().filter(s -> s.getSeatStatus().equals(SeatStatus.AVAILABLE)).toList();

        return seats;
    }

    public Booking book(Theatre theatre, Screen screen, Show show, List<ShowSeat> showSeats){
        Booking booking = new Booking(theatre, screen, show, showSeats);
        bookingMap.put(booking.getBookingId(), booking);
        List<ShowSeat> bookedSeats = new ArrayList<>();

        for (ShowSeat showSeat : showSeats) {
            if (showSeat.updateSeatStatus(SeatStatus.LOCKED, SeatStatus.AVAILABLE))
            {
                bookedSeats.add(showSeat);
            } else {
                for (ShowSeat showSeat1: bookedSeats) {
                    showSeat1.updateSeatStatus(SeatStatus.AVAILABLE, SeatStatus.LOCKED);
                }
                booking.updateBookingStatus(BookingStatus.FAILED);
                return booking;
//                throw new RuntimeException("Could not complete booking, As seat " + showSeat.getSeat().getSeatNumber() + " is already booked");
            }
        }

        for (ShowSeat showSeat : showSeats) {
            if (showSeat.getSeatStatus().equals(SeatStatus.LOCKED)) {
                showSeat.updateSeatStatus(SeatStatus.BOOKED, SeatStatus.LOCKED);
                bookedSeats.add(showSeat);
            }
        }

        booking.updateBookingStatus(BookingStatus.CONFIRMED);
        return booking;

    }

}
