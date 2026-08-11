package InterviewQuestions.BookMyShow.entities;

import InterviewQuestions.BookMyShow.enums.SeatType;

import java.util.UUID;

public class Seat {
    private final String seatId;
    private final int seatNumber;
    private final SeatType seatType;

    public Seat(int seatNumber, SeatType seatType) {
        this.seatId = UUID.randomUUID().toString();
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    public int getSeatNumber() {
        return this.seatNumber;
    }
    public SeatType getSeatType() {
        return this.seatType;
    }

    public String getSeatId() {
        return this.seatId;
    }
}
