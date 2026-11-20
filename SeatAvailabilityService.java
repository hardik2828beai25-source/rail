package booking;

import java.time.LocalDate;

import data.SeatInventory;
import model.Quota;
import model.Train;
import model.TravelClass;

/**
 * SeatAvailabilityService.java
 * ---------------------------------------------------------------
 * Counts the free seats of a train for a date and class.
 */
public class SeatAvailabilityService {

    private final SeatInventory seatInventory;

    public SeatAvailabilityService(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    /**
     * Number of free seats for a train, date and class.
     * Returns 0 if the train does not run that class.
     *
     * The quota is accepted here for the FUTURE: today all quotas share
     * the same seats. When quota-wise seat pools are added, only this
     * method (and the key in SeatInventory) needs to change.
     */
    public int getAvailableSeats(Train train, LocalDate date, TravelClass travelClass, Quota quota) {
        if (!train.offersClass(travelClass)) {
            return 0;
        }
        boolean[] seats = seatInventory.getSeats(train, date, travelClass);
        int free = 0;
        for (boolean booked : seats) {
            if (!booked) {
                free++;
            }
        }
        return free;
    }
}
