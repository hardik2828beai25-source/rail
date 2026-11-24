package menu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import model.Passenger;
import model.Quota;
import model.Train;
import model.TrainBooking;
import model.TravelClass;
import model.User;
import service.TrainService;
import util.ConsolePrinter;
import util.InputReader;
import validation.JourneyDateValidator;

/**
 * BookTicketScreen.java
 * ---------------------------------------------------------------
 * Option 1 of the Train Booking Menu: the complete booking workflow.
 * It controls the ORDER of the steps; the steps themselves are done
 * by helper classes (TrainSearchView, ClassAndQuotaSelector,
 * PassengerInput, TicketPrinter) and the services in TrainService.
 */
public class BookTicketScreen {

    private static final int MAX_PASSENGERS = 6;

    private final User user;
    private final InputReader input;
    private final TrainService trainService;
    private final ConsolePrinter printer;
    private final TicketPrinter ticketPrinter;
    private final TrainSearchView trainSearchView;
    private final ClassAndQuotaSelector classAndQuotaSelector;
    private final PassengerInput passengerInput;

    public BookTicketScreen(User user, InputReader input, TrainService trainService,
                            ConsolePrinter printer, TicketPrinter ticketPrinter) {
        this.user = user;
        this.input = input;
        this.trainService = trainService;
        this.printer = printer;
        this.ticketPrinter = ticketPrinter;
        this.trainSearchView = new TrainSearchView(input);
        this.classAndQuotaSelector = new ClassAndQuotaSelector(input, printer,
                trainService.getSeatAvailabilityService());
        this.passengerInput = new PassengerInput(input);
    }

    /**
     * Complete booking workflow, in this exact order:
     *  1 Boarding Station   2 Destination Station   3 Select Train
     *  4 Travel Class       5 Quota                 6 Journey Date
     *  7 Passenger Details  8 Total Fare            9 Confirm
     * then the PNR is generated and the confirmation is shown.
     * Returns to the Train Menu when finished or cancelled.
     */
    public void bookTicket() {
        printer.printSectionHeader("BOOK TICKET");
        System.out.println("  (Enter 0 at any prompt of steps 1-6 to cancel the booking)");

        // ---- STEPS 1-3: stations -> train search -> select train ----
        // Repeats until a train is chosen, so the user can go back and
        // pick different stations (no trains found, or 0 at the train prompt).
        Train train = null;
        while (train == null) {

            // STEP 1: boarding station
            printStep(1, "BOARDING STATION");
            System.out.println("  Available stations: "
                    + String.join(", ", trainService.getStationService().getStations()));
            String source = readStation("Boarding Station: ", null);
            if (source == null) {
                printCancelled();
                return;
            }

            // STEP 2: destination station, then search the sample train database
            printStep(2, "DESTINATION STATION");
            List<String> reachable = trainService.getTrainSearchService().getDestinationsFrom(source);
            if (!reachable.isEmpty()) {
                System.out.println("  Trains from " + source + " go to: " + String.join(", ", reachable));
            }
            String destination = readStation("Destination Station: ", source);
            if (destination == null) {
                printCancelled();
                return;
            }

            List<Train> trains = trainService.getTrainSearchService().searchTrains(source, destination);
            if (trains.isEmpty()) {
                System.out.println("\n  Sorry! No trains are available between these stations.");
                if (!input.askYesNo("Search again with different stations? (Y/N): ")) {
                    printCancelled();
                    return;
                }
                continue;                                   // back to step 1
            }
            trainSearchView.displayTrains(source, destination, trains);

            // STEP 3: select a train from the results
            printStep(3, "SELECT TRAIN");
            train = trainSearchView.readTrainSelection(trains);   // null = change stations
        }
        System.out.println("\n  Selected train: " + train.getTrainNumber() + " - " + train.getTrainName()
                + "  (" + train.getDepartureTime() + " -> " + train.getArrivalTime() + ")");

        // ---- STEP 4: travel class ----
        printStep(4, "SELECT TRAVEL CLASS");
        int classOption = classAndQuotaSelector.selectClassOption(train);   // 0 = cancel, 1 = All Class, 2-10 = one class
        if (classOption == 0) {
            printCancelled();
            return;
        }
        boolean allClasses = (classOption == ClassAndQuotaSelector.ALL_CLASSES_OPTION);
        // With "All Class" the real class is chosen after the date (see step 6).
        TravelClass travelClass = allClasses ? null : classAndQuotaSelector.classFromOption(classOption);

        // ---- STEP 5: quota ----
        printStep(5, "SELECT QUOTA");
        Quota quota = classAndQuotaSelector.selectQuota(train, travelClass);
        if (quota == null) {
            printCancelled();
            return;
        }

        // ---- STEP 6: journey date (then seats are checked for that date) ----
        printStep(6, "SELECT JOURNEY DATE");
        LocalDate date;
        int freeSeats;
        while (true) {
            date = readJourneyDate();
            if (date == null) {
                printCancelled();
                return;
            }
            if (allClasses) {
                // Show every class with seats and fare for this date; the user picks one.
                travelClass = classAndQuotaSelector.chooseAvailableClass(train, date, quota);
                if (travelClass == null) {
                    continue;                       // nothing chosen: ask for another date
                }
            }
            freeSeats = trainService.getSeatAvailabilityService()
                    .getAvailableSeats(train, date, travelClass, quota);
            if (freeSeats > 0) {
                break;
            }
            System.out.println("[Error] No seats available in " + travelClass.getDisplayName()
                    + " on " + date.format(TrainBooking.DATE_FORMAT)
                    + ". Please enter another date.");
        }
        System.out.println("  Seats available in " + travelClass.getDisplayName() + ": " + freeSeats);

        // ---- STEP 7: passenger details ----
        printStep(7, "PASSENGER DETAILS");
        List<Passenger> passengers = passengerInput.readPassengers(Math.min(MAX_PASSENGERS, freeSeats));

        // ---- STEP 8: total fare ----
        printStep(8, "TOTAL FARE");
        int farePerPassenger = trainService.getFareCalculator().getFarePerPassenger(train, travelClass, quota);
        int totalFare = trainService.getFareCalculator()
                .calculateTotalFare(train, travelClass, quota, passengers.size());
        System.out.println("  Train      : " + train.getTrainNumber() + " - " + train.getTrainName());
        System.out.println("  Route      : " + train.getSource() + " -> " + train.getDestination());
        System.out.println("  Class      : " + travelClass.getDisplayName() + " (" + travelClass.getCode() + ")");
        System.out.println("  Quota      : " + quota.getDisplayName());
        System.out.println("  Date       : " + date.format(TrainBooking.DATE_FORMAT));
        System.out.println("  Passengers : " + passengers.size());
        System.out.println("  Fare       : Rs " + farePerPassenger + " x "
                + passengers.size() + " = Rs " + totalFare);

        // ---- STEP 9: confirm ----
        printStep(9, "CONFIRM BOOKING");
        if (!input.askYesNo("Confirm booking? (Y/N): ")) {
            printCancelled();
            return;
        }

        // ---- PNR generation + seat allocation + saving ----
        TrainBooking booking = trainService.getBookingService()
                .bookTicket(user, train, date, travelClass, quota, passengers);
        if (booking == null) {
            System.out.println("\n[Error] Seats are no longer available. Please try again.");
            TrainMenu.waitForReturn(input);
            return;
        }

        // ---- Booking confirmation ----
        System.out.println();
        printer.printLine('=');
        printer.printCentered("BOOKING CONFIRMED");
        printer.printLine('=');
        ticketPrinter.printBookingDetails(booking);
        System.out.println("  Thank you for booking with IRCTC! Happy Journey!");
        TrainMenu.waitForReturn(input);
    }

