package cancellation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import model.TrainBooking;


public class RefundCalculator {


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

    
    public int calculateRefund(TrainBooking booking) {
        return booking.getTotalFare() * getRefundPercent(booking.getJourneyDate()) / 100;
    }
}
