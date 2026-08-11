package InterviewQuestions.BookMyShow.entities;

import InterviewQuestions.BookMyShow.enums.SeatStatus;

import java.util.concurrent.locks.ReentrantLock;

public class ShowSeat {
    private final Show show;
    private final Seat seat;
    private SeatStatus seatStatus;
    private double price;
    private ReentrantLock lock;

    public ShowSeat(Show show, Seat seat, double price) {
        this.show = show;
        this.seat = seat;
        this.price = price;
        this.seatStatus = SeatStatus.AVAILABLE;
        this.lock = new ReentrantLock();
    }

    public Show getShow() {
        return this.show;
    }

    public Seat getSeat() {
        return this.seat;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public boolean updateSeatStatus(SeatStatus newSeatStatus, SeatStatus previousStatus) {
        lock.lock();
        try {
            if (previousStatus != seatStatus) {
                return false;
            }
            this.seatStatus = newSeatStatus;

            return true;
        } finally {
            lock.unlock();
        }
    }

    public double getPrice() {
        return this.price;
    }

    public void updatePrice(double price) {
        this.price = price;
    }
}
