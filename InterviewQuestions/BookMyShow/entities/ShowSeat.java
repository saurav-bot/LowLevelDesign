package InterviewQuestions.BookMyShow.entities;

import InterviewQuestions.BookMyShow.enums.SeatStatus;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class ShowSeat {
    private final Show show;
    private final Seat seat;
    private final AtomicReference<SeatStatus> seatStatus;
    private BigDecimal price;

    public ShowSeat(Show show, Seat seat, BigDecimal price) {
        this.show = show;
        this.seat = seat;
        this.price = price;
        this.seatStatus = new AtomicReference<>(SeatStatus.AVAILABLE);
    }

    public Show getShow() {
        return this.show;
    }

    public Seat getSeat() {
        return this.seat;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus.get();
    }

    public boolean updateSeatStatus(SeatStatus newSeatStatus, SeatStatus previousStatus) {
        return seatStatus.compareAndSet(previousStatus, newSeatStatus);
    }

    public boolean lockSeat() {
        return seatStatus.compareAndSet(SeatStatus.AVAILABLE, SeatStatus.LOCKED);
    }

    public boolean confirmSeat() {
        return seatStatus.compareAndSet(SeatStatus.LOCKED, SeatStatus.BOOKED);
    }

    public boolean releaseSeat() {
        return seatStatus.compareAndSet(SeatStatus.LOCKED, SeatStatus.AVAILABLE);
    }

    public boolean cancelSeat() {
        return seatStatus.compareAndSet(SeatStatus.BOOKED, SeatStatus.AVAILABLE);
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void updatePrice(BigDecimal price) {
        this.price = price;
    }
}
