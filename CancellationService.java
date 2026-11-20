package cancellation;

import java.time.LocalDate;

import booking.SeatAllocator;
import model.Booking;
import model.TrainBooking;


public class CancellationService {

    private final SeatAllocator seatAllocator;
    private final RefundCalculator refundCalculator;

    public CancellationService(SeatAllocator seatAllocator, RefundCalculator refundCalculator) {
        this.seatAllocator = seatAllocator;
        this.refundCalculator = refundCalculator;
    }

    public boolean isCancellable(TrainBooking booking) {
        boolean active = booking.getStatus().equals(Booking.CONFIRMED)
                || booking.getStatus().equals(Booking.WAITING);
        return active && !booking.getJourneyDate().isBefore(LocalDate.now());
    }

    
    public int cancelBooking(TrainBooking booking) {
        seatAllocator.releaseSeats(booking);
        int refund = refundCalculator.calculateRefund(booking);
        booking.markCancelled(refund);
        return refund;
    }
}
