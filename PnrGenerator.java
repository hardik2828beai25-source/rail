package booking;

import java.util.Random;

import data.BookingRepository;


public class PnrGenerator {

    private final Random random = new Random();
    private final BookingRepository bookingRepository;

 
    public PnrGenerator(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

   
    public String generatePnr() {
        String pnr;
        do {
            long number = random.nextLong(1_000_000_000L, 10_000_000_000L);
            pnr = String.valueOf(number);
        } while (bookingRepository.containsPnr(pnr));
        return pnr;
    }
}
