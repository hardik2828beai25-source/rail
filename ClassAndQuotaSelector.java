package menu;

import java.time.LocalDate;

import booking.SeatAvailabilityService;
import model.Quota;
import model.Train;
import model.TrainBooking;
import model.TravelClass;
import util.ConsolePrinter;
import util.InputReader;

/**
 * ClassAndQuotaSelector.java
 * ---------------------------------------------------------------
 * The travel-class and quota questions of the booking (steps 4 and 5),
 * plus the "All Class" availability list shown after the date.
 */
public class ClassAndQuotaSelector {

    /** Menu number of "All Class" in the class list. */
    public static final int ALL_CLASSES_OPTION = 1;

    private final InputReader input;
    private final ConsolePrinter printer;
    private final SeatAvailabilityService seatAvailability;

    public ClassAndQuotaSelector(InputReader input, ConsolePrinter printer,
                                 SeatAvailabilityService seatAvailability) {
        this.input = input;
        this.printer = printer;
        this.seatAvailability = seatAvailability;
    }

    /**
     * Shows "All Class" at the top, then the nine classes. Classes the
     * train does not run are marked and cannot be chosen.
     * @return 0 = cancel, 1 = All Class, 2 to 10 = a single class
     */
    public int selectClassOption(Train train) {
        TravelClass[] classes = TravelClass.values();

        System.out.println("  Select a travel class (fare per passenger, General quota):");
        System.out.printf("  %d. %-16s see seats and fares of every class for your date%n",
                ALL_CLASSES_OPTION, "All Class");
        for (int i = 0; i < classes.length; i++) {
            TravelClass c = classes[i];
            if (train.offersClass(c)) {
                System.out.printf("  %d. %-16s (%s)   Rs %d%n",
                        i + 2, c.getDisplayName(), c.getCode(), train.getFare(c));
            } else {
                System.out.printf("  %d. %-16s (%s)   not available on this train%n",
                        i + 2, c.getDisplayName(), c.getCode());
            }
        }
        System.out.println("  0. Cancel booking");

        int highest = classes.length + 1;
        while (true) {
            int choice = input.readNumber("Enter class number (1-" + highest + "): ", 0, highest);
            if (choice <= ALL_CLASSES_OPTION) {
                return choice;                           // 0 = cancel, 1 = All Class
            }
            TravelClass selected = classes[choice - 2];
            if (train.offersClass(selected)) {
                return choice;
            }
            System.out.println("[Error] " + selected.getDisplayName()
                    + " is not available on this train. Please choose another class.");
        }
    }

    /** Converts a class menu number (2 to 10) into the TravelClass. */
    public TravelClass classFromOption(int classOption) {
        return TravelClass.values()[classOption - 2];
    }

    /**
     * Used after "All Class": shows every class this train runs, with free
     * seats and fare for the chosen date and quota, and lets the user pick
     * one that still has seats.
     * @return the chosen class, or null if no seats are free or the user
     *         chooses 0 (the caller then asks for another date)
     */
    public TravelClass chooseAvailableClass(Train train, LocalDate date, Quota quota) {
        TravelClass[] classes = TravelClass.values();

        System.out.println();
        System.out.println("  CLASS AVAILABILITY ON " + date.format(TrainBooking.DATE_FORMAT)
                + " (" + quota.getDisplayName() + " quota)");
        printer.printLine('-');
        int classesWithSeats = 0;
        for (int i = 0; i < classes.length; i++) {
            TravelClass c = classes[i];
            if (!train.offersClass(c)) {
                continue;                                // train does not run this class
            }
            int free = seatAvailability.getAvailableSeats(train, date, c, quota);
            String seats = free > 0 ? String.valueOf(free) : "SOLD OUT";
            System.out.printf("  %d. %-16s (%s)   Seats: %-9s Fare: Rs %d%n",
                    i + 2, c.getDisplayName(), c.getCode(), seats, train.getFare(c, quota));
            if (free > 0) {
                classesWithSeats++;
            }
        }
        printer.printLine('-');

        if (classesWithSeats == 0) {
            System.out.println("[Error] No seats available in any class on "
                    + date.format(TrainBooking.DATE_FORMAT) + ". Please enter another date.");
            return null;
        }

        int highest = classes.length + 1;
        while (true) {
            int choice = input.readNumber("Enter class number (0 = choose another date): ", 0, highest);
            if (choice == 0) {
                return null;
            }
            if (choice == ALL_CLASSES_OPTION) {
                System.out.println("[Error] Please choose one of the classes listed above.");
                continue;
            }
            TravelClass selected = classes[choice - 2];
            if (train.offersClass(selected)
                    && seatAvailability.getAvailableSeats(train, date, selected, quota) > 0) {
                return selected;
            }
            System.out.println("[Error] " + selected.getDisplayName()
                    + " is not available. Please choose a class with seats.");
        }
    }

    /**
     * Shows the three quotas with the fare per passenger for the chosen class.
     * @return the chosen quota, or null if the user enters 0
     */
    public Quota selectQuota(Train train, TravelClass travelClass) {
        Quota[] quotas = Quota.values();

        if (travelClass != null) {
            System.out.println("  Select a quota (fare per passenger in " + travelClass.getDisplayName() + "):");
        } else {
            System.out.println("  Select a quota (fares of all classes are shown after you pick the date):");
        }
        for (int i = 0; i < quotas.length; i++) {
            Quota q = quotas[i];
            String fare = travelClass == null ? "" : "Rs " + train.getFare(travelClass, q);
            String extra = q.getSurchargePercent() == 0
                    ? "" : "  (+" + q.getSurchargePercent() + "% surcharge)";
            System.out.printf("  %d. %-15s %s%s%n", i + 1, q.getDisplayName(), fare, extra);
        }
        System.out.println("  0. Cancel booking");
        System.out.println("  Note: Tatkal rules such as booking timings and refund rules are not");
        System.out.println("        implemented yet. Only the fare surcharge is applied.");

        int choice = input.readNumber("Enter quota number (1-" + quotas.length + "): ", 0, quotas.length);
        if (choice == 0) {
            return null;
        }
        return quotas[choice - 1];
    }
}
