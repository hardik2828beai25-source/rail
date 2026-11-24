package menu;

import booking.BookingService;
import model.TrainBooking;
import model.User;
import util.InputReader;
import validation.BookingInputValidator;

/**
 * PnrLookup.java
 * ---------------------------------------------------------------
 * Reads a PNR from the keyboard and finds the booking ONLY if it
 * belongs to the logged-in user. Used by:
 *   - Dashboard  > My Bookings > View Booking Details
 *   - TrainMenu  > PNR Status and Cancel Ticket
 * so the security check is written in one place only.
 */
public class PnrLookup {

    private final InputReader input;
    private final BookingService bookingService;
    private final User user;                 // the logged-in user (session)

    public PnrLookup(InputReader input, BookingService bookingService, User user) {
        this.input = input;
        this.bookingService = bookingService;
        this.user = user;
    }

    /** Reads a PNR: exactly 10 digits. Returns null if the user enters 0. */
    public String readPnr() {
        while (true) {
            System.out.print("Enter 10-digit PNR (0 to go back): ");
            String typed = input.readLine();
            if (typed.equals("0")) {
                return null;
            }
            if (BookingInputValidator.isValidPnr(typed)) {
                return typed;
            }
            System.out.println("[Error] PNR must contain exactly 10 digits.");
        }
    }

    /**
     * Finds a booking by PNR AND checks that it belongs to the
     * logged-in user. Prints the error and returns null otherwise.
     */
    public TrainBooking findOwnBooking(String pnr) {
        TrainBooking booking = bookingService.findBookingByPnr(pnr);
        if (booking == null) {
            System.out.println("\n[Error] Invalid PNR Number");
            return null;
        }
        if (!booking.isOwnedBy(user.getUsername())) {
            System.out.println("\n[Error] Access denied. This PNR does not belong to your account.");
            return null;
        }
        return booking;
    }
}
