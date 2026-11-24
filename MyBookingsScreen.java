package menu;

import java.util.List;

import booking.BookingService;
import model.Booking;
import model.Passenger;
import model.Train;
import model.TrainBooking;
import model.User;
import service.TrainService;
import util.ConsolePrinter;
import util.InputReader;

/**
 * MyBookingsScreen.java
 * ---------------------------------------------------------------
 * Dashboard option 4: "My Bookings".
 *
 * Shows EVERY ticket booked by the logged-in user (confirmed and
 * cancelled), newest bookings at the end, and then lets the user
 * open the complete details of one booking by entering its PNR.
 *
 * The bookings are NOT stored here. They are read every time from
 * the shared BookingService (the same storage used for booking and
 * cancelling), so new bookings and cancellations appear at once.
 */
public class MyBookingsScreen {

    private final User user;                    // the logged-in user (session)
    private final InputReader input;
    private final BookingService bookingService;
    private final ConsolePrinter printer;
    private final TicketPrinter ticketPrinter;
    private final PnrLookup pnrLookup;

    public MyBookingsScreen(User user, InputReader input, TrainService trainService,
                            ConsolePrinter printer) {
        this.user = user;
        this.input = input;
        this.bookingService = trainService.getBookingService();
        this.printer = printer;
        this.ticketPrinter = new TicketPrinter(printer);
        this.pnrLookup = new PnrLookup(input, bookingService, user);
    }

    /** Shows the user's bookings, then the "view details" option. */
    public void show() {
        printer.printSectionHeader("MY BOOKINGS");

        // Only this user's bookings: BookingService filters by username.
        List<TrainBooking> myBookings = bookingService.getBookingsOfUser(user.getUsername());

        if (myBookings.isEmpty()) {
            System.out.println("  You have not booked any tickets yet.");
            Dashboard.waitForReturn(input, printer);
            return;
        }

        for (int i = 0; i < myBookings.size(); i++) {
            if (i > 0) {                                  // separator between bookings
                System.out.println();
                printer.printLine('-');
                System.out.println();
            }
            printBookingSummary(i + 1, myBookings.get(i));
        }
        System.out.println();
        printer.printLine('=');
        System.out.println("  Total bookings: " + myBookings.size());

        viewDetailsMenu();
    }

    // ======================= LIST OF BOOKINGS =================

    /** Prints one booking of the list, e.g. "Booking 1" and its details. */
    private void printBookingSummary(int number, TrainBooking b) {
        Train t = b.getTrain();
        System.out.println("  Booking " + number);
        System.out.println();
        printField("PNR Number", b.getPnr());
        printField("Train Number", t.getTrainNumber());
        printField("Train Name", t.getTrainName());
        printField("From", t.getSource());
        printField("To", t.getDestination());
        printField("Journey Date", b.getFormattedDate());
        printField("Departure Time", t.getDepartureTime());
        printField("Arrival Time", t.getArrivalTime());
        printField("Travel Class", b.getTravelClass().getDisplayName()
                + " (" + b.getTravelClass().getCode() + ")");
        printField("Quota", b.getQuota().getDisplayName());
        printField("Passengers", String.valueOf(b.getPassengerCount()));
        printPassengers(b);
        printField("Total Fare", "Rs. " + b.getTotalFare());
        printField("Booking Status", b.getStatus());
        if (b.getStatus().equals(Booking.CANCELLED)) {
            printField("Refund Amount", "Rs. " + b.getRefundAmount());
        }
    }

    /** One line per passenger: name, age, gender and seat. */
    private void printPassengers(TrainBooking b) {
        // A cancelled ticket's seats were given back, so mark them "released".
        String seatNote = b.getStatus().equals(Booking.CANCELLED) ? " (released)" : "";
        int number = 1;
        for (Passenger p : b.getPassengers()) {
            System.out.printf("      %d. %s, %d, %s - Seat %s%s%n", number, p.getName(),
                    p.getAge(), p.getGender(), b.getSeatLabel(p), seatNote);
            number++;
        }
    }

    /** Prints "  Label            : value" with the labels lined up. */
    private void printField(String label, String value) {
        System.out.printf("  %-16s : %s%n", label, value);
    }

    // ======================= VIEW DETAILS =====================

    /** Repeats until the user chooses 0 (back to the Dashboard). */
    private void viewDetailsMenu() {
        while (true) {
            System.out.println();
            printer.printLine('-');
            System.out.println("  1. View Booking Details (enter PNR)");
            System.out.println("  0. Return to Dashboard");
            System.out.print("Enter Your Choice: ");

            int choice = input.readChoice();
            if (choice == 0) {
                return;
            } else if (choice == 1) {
                viewBookingDetails();
            } else {
                System.out.println("[Error] Please enter 1 or 0.");
            }
        }
    }

    /**
     * Asks for a PNR and prints the complete ticket, only if the PNR
     * exists AND belongs to the logged-in user (checked by PnrLookup).
     */
    private void viewBookingDetails() {
        String pnr = pnrLookup.readPnr();
        if (pnr == null) {
            return;                                   // user entered 0
        }
        TrainBooking booking = pnrLookup.findOwnBooking(pnr);
        if (booking != null) {
            System.out.println();
            printer.printCentered("BOOKING DETAILS");
            ticketPrinter.printBookingDetails(booking);
        }
    }
}
