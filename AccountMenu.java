package menu;

import auth.AuthService;
import auth.SessionManager;
import model.User;
import model.Wallet;
import util.ConsolePrinter;
import util.CurrencyFormatter;
import util.InputReader;
import validation.UserValidator;
import wallet.WalletService;

/**
 * AccountMenu.java
 * ---------------------------------------------------------------
 * Dashboard option 5: "My Account".
 *   1. My Profile        2. My IRCTC Wallet
 *   3. Change Password   4. Logout
 *   0. Back to Dashboard
 *
 * Every option works on the user of the CURRENT SESSION
 * (SessionManager.getCurrentUser()), never on sample data.
 */
public class AccountMenu {

    private final SessionManager session;
    private final InputReader input;
    private final ConsolePrinter printer;
    private final AuthService authService;
    private final WalletService walletService;

    public AccountMenu(SessionManager session, InputReader input, ConsolePrinter printer,
                       AuthService authService, WalletService walletService) {
        this.session = session;
        this.input = input;
        this.printer = printer;
        this.authService = authService;
        this.walletService = walletService;
    }

    /**
     * Shows the My Account menu until the user goes back (0) or logs out.
     * After a logout the session is empty; the Dashboard checks this.
     */
    public void show() {
        while (session.isLoggedIn()) {
            printAccountMenu();
            int choice = input.readChoice();

            switch (choice) {
                case 1:
                    showProfile();
                    break;
                case 2:
                    showWallet();
                    break;
                case 3:
                    changePassword();
                    break;
                case 4:
                    confirmLogout();
                    break;              // the loop stops if the user logged out
                case 0:
                    return;             // back to the Dashboard
                default:
                    System.out.println("\n[Error] Invalid choice. Please enter a number from 0 to 4.");
            }
        }
    }

    private void printAccountMenu() {
        printer.printSectionHeader("MY ACCOUNT");
        System.out.println("  1. My Profile");
        System.out.println("  2. My IRCTC Wallet");
        System.out.println("  3. Change Password");
        System.out.println("  4. Logout");
        System.out.println("  0. Back to Dashboard");
        printer.printLine('-');
        System.out.print("Enter your choice: ");
    }

    // ======================= 1. MY PROFILE ====================

    /** Shows the logged-in user's details. The password is never shown. */
    private void showProfile() {
        User user = session.getCurrentUser();
        printer.printSectionHeader("MY PROFILE");
        printField("Full Name", user.getFullName());
        printField("Username", user.getUsername());
        printField("Mobile Number", user.getMobile());
        printField("Email Address", user.getEmail());
        // Accounts cannot be blocked in this project, so a logged-in account is always active.
        printField("Account Status", "Active");
        waitForReturn();
    }

    // ====================== 2. MY IRCTC WALLET ================

    /** Shows the wallet of the logged-in user only. */
    private void showWallet() {
        User user = session.getCurrentUser();
        Wallet wallet = walletService.getWallet(user);
        printer.printSectionHeader("MY IRCTC WALLET");
        printField("Username", user.getUsername());
        printField("Available Balance", CurrencyFormatter.formatRupees(wallet.getBalanceInPaise()));
        waitForReturn();
    }

    // ====================== 3. CHANGE PASSWORD ================

    /**
     * Changes the logged-in user's password:
     * current password -> new password (validated) -> confirm -> save.
     * Passwords are read with InputReader.readPassword(), which hides
     * the typing in a real terminal.
     */
    private void changePassword() {
        User user = session.getCurrentUser();
        printer.printSectionHeader("CHANGE PASSWORD");

        String current = input.readPassword("Enter Current Password: ");
        if (!authService.verifyPassword(user, current)) {
            System.out.println("[Error] Current password is incorrect. Password not changed.");
            waitForReturn();
            return;
        }

        String newPassword;
        while (true) {
            newPassword = input.readPassword("Enter New Password (0 to cancel): ");
            if (newPassword.equals("0")) {
                System.out.println("  Password change cancelled.");
                waitForReturn();
                return;
            }
            if (newPassword.isEmpty()) {
                System.out.println("[Error] Password cannot be empty.");
                continue;
            }
            if (newPassword.equals(current)) {
                System.out.println("[Error] New password must be different from the current password.");
                continue;
            }
            if (!UserValidator.isValidPassword(newPassword)) {
                System.out.println("[Error] Password must be at least 6 characters with a letter and a digit.");
                continue;
            }
            String confirm = input.readPassword("Confirm New Password: ");
            if (!confirm.equals(newPassword)) {
                System.out.println("[Error] Passwords do not match. Please try again.");
                continue;
            }
            break;
        }

        authService.changePassword(user, current, newPassword);
        System.out.println();
        System.out.println("  Password changed successfully.");
        System.out.println("  Use your new password the next time you log in.");
        waitForReturn();
    }

    // ========================== 4. LOGOUT =====================

    /** Asks for confirmation; on "Yes" the session is ended. */
    private void confirmLogout() {
        System.out.println();
        System.out.println("  Are you sure you want to logout?");
        System.out.println("  1. Yes");
        System.out.println("  2. No");
        int answer = input.readNumber("Enter your choice: ", 1, 2);
        if (answer == 1) {
            session.endSession();      // Dashboard sees this and returns to the welcome screen
        } else {
            System.out.println("  Logout cancelled.");
        }
    }

    // ========================== HELPERS =======================

    private void printField(String label, String value) {
        System.out.printf("  %-17s : %s%n", label, value);
    }

    /** "0. Return to My Account" at the end of each option. */
    private void waitForReturn() {
        System.out.println();
        printer.printLine('-');
        System.out.println("  0. Return to My Account");
        input.waitForZero("Enter your choice: ", "[Error] Please enter 0 to return to My Account.");
    }
}
