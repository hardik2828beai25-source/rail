package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.TrainBooking;


public class BookingRepository {

   
    private final Map<String, TrainBooking> bookings = new LinkedHashMap<>();

    
    public void save(TrainBooking booking) {
        bookings.put(booking.getPnr(), booking);
    }


    public boolean containsPnr(String pnr) {
        return bookings.containsKey(pnr);
    }

    
    public TrainBooking findByPnr(String pnr) {
        return bookings.get(pnr);
    }

    public List<TrainBooking> findByUsername(String username) {
        List<TrainBooking> result = new ArrayList<>();
        for (TrainBooking b : bookings.values()) {
            if (b.isOwnedBy(username)) {
                result.add(b);
            }
        }
        return result;
    }
}
