package booking;

import java.time.LocalDate;
import java.util.List;

import data.BookingRepository;
import fare.FareCalculator;
import model.Passenger;
import model.Quota;
import model.Train;
import model.TrainBooking;
import model.TravelClass;
import model.User;

/**
 * BookingService.java
 * ---------------------------------------------------------------
 * Books train tickets and finds existing bookings.
 * It does not do the small jobs itself; it uses (composition):
 *   SeatAvailabilityService -> are enough seats free?
 *   SeatAllocator           -> give seats to the passengers
 *   PnrGenerator            -> new unique PNR
 *   FareCalculator          -> total fare
 *   BookingRepository       -> save / find bookings
 */
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatAvailabilityService seatAvailabilityService;
    private final SeatAllocator seatAllocator;
    private final PnrGenerator pnrGenerator;
    private final FareCalculator fareCalculator;

    public BookingService(BookingRepository bookingRepository,
                          SeatAvailabilityService seatAvailabilityService,
                          SeatAllocator seatAllocator,
                          PnrGenerator pnrGenerator,
                          FareCalculator fareCalculator) {
        this.bookingRepository = bookingRepository;
        this.seatAvailabilityService = seatAvailabilityService;
        this.seatAllocator = seatAllocator;
        this.pnrGenerator = pnrGenerator;
        this.fareCalculator = fareCalculator;
    }

    /**
     * Books a ticket: allocates the first free seats automatically,
     * generates a unique PNR and saves the booking for the user.
     *
     * @return the new booking, or null if the class is not available
     *         on this train or there are not enough seats
     */
    public TrainBooking bookTicket(User user, Train train, LocalDate date,
                                   TravelClass travelClass, Quota quota,
                                   List<Passenger> passengers) {
        if (seatAvailabilityService.getAvailableSeats(train, date, travelClass, quota) < passengers.size()) {
            return null;
        }

        seatAllocator.allocateSeats(train, date, travelClass, passengers);

        String pnr = pnrGenerator.generatePnr();
        int totalFare = fareCalculator.calculateTotalFare(train, travelClass, quota, passengers.size());
        TrainBooking booking = new TrainBooking(pnr, user.getUsername(), train,
                travelClass, quota, date, passengers, totalFare);
        bookingRepository.save(booking);
        return booking;
    }

    /** Finds a booking by PNR, or null if the PNR does not exist. */
    public TrainBooking findBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr);
    }

    /** Only the bookings of the given user (never other users' bookings). */
    public List<TrainBooking> getBookingsOfUser(String username) {
        return bookingRepository.findByUsername(username);
    }
}
