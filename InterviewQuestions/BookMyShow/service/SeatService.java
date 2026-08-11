//package InterviewQuestions.BookMyShow.service;
//
//import InterviewQuestions.BookMyShow.entities.Seat;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class SeatService {
//    ConcurrentHashMap<String, Seat> seats = new ConcurrentHashMap<>();
//
//    public void createSeats(Seat seat) {
//        this.seats.put(seat.getSeatId(), seat);
//    }
//
//    public Seat getSeatById(String seatId) {
//        return this.seats.get(seatId);
//    }
//}
