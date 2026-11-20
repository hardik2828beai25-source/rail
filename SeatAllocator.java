package booking;

import java.time.LocalDate;
import java.util.List;

import data.SeatInventory;
import model.Passenger;
import model.Train;
import model.TrainBooking;
import model.TravelClass;

/**
 * SeatAllocator.java
 * ---------------------------------------------------------------
 * Gives seats to passengers (first free seats, automatically) and
 * frees them again when a ticket is cancelled.
 */
public class SeatAllocator {

    private final SeatInventory seatInventory;

    public SeatAllocator(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    /**
     * Allocates the first free seats to the passengers.
     * The caller must first check that enough seats are free.
     */
    public void allocateSeats(Train train, LocalDate date, TravelClass travelClass,
                              List<Passenger> passengers) {
        boolean[] seats = seatInventory.getSeats(train, date, travelClass);
        int index = 0;
        for (Passenger p : passengers) {
            while (seats[index]) {          // skip seats that are already booked
                index++;
            }
            seats[index] = true;            // book it
            p.assignSeat(index + 1);        // seat numbers start from 1
        }
    }

    /** Frees the seats of every passenger of this booking. */
    public void releaseSeats(TrainBooking booking) {
        boolean[] seats = seatInventory.getSeats(booking.getTrain(), booking.getJourneyDate(),
                booking.getTravelClass());
        for (Passenger p : booking.getPassengers()) {
            seats[p.getSeatNumber() - 1] = false;      // release the seat
        }
    }
}
