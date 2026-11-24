package menu;

import java.util.List;

import model.Train;
import model.TravelClass;
import util.ConsolePrinter;
import util.InputReader;

/**
 * TrainSearchView.java
 * ---------------------------------------------------------------
 * Shows the train search results as a table (like the IRCTC app)
 * and reads which train the user wants (booking step 3).
 */
public class TrainSearchView {

    private static final int TABLE_WIDTH = 73;   // width of the train results table

    private final InputReader input;
    private final ConsolePrinter tablePrinter = new ConsolePrinter(TABLE_WIDTH);

    public TrainSearchView(InputReader input) {
        this.input = input;
    }

    /**
     * Prints the search results: a table of trains, and under each
     * train its classes, fares and seats.
     * (The journey date is chosen later, so seats are shown per date.)
     */
    public void displayTrains(String source, String destination, List<Train> trains) {
        System.out.println();
        tablePrinter.printLine('=');
        tablePrinter.printCentered("AVAILABLE TRAINS");
        tablePrinter.printLine('=');
        System.out.println();
        System.out.println("  From : " + source);
        System.out.println("  To   : " + destination);
        System.out.println("  Trains found : " + trains.size());
        System.out.println();
        tablePrinter.printLine('-');
        System.out.printf("%-3s %-7s %-26s %-10s %-14s %-8s%n",
                "No", "Train", "Train Name", "Departure", "Arrival", "Duration");
        tablePrinter.printLine('-');

        for (int i = 0; i < trains.size(); i++) {
            Train t = trains.get(i);
            System.out.printf("%-3d %-7s %-26s %-10s %-14s %-8s%n", i + 1, t.getTrainNumber(),
                    t.getTrainName(), t.getDepartureTime(), t.getArrivalTime(), t.getDuration());

            StringBuilder fares = new StringBuilder("    Fare  : ");
            StringBuilder seats = new StringBuilder("    Seats : ");
            for (TravelClass c : t.getClasses()) {
                fares.append(c.getCode()).append(" Rs").append(t.getFare(c)).append("   ");
                seats.append(c.getCode()).append(' ').append(c.getSeatCapacity()).append("   ");
            }
            System.out.println(fares);
            System.out.println(seats);
            tablePrinter.printLine('-');
        }
        System.out.println("  Fares are per passenger (General quota). Seats are per journey date;");
        System.out.println("  the seats left on your date are shown after you choose the date.");
        System.out.println("  All trains and schedules are fictional sample data for this project.");
    }

    /**
     * Reads the train choice: the serial number (No column) OR the train
     * number. Only trains in the search results are accepted.
     * @return the chosen train, or null if the user enters 0 (change stations)
     */
    public Train readTrainSelection(List<Train> trains) {
        while (true) {
            System.out.print("Enter serial no. (1-" + trains.size()
                    + ") or train number (0 = change stations): ");
            String choice = input.readLine();
            if (choice.equals("0")) {
                return null;
            }
            for (Train t : trains) {
                if (t.getTrainNumber().equals(choice)) {
                    return t;
                }
            }
            try {
                int serial = Integer.parseInt(choice);
                if (serial >= 1 && serial <= trains.size()) {
                    return trains.get(serial - 1);
                }
            } catch (NumberFormatException e) {
                // not a number: fall through to the error message
            }
            System.out.println("[Error] Enter a serial number from 1 to " + trains.size()
                    + " or a train number shown in the list above.");
        }
    }
}
