package menu;

import auth.AuthService;
import util.InputReader;
import validation.UserValidator;

/**
 * RegistrationScreen.java
 * ---------------------------------------------------------------
 * The "Create New Account" screen. Asks for every detail, repeats
 * each question until the answer is valid (UserValidator), then
 * registers the user through AuthService (which hashes the password
 * and creates the user's IRCTC Wallet).
 */
public class RegistrationScreen {

    private final InputReader input;
    private final AuthService authService;

    public RegistrationScreen(InputReader input, AuthService authService) {
        this.input = input;
        this.authService = authService;
    }

    /** Collects and validates all details, then registers the user. */
    public void handleRegistration() {
        System.out.println("\n------------- CREATE NEW ACCOUNT -------------");

        String fullName = readFullName();
        String username = readUsername();
        String password = readPassword();
        String mobile = readMobile();
        String email = readEmail();

        // AuthService hashes the password and creates the user's wallet.
        if (authService.registerUser(fullName, username, password, mobile, email)) {
            System.out.println("\nRegistration successful! You can now log in.");
        } else {
            // Safety net; the username was already checked in readUsername().
            System.out.println("\n[Error] Registration failed. Username already exists.");
        }
    }

    // ---- One input method per field: each repeats until valid ----

    private String readFullName() {
        while (true) {
            System.out.print("Full Name: ");
            String name = input.readLine();
            if (UserValidator.isValidName(name)) {
                return name;
            }
            System.out.println("[Error] Name must have at least 3 letters (letters and spaces only).");
        }
    }

    private String readUsername() {
        while (true) {
            System.out.print("Username (4-15 chars, letters/digits/_): ");
            String username = input.readLine();
            if (!UserValidator.isValidUsername(username)) {
                System.out.println("[Error] Username must be 4-15 characters: letters, digits or underscore.");
            } else if (authService.isUsernameTaken(username)) {
                System.out.println("[Error] This username is already taken. Try another.");
            } else {
                return username;
            }
        }
    }

    private String readPassword() {
        while (true) {
            System.out.print("Password (min 6 chars, letter + digit): ");
            String password = input.readRawLine();
            if (UserValidator.isValidPassword(password)) {
                return password;
            }
            System.out.println("[Error] Password must be at least 6 characters with a letter and a digit.");
        }
    }

    private String readMobile() {
        while (true) {
            System.out.print("Mobile Number (10 digits): ");
            String mobile = input.readLine();
            if (UserValidator.isValidMobile(mobile)) {
                return mobile;
            }
            System.out.println("[Error] Enter a valid 10-digit mobile number starting with 6, 7, 8 or 9.");
        }
    }

    private String readEmail() {
        while (true) {
            System.out.print("Email Address: ");
            String email = input.readLine();
            if (UserValidator.isValidEmail(email)) {
                return email;
            }
            System.out.println("[Error] Enter a valid email address (e.g. name@example.com).");
        }
    }
}
