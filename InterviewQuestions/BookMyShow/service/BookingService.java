package InterviewQuestions.BookMyShow.service;

import InterviewQuestions.BookMyShow.entities.*;
import InterviewQuestions.BookMyShow.enums.BookingStatus;
import InterviewQuestions.BookMyShow.enums.SeatStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BookingService {
    private final ConcurrentHashMap<String, Booking> bookingMap = new ConcurrentHashMap<>();

    private final ShowService showService;


    public BookingService(ShowService showService) {
        this.showService = showService;
    }

    public List<ShowSeat> getAvailableSeatForShow(String showId) {
        List<ShowSeat> seats = showService.getAllSeatOfShow(showId);
        seats = seats.stream().filter(s -> s.getSeatStatus().equals(SeatStatus.AVAILABLE)).toList();

        return seats;
    }

    public void initiateBooking(Booking booking, List<ShowSeat> showSeats) {
        List<ShowSeat> bookedSeats = new ArrayList<>();

        for (ShowSeat showSeat : showSeats) {
            if (showSeat.lockSeat())
            {
                bookedSeats.add(showSeat);
            } else {
                for (ShowSeat bookedSeat: bookedSeats) {
                    bookedSeat.releaseSeat();
                }
                booking.updateBookingStatus(BookingStatus.FAILED);
                return ;
            }
        }

//        return booking;
    }

    public void confirmBooking(Booking booking) {
        for (ShowSeat showSeat : booking.getShowSeatList()) {
            showSeat.confirmSeat();
        }
    }


    public Booking book(Show show, List<ShowSeat> showSeats, User user){
        Booking booking = new Booking(show.getTheatre(), show.getShowScreen(), show, showSeats, user);
        bookingMap.put(booking.getBookingId(), booking);

        initiateBooking(booking, showSeats);

        if (booking.getBookingStatus().equals(BookingStatus.FAILED)) {
            return booking;
        }

        // Simulate payment and if payment is confirmed then make seat confirmed for booking

        confirmBooking(booking);
        booking.updateBookingStatus(BookingStatus.CONFIRMED);
        return booking;

    }

}
