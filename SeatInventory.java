package data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import model.Train;
import model.TravelClass;

/**
 * SeatInventory.java
 * ---------------------------------------------------------------
 * In-memory storage of the seats of every train, date and class.
 * One boolean array per "train + date + class":
 *   true = booked, false = free.
 * The array is created (all seats free) the first time it is needed.
 */
public class SeatInventory {

    // "trainNo|date|CLASS" -> array of seats (true = booked, false = free)
    private final Map<String, boolean[]> seatMap = new HashMap<>();

    /** Returns the seat array of this train, date and class. */
    public boolean[] getSeats(Train train, LocalDate date, TravelClass travelClass) {
        String key = train.getTrainNumber() + "|" + date + "|" + travelClass.name();
        boolean[] seats = seatMap.get(key);
        if (seats == null) {                         // first time: all seats free
            seats = new boolean[travelClass.getSeatCapacity()];
            seatMap.put(key, seats);
        }
        return seats;
    }
}
