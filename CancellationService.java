package cancellation;

import java.time.LocalDate;

import booking.SeatAllocator;
import model.Booking;
import model.TrainBooking;

/**
 * CancellationService.java
 * ---------------------------------------------------------------
 * Decides whether a ticket can be cancelled, and cancels it:
 * frees the seats, calculates the refund and marks it Cancelled.
 */
public class CancellationService {

    private final SeatAllocator seatAllocator;
    private final RefundCalculator refundCalculator;

    public CancellationService(SeatAllocator seatAllocator, RefundCalculator refundCalculator) {
        this.seatAllocator = seatAllocator;
        this.refundCalculator = refundCalculator;
    }

    /** A ticket can be cancelled if it is Confirmed/Waiting and the journey is not in the past. */
    public boolean isCancellable(TrainBooking booking) {
        boolean active = booking.getStatus().equals(Booking.CONFIRMED)
                || booking.getStatus().equals(Booking.WAITING);
        return active && !booking.getJourneyDate().isBefore(LocalDate.now());
    }

    /**
     * Cancels the ticket: frees its seats (so another booking can take
     * them), marks it Cancelled and returns the refund amount.
     */
    public int cancelBooking(TrainBooking booking) {
        seatAllocator.releaseSeats(booking);
        int refund = refundCalculator.calculateRefund(booking);
        booking.markCancelled(refund);
        return refund;
    }
}
