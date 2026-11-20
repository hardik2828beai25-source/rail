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

    public TrainBooking findBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr);
    }

    public List<TrainBooking> getBookingsOfUser(String username) {
        return bookingRepository.findByUsername(username);
    }
}
