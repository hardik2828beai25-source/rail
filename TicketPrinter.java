package menu;

import model.Booking;
import model.Passenger;
import model.Train;
import model.TrainBooking;
import util.ConsolePrinter;

/**
 * TicketPrinter.java
 * ---------------------------------------------------------------
 * Prints the full details of one ticket (train, journey, passengers,
 * seats, fare and refund). Used by three screens: booking
 * confirmation, PNR status and cancel ticket.
 */
public class TicketPrinter {

    private final ConsolePrinter printer;

    public TicketPrinter(ConsolePrinter printer) {
        this.printer = printer;
    }

    /** Prints the full details of one booking. */
    public void printBookingDetails(TrainBooking b) {
        Train t = b.getTrain();
        printer.printLine('-');
        System.out.println("  PNR Number   : " + b.getPnr());
        System.out.println("  Status       : " + b.getStatusLabel());
        System.out.println("  Train        : " + t.getTrainNumber() + " - " + t.getTrainName());
        System.out.println("  Route        : " + t.getSource() + " -> " + t.getDestination());
        System.out.println("  Departure    : " + t.getDepartureTime() + "   Arrival : " + t.getArrivalTime());
        System.out.println("  Duration     : " + t.getDuration());
        System.out.println("  Journey Date : " + b.getFormattedDate());
        System.out.println("  Class        : " + b.getTravelClass().getDisplayName());
        System.out.println("  Quota        : " + b.getQuota().getDisplayName());
        printer.printLine('-');
        System.out.println("  PASSENGERS");
        System.out.printf("  %-3s %-20s %-4s %-8s %-6s%n", "No", "Name", "Age", "Gender", "Seat");
        int number = 1;
        for (Passenger p : b.getPassengers()) {
            System.out.printf("  %-3d %-20s %-4d %-8s %-6s%n", number, p.getName(),
                    p.getAge(), p.getGender(), b.getSeatLabel(p));
            number++;
        }
        printer.printLine('-');
        System.out.println("  Total Fare   : Rs " + b.getTotalFare());
        if (b.getStatus().equals(Booking.CANCELLED)) {
            System.out.println("  Refund       : Rs " + b.getRefundAmount());
        }
        printer.printLine('-');
    }
}
