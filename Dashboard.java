package menu;

import java.util.List;

import auth.AuthService;
import auth.SessionManager;
import model.Booking;
import model.User;
import service.TrainService;
import util.ConsolePrinter;
import util.InputReader;
import wallet.WalletService;

/**
 * Dashboard.java
 * ---------------------------------------------------------------
 * The IRCTC Main Dashboard shown after a successful login.
 *
 * - Receives the SessionManager; the logged-in User is taken from it.
 *   The dashboard keeps running while the session is active.
 * - Receives the SAME InputReader used by the login system, so only
 *   one Scanner for System.in exists in the whole program.
 * - Runs its own menu loop. When the user chooses LOGOUT the loop
 *   ends and control goes back to the login menu.
 */
public class Dashboard {

    private static final int WIDTH = 60;     // width of every border line

    private final SessionManager session;    // who is logged in
    private final User user;                 // the logged-in user (from the session)
    private final InputReader input;         // shared input reader (not created here)
    private final TrainService trainService; // shared train data and bookings
    private final ConsolePrinter printer = new ConsolePrinter(WIDTH);
    private final MyBookingsScreen myBookingsScreen;   // option 4
    private final AccountMenu accountMenu;             // option 5
    private List<Booking> bookings;          // this user's bookings (refreshed when shown)

    /**
     * Constructor: object passing.
     * @param session       the session started by LoginSystem (holds the logged-in user)
     * @param input         the InputReader created in LoginSystem
     * @param trainService  the TrainService created in LoginSystem
     * @param authService   used by My Account > Change Password
     * @param walletService used by My Account > My IRCTC Wallet
     */
    public Dashboard(SessionManager session, InputReader input, TrainService trainService,
                     AuthService authService, WalletService walletService) {
        this.session = session;
        this.user = session.getCurrentUser();
        this.input = input;
        this.trainService = trainService;
        this.myBookingsScreen = new MyBookingsScreen(user, input, trainService, printer);
        this.accountMenu = new AccountMenu(session, input, printer, authService, walletService);
        this.bookings = loadBookings();
    }

    // ===================== MAIN MENU LOOP =====================

    /**
     * Shows the dashboard again and again until the user logs out.
     * This is the only method the login system needs to call.
     */
    public void displayDashboard() {
        while (session.isLoggedIn()) {
            printDashboardMenu();
            int choice = input.readChoice();

            switch (choice) {
                case 1:
                    trainBooking();
                    break;
                case 2:
                    flightBooking();
                    break;
                case 3:
                    busBooking();
                    break;
                case 4:
                    myBookings();
                    break;
                case 5:
                    myAccount();
                    break;
                case 6:
                    travelHistory();
                    break;
                case 7:
                    helpAndSupport();
                    break;
                case 8:
                    logout();                // ends the session -> loop stops
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please enter a number from 1 to 8.");
            }
        }
    }

    // ======================= MENU OPTIONS =====================

    /**
     * OPTION 1: Train Booking.
     * Opens the Train Booking Menu. TrainMenu.start() has its own loop
     * and returns here ONLY when the user selects "Back to Main Dashboard".
     * Then displayDashboard() simply continues and shows the Dashboard again.
     */
    public void trainBooking() {
        TrainMenu trainMenu = new TrainMenu(user, input, trainService);
        trainMenu.start();
    }

    /** OPTION 2: Flight Booking (placeholder). */
    public void flightBooking() {
        printer.printSectionHeader("FLIGHT BOOKING");
        System.out.println("  Flight Booking Module Coming Soon");
        waitForReturn();
    }

    /** OPTION 3: Bus Booking (placeholder). */
    public void busBooking() {
        printer.printSectionHeader("BUS BOOKING");
        System.out.println("  Bus Booking Module Coming Soon");
        waitForReturn();
    }

    /**
     * OPTION 4: shows all train tickets of the logged-in user with full
     * details, and lets the user open one booking by PNR.
     * The work is done by MyBookingsScreen.
     */
    public void myBookings() {
        myBookingsScreen.show();
    }

    /**
     * OPTION 5: My Account (profile, wallet, change password, logout).
     * If the user logs out inside My Account, the session is now empty:
     * the goodbye message is shown and the dashboard loop stops.
     */
    public void myAccount() {
        accountMenu.show();
        if (!session.isLoggedIn()) {
            printLogoutMessage();
        }
    }

    /** OPTION 6: shows completed and cancelled journeys separately. */
    public void travelHistory() {
        bookings = loadBookings();          // refresh: bookings may have changed
        printer.printSectionHeader("TRAVEL HISTORY");

        int completed = printHistoryGroup("COMPLETED JOURNEYS", Booking.COMPLETED);
        int cancelled = printHistoryGroup("CANCELLED JOURNEYS", Booking.CANCELLED);

        if (completed == 0 && cancelled == 0) {
            System.out.println("  No travel history available.");
        }
        waitForReturn();
    }

