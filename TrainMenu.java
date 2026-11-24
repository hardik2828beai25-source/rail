package menu;

import java.util.List;

import model.Booking;
import model.Train;
import model.TrainBooking;
import model.User;
import service.TrainService;
import util.ConsolePrinter;
import util.InputReader;

/**
 * TrainMenu.java
 * ---------------------------------------------------------------
 * The Train Booking Menu. Opened from Dashboard.trainBooking().
 *
 * It has its OWN continuous loop (in start()). The loop ends ONLY
 * when the user chooses option 5; then start() returns to the
 * Dashboard, which simply continues its own loop.
 * This class never calls the Dashboard and never touches login.
 *
 * Option 1 (Book Ticket) is handled by BookTicketScreen.
 * Options 2-4 (My Bookings, PNR Status, Cancel Ticket) are here;
 * reading a PNR and the ownership check are done by PnrLookup.
 *
 * It receives:
 *   - the logged-in User       (so bookings belong to the right user)
 *   - the shared InputReader   (so there is only ONE Scanner for System.in)
 *   - the shared TrainService  (trains, seats and all bookings)
 */
public class TrainMenu {

    private static final int WIDTH = 60;

    private final User user;
    private final InputReader input;
    private final TrainService trainService;
    private final ConsolePrinter printer = new ConsolePrinter(WIDTH);
    private final TicketPrinter ticketPrinter = new TicketPrinter(printer);
    private final BookTicketScreen bookTicketScreen;
    private final PnrLookup pnrLookup;

    public TrainMenu(User user, InputReader input, TrainService trainService) {
        this.user = user;
        this.input = input;
        this.trainService = trainService;
        this.bookTicketScreen = new BookTicketScreen(user, input, trainService, printer, ticketPrinter);
        this.pnrLookup = new PnrLookup(input, trainService.getBookingService(), user);
    }

    // ===================== MAIN MENU LOOP =====================

    /** Shows the Train Menu again and again until the user selects 5. */
    public void start() {
        boolean inTrainMenu = true;

        while (inTrainMenu) {
            printTrainMenu();
            int choice = input.readChoice();

            switch (choice) {
                case 1:
                    bookTicketScreen.bookTicket();
                    break;
                case 2:
                    myBookings();
                    break;
                case 3:
                    pnrStatus();
                    break;
                case 4:
                    cancelTicket();
                    break;
                case 5:
                    inTrainMenu = false;      // leave the loop -> back to Dashboard
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }

    /** Prints the Train Booking Menu exactly as required. */
    private void printTrainMenu() {
        System.out.println();
        System.out.println("========== TRAIN BOOKING MENU ==========");
        System.out.println();
        System.out.println("1. Book Ticket");
        System.out.println("2. My Bookings");
        System.out.println("3. PNR Status");
        System.out.println("4. Cancel Ticket");
        System.out.println("5. Back to Main Dashboard");
        System.out.println("========================================");
        System.out.println();
        System.out.print("Enter your choice: ");
    }

    // ======================= 2. MY BOOKINGS ===================

    /** Shows only the logged-in user's train bookings. */
    public void myBookings() {
        printer.printSectionHeader("MY BOOKINGS");

        List<TrainBooking> myList = trainService.getBookingService().getBookingsOfUser(user.getUsername());
        if (myList.isEmpty()) {
            System.out.println("  No Bookings Found");
        } else {
            for (TrainBooking b : myList) {
                Train t = b.getTrain();
                System.out.println("  PNR        : " + b.getPnr());
                System.out.println("  Train      : " + t.getTrainNumber() + " - " + t.getTrainName());
                System.out.println("  Route      : " + t.getSource() + " -> " + t.getDestination());
                System.out.println("  Date       : " + b.getFormattedDate());
                System.out.println("  Passengers : " + b.getPassengerCount()
                        + "   Status : " + b.getStatusLabel()
                        + "   Total Fare : Rs " + b.getTotalFare());
                printer.printLine('-');
            }
        }
        waitForReturn(input);
    }

    // ======================= 3. PNR STATUS ====================

    /** Shows the details of one PNR, only if it belongs to the user. */
    public void pnrStatus() {
        printer.printSectionHeader("PNR STATUS");

        String pnr = pnrLookup.readPnr();
        if (pnr == null) {
            return;
        }
        TrainBooking booking = pnrLookup.findOwnBooking(pnr);
        if (booking != null) {
            ticketPrinter.printBookingDetails(booking);
        }
        waitForReturn(input);
    }

    // ===================== 4. CANCEL TICKET ===================

    /** Cancels a ticket of the logged-in user after confirmation. */
    public void cancelTicket() {
        printer.printSectionHeader("CANCEL TICKET");

        String pnr = pnrLookup.readPnr();
        if (pnr == null) {
            return;
        }
        TrainBooking booking = pnrLookup.findOwnBooking(pnr);
        if (booking == null) {
            waitForReturn(input);
            return;
        }

        ticketPrinter.printBookingDetails(booking);

        if (booking.getStatus().equals(Booking.CANCELLED)) {
            System.out.println("  This ticket is already cancelled.");
            waitForReturn(input);
            return;
        }
        if (!trainService.getCancellationService().isCancellable(booking)) {
            System.out.println("  This ticket cannot be cancelled (journey date has passed).");
            waitForReturn(input);
            return;
        }

        System.out.println("  CANCELLATION POLICY");
        System.out.println("    7 or more days before journey : 90% refund");
        System.out.println("    2 to 6 days before journey    : 75% refund");
        System.out.println("    1 day before journey          : 50% refund");
        System.out.println("    Same day                      : 25% refund");
        System.out.println();
        System.out.println("  Refund if cancelled now: Rs "
                + trainService.getRefundCalculator().calculateRefund(booking)
                + " (" + trainService.getRefundCalculator().getRefundPercent(booking.getJourneyDate()) + "%)");
        System.out.println();

        if (input.askYesNo("Do you want to cancel this ticket? (Y/N): ")) {
            int refund = trainService.getCancellationService().cancelBooking(booking);
            System.out.println();
            printer.printLine('=');
            System.out.println("  Ticket cancelled successfully.");
            System.out.println("  PNR " + booking.getPnr() + " - Refund Amount: Rs " + refund);
            System.out.println("  Your seats have been released.");
            printer.printLine('=');
        } else {
            System.out.println("\n  Cancellation aborted. Your ticket is unchanged.");
        }
        waitForReturn(input);
    }

    // ======================== HELPERS =========================

    /**
     * Waits until the user types 0, then returns to the TRAIN menu.
     * Package-level 'static' so BookTicketScreen can use the same code.
     */
    static void waitForReturn(InputReader input) {
        System.out.println();
        System.out.println("  0. Return to Train Booking Menu");
        input.waitForZero("Enter your choice: ",
                "[Error] Please enter 0 to return to the Train Booking Menu.");
    }
}
