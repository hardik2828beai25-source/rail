package menu;

import java.util.Scanner;

import auth.AuthService;
import auth.SessionManager;
import model.User;
import service.TrainService;
import util.InputReader;
import wallet.WalletService;

/**
 * LoginSystem.java
 * ---------------------------------------------------------------
 * User-interface class for the welcome screen. It shows the menu,
 * handles LOGIN itself and sends REGISTRATION to RegistrationScreen.
 * All real work (saving users, checking passwords) is delegated
 * to AuthService.
 */
public class LoginSystem {

    private static final int MAX_ATTEMPTS = 3;

    // ONE InputReader (one Scanner) for the whole program
    private final InputReader input = new InputReader(new Scanner(System.in));
    private final WalletService walletService = new WalletService();
    private final AuthService authService = new AuthService(walletService);
    // Remembers who is logged in; emptied on logout
    private final SessionManager session = new SessionManager();
    // ONE TrainService shared by all users (trains, seats and bookings live here)
    private final TrainService trainService = new TrainService();
    private final RegistrationScreen registrationScreen = new RegistrationScreen(input, authService);

    // ======================== MAIN LOOP ========================

    /** Shows the welcome screen again and again until the user exits. */
    public void start() {
        boolean running = true;

        while (running) {
            showWelcomeScreen();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    registrationScreen.handleRegistration();
                    break;
                case 3:
                    System.out.println("\nThank you for using IRCTC. Happy Journey!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[Error] Invalid choice. Please enter 1, 2 or 3.");
            }
        }
        input.close();
    }

    // ====================== WELCOME SCREEN =====================

    /** Prints the banner and the menu. */
    private void showWelcomeScreen() {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("        IRCTC RAILWAY BOOKING SYSTEM");
        System.out.println("==============================================");
        System.out.println("          Your Journey, Our Priority!");
        System.out.println("----------------------------------------------");
        System.out.println("  1. Login");
        System.out.println("  2. Create New Account");
        System.out.println("  3. Exit");
        System.out.println("----------------------------------------------");
    }

    /** Reads the menu choice safely (-1 for letters or empty input). */
    private int readMenuChoice() {
        System.out.print("Enter your choice: ");
        return input.readChoice();
    }

    // ========================== LOGIN ==========================

    /** Asks for credentials. Allows a maximum of 3 attempts. */
    private void handleLogin() {
        System.out.println("\n----------------- LOGIN -----------------");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("Username: ");
            String username = input.readLine();
            System.out.print("Password: ");
            String password = input.readRawLine();

            User loggedInUser = authService.login(username, password);

            if (loggedInUser != null) {
                onLoginSuccess(loggedInUser);
                return;                         // leave the login method
            }

            int remaining = MAX_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("[Error] Invalid username or password. "
                        + remaining + " attempt(s) left.\n");
            }
        }

        // Reached only when all 3 attempts failed.
        System.out.println("[Error] Too many failed attempts. Returning to the welcome screen.");
    }

    /**
     * Called after a successful login.
     * Starts the session for the logged-in User and creates the
     * Dashboard with the SAME InputReader and services.
     * displayDashboard() keeps running until the user logs out (the
     * session becomes empty); then this method ends and the welcome
     * screen appears again.
     */
    private void onLoginSuccess(User user) {
        System.out.println("\nLogin Successful! Welcome to IRCTC.");
        session.startSession(user);
        Dashboard dashboard = new Dashboard(session, input, trainService, authService, walletService);
        dashboard.displayDashboard();
    }
}
