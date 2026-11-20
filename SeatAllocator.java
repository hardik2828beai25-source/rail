package booking;

import java.time.LocalDate;
import java.util.List;

import data.SeatInventory;
import model.Passenger;
import model.Train;
import model.TrainBooking;
import model.TravelClass;


public class SeatAllocator {

    private final SeatInventory seatInventory;

    public SeatAllocator(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

  
    public void allocateSeats(Train train, LocalDate date, TravelClass travelClass,
                              List<Passenger> passengers) {
        boolean[] seats = seatInventory.getSeats(train, date, travelClass);
        int index = 0;
        for (Passenger p : passengers) {
            while (seats[index]) {          
                index++;
            }
            seats[index] = true;           
            p.assignSeat(index + 1);        
        }
    }
    public void releaseSeats(TrainBooking booking) {
        boolean[] seats = seatInventory.getSeats(booking.getTrain(), booking.getJourneyDate(),
                booking.getTravelClass());
        for (Passenger p : booking.getPassengers()) {
            seats[p.getSeatNumber() - 1] = false;     
        }
    }
}