    // ======================== INPUT HELPERS ===================

    /**
     * Reads a station name and checks it exists.
     * @param notAllowed a station that must not be entered again (the source), or null
     * @return the station name, or null if the user entered 0
     */
    private String readStation(String prompt, String notAllowed) {
        while (true) {
            System.out.print(prompt);
            String typed = input.readLine();
            if (typed.equals("0")) {
                return null;
            }
            String station = trainService.getStationService().findStation(typed);
            if (station == null) {
                System.out.println("[Error] Station not found. Choose from the list above.");
            } else if (station.equalsIgnoreCase(notAllowed)) {
                System.out.println("[Error] Destination must be different from the source.");
            } else {
                return station;
            }
        }
    }

    /**
     * Reads the journey date (dd-MM-yyyy). Allowed: today up to
     * JourneyDateValidator.MAX_ADVANCE_DAYS days ahead.
     * @return the date, or null if the user entered 0
     */
    private LocalDate readJourneyDate() {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = JourneyDateValidator.lastBookableDate(today);

        while (true) {
            System.out.print("Journey Date (dd-MM-yyyy): ");
            String typed = input.readLine();
            if (typed.equals("0")) {
                return null;
            }
            try {
                LocalDate date = JourneyDateValidator.parse(typed);
                if (JourneyDateValidator.isInPast(date, today)) {
                    System.out.println("[Error] The date cannot be in the past.");
                } else if (JourneyDateValidator.isTooFarAhead(date, today)) {
                    System.out.println("[Error] You can book only " + JourneyDateValidator.MAX_ADVANCE_DAYS
                            + " days in advance. The last bookable date is "
                            + lastDate.format(TrainBooking.DATE_FORMAT) + ".");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("[Error] Invalid date. Use dd-MM-yyyy, e.g. "
                        + today.plusDays(10).format(TrainBooking.DATE_FORMAT));
            }
        }
    }

    // ======================== DISPLAY HELPERS =================

    /** Prints a step heading such as "STEP 4: SELECT TRAVEL CLASS". */
    private void printStep(int number, String title) {
        System.out.println();
        System.out.println("  STEP " + number + ": " + title);
        printer.printLine('-');
    }

    private void printCancelled() {
        System.out.println("\n  Booking cancelled.");
    }
}
