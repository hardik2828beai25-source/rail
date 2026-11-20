package booking;

import java.time.LocalDate;

import data.SeatInventory;
import model.Quota;
import model.Train;
import model.TravelClass;


public class SeatAvailabilityService {

    private final SeatInventory seatInventory;

    public SeatAvailabilityService(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }


     
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