    /** OPTION 7: basic help information. */
    public void helpAndSupport() {
        printer.printSectionHeader("HELP & SUPPORT");
        System.out.println("  AVAILABLE SERVICES");
        System.out.println("    - Train, Flight and Bus booking");
        System.out.println("    - View bookings and travel history");
        System.out.println("    - My Account: profile, IRCTC wallet, change password");
        System.out.println();
        System.out.println("  HOW TO USE THE PORTAL");
        System.out.println("    1. Type the number of an option and press Enter.");
        System.out.println("    2. Follow the instructions shown on the screen.");
        System.out.println("    3. Choose 0 to return to the dashboard.");
        System.out.println("    4. Choose 8 on the dashboard to log out.");
        System.out.println();
        System.out.println("  CONTACT (demo details)");
        System.out.println("    Email : support@example.com");
        waitForReturn();
    }

    /** OPTION 8: ends the session. The loop in displayDashboard() then stops. */
    public void logout() {
        printLogoutMessage();
        session.endSession();
    }

    /** The goodbye message shown on logout. */
    private void printLogoutMessage() {
        System.out.println();
        printer.printLine('=');
        System.out.println("  " + user.getFullName() + ", you have been logged out successfully.");
        System.out.println("  Thank you for using IRCTC Smart Travel Portal!");
        printer.printLine('=');
    }

    // ===================== DATA (FUTURE DB) ====================

    /** Loads this user's bookings as small summary rows (BookingHistoryService). */
    private List<Booking> loadBookings() {
        return trainService.getBookingHistoryService().getBookingRows(user.getUsername());
    }

    // ======================= HELPER METHODS ===================

    /** Prints the full dashboard screen with the user's name. */
    private void printDashboardMenu() {
        System.out.println();
        printer.printLine('=');
        System.out.println();
        printer.printCentered("WELCOME TO IRCTC");
        printer.printCentered("SMART TRAVEL PORTAL");
        System.out.println();
        printer.printCentered("Your Journey, Our Priority!");
        System.out.println();
        printer.printLine('=');
        System.out.println();
        printer.printCentered("MAIN DASHBOARD");
        System.out.println();
        printer.printCentered("Logged in as: " + user.getFullName());
        System.out.println();
        printer.printLine('=');
        System.out.println();
        printer.printCentered("BOOK YOUR JOURNEY");
        System.out.println();
        System.out.println("    1. TRAIN BOOKING");
        System.out.println("    2. FLIGHT BOOKING");
        System.out.println("    3. BUS BOOKING");
        System.out.println();
        printer.printLine('-');
        System.out.println();
        printer.printCentered("MY ACCOUNT");
        System.out.println();
        System.out.println("    4. MY BOOKINGS");
        System.out.println("    5. MY ACCOUNT");
        System.out.println("    6. TRAVEL HISTORY");
        System.out.println();
        printer.printLine('-');
        System.out.println();
        printer.printCentered("OTHER SERVICES");
        System.out.println();
        System.out.println("    7. HELP & SUPPORT");
        System.out.println("    8. LOGOUT");
        System.out.println();
        printer.printLine('=');
        System.out.print("Enter Your Choice: ");
    }

    /**
     * Shows "0. Return to Dashboard" and waits until the user types 0.
     * Used at the end of every section.
     */
    private void waitForReturn() {
        waitForReturn(input, printer);
    }

    /**
     * Same as above, but 'static' so other dashboard screens
     * (MyBookingsScreen) can use exactly the same code.
     */
    static void waitForReturn(InputReader input, ConsolePrinter printer) {
        System.out.println();
        printer.printLine('-');
        System.out.println("  0. Return to Dashboard");
        input.waitForZero("Enter Your Choice: ", "[Error] Please enter 0 to return to the dashboard.");
    }

    /**
     * Prints all bookings having the given status under a heading.
     * @return how many bookings were printed
     */
    private int printHistoryGroup(String heading, String status) {
        int count = 0;
        for (Booking b : bookings) {
            if (b.getStatus().equals(status)) {
                if (count == 0) {                       // print heading once
                    System.out.println("  " + heading);
                    printBookingTableHeader();
                }
                printBookingRow(b);
                count++;
            }
        }
        if (count > 0) {
            System.out.println();
        }
        return count;
    }

    /** Prints the column titles of the booking table and a line under them. */
    private void printBookingTableHeader() {
        System.out.printf("  %-10s %-6s %-10s %-10s %-10s %-9s%n",
                "ID", "Type", "From", "To", "Date", "Status");
        printer.printLine('-');
    }

    /** Prints one booking as one table row. */
    private void printBookingRow(Booking b) {
        System.out.printf("  %-10s %-6s %-10s %-10s %-10s %-9s%n",
                b.getBookingId(), b.getJourneyType(), b.getSource(),
                b.getDestination(), b.getJourneyDate(), b.getStatus());
    }
}
