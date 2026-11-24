package menu;

import java.util.ArrayList;
import java.util.List;

import model.Passenger;
import util.InputReader;
import validation.BookingInputValidator;

/**
 * PassengerInput.java
 * ---------------------------------------------------------------
 * Booking step 7: asks how many passengers travel and reads the
 * name, age and gender of each one.
 */
public class PassengerInput {

    private final InputReader input;

    public PassengerInput(InputReader input) {
        this.input = input;
    }

    /** Reads 1 to maxAllowed passengers (name, age, gender). */
    public List<Passenger> readPassengers(int maxAllowed) {
        int count = input.readNumber("Number of passengers (1-" + maxAllowed + "): ", 1, maxAllowed);
        List<Passenger> passengers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            System.out.println("\n  --- Passenger " + i + " ---");
            String name = readPassengerName();
            int age = input.readNumber("Age (1-120): ", 1, 120);
            String gender = readGender();
            passengers.add(new Passenger(name, age, gender));
        }
        return passengers;
    }

    private String readPassengerName() {
        while (true) {
            System.out.print("Name: ");
            String name = input.readLine();
            if (BookingInputValidator.isValidPassengerName(name)) {
                return name;
            }
            System.out.println("[Error] Name must be 2-20 characters (letters and spaces only).");
        }
    }

    private String readGender() {
        while (true) {
            System.out.print("Gender (M/F/O): ");
            String gender = BookingInputValidator.genderFromCode(input.readLine().toUpperCase());
            if (gender != null) {
                return gender;
            }
            System.out.println("[Error] Enter M, F or O.");
        }
    }
}
