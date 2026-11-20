package booking;

import java.util.Random;

import data.BookingRepository;

/**
 * PnrGenerator.java
 * ---------------------------------------------------------------
 * Creates a random 10-digit PNR (first digit never 0) that is not
 * used by any existing booking.
 */
public class PnrGenerator {

    private final Random random = new Random();
    private final BookingRepository bookingRepository;

    /** @param bookingRepository used to make sure a PNR is never repeated */
    public PnrGenerator(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /** Returns a new, unused 10-digit PNR. */
    public String generatePnr() {
        String pnr;
        do {
            long number = random.nextLong(1_000_000_000L, 10_000_000_000L);
            pnr = String.valueOf(number);
        } while (bookingRepository.containsPnr(pnr));
        return pnr;
    }
}
