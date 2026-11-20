package cancellation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import model.TrainBooking;

/**
 * RefundCalculator.java
 * ---------------------------------------------------------------
 * Calculates how much money is returned when a ticket is cancelled.
 */
public class RefundCalculator {

    /**
     * Refund percentage, based on days left before the journey:
     *   7 or more days : 90%
     *   2 to 6 days    : 75%
     *   1 day          : 50%
     *   same day       : 25%
     */
    public int getRefundPercent(LocalDate journeyDate) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), journeyDate);
        if (daysLeft >= 7) {
            return 90;
        } else if (daysLeft >= 2) {
            return 75;
        } else if (daysLeft == 1) {
            return 50;
        }
        return 25;
    }

    /** Refund the user would get if the ticket were cancelled now. */
    public int calculateRefund(TrainBooking booking) {
        return booking.getTotalFare() * getRefundPercent(booking.getJourneyDate()) / 100;
    }
}
