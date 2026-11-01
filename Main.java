import java.util.*;
import java.io.*;

class Expense {
    String payer;
    double amount;
    String description;

    Expense(String payer, double amount, String description) {
        this.payer = payer;
        this.amount = amount;
        this.description = description;
    }

    void display() {
        System.out.println(
            payer + " paid Rs. " + amount +
            " for " + description
        );
    }
}

public class Main {

    static ArrayList<Expense> expenses = new ArrayList<>();

    static String[] members = {"Rahul", "Amit", "Priya"};

    static final String FILE_NAME = "expenses.csv";

    static void loadExpenses() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {

            if (reader.hasNextLine()) {
                reader.nextLine();
            }

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split(",", 3);

                if (data.length == 3) {
                    try {
                        String payer = data[0];
                        double amount = Double.parseDouble(data[1]);
                        String description = data[2];

                        if (isMember(payer) && amount > 0) {
                            expenses.add(
                                new Expense(payer, amount, description)
                            );
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid expense.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading expenses!");
        }
    }

    static void saveExpenses() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {

            writer.write("Payer,Amount,Description\n");

            for (Expense e : expenses) {
                writer.write(
                    e.payer + "," +
                    e.amount + "," +
                    e.description + "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving expenses!");
        }
    }

    static boolean isMember(String name) {
        for (String member : members) {
            if (member.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    static void addExpense(Scanner sc) {
        System.out.print("Enter payer name: ");
        String payer = sc.nextLine().trim();

        if (!isMember(payer)) {
            System.out.println("Member not found!");
            return;
        }

        System.out.print("Enter amount: ");

        double amount;

        try {
            amount = Double.parseDouble(sc.nextLine());

            if (!Double.isFinite(amount) || amount <= 0) {
                System.out.println("Enter a valid positive amount!");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount!");
            return;
        }

        System.out.print("Enter description: ");
        String description = sc.nextLine().trim();

        if (description.isEmpty() || description.contains(",")) {
            System.out.println("Invalid description!");
            return;
        }

        expenses.add(new Expense(payer, amount, description));

        saveExpenses();

        System.out.println("Expense added successfully!");
    }

    static void showExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses available.");
            return;
        }

        System.out.println("\n--- Group Expenses ---");

        for (Expense e : expenses) {
            e.display();
        }
    }

    static void showSummary() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses available.");
            return;
        }

        double total = 0;
        double[] paid = new double[members.length];

        for (Expense e : expenses) {
            total += e.amount;

            for (int i = 0; i < members.length; i++) {
                if (members[i].equalsIgnoreCase(e.payer)) {
                    paid[i] += e.amount;
                }
            }
        }

        double share = total / members.length;

        System.out.println("\n--- Expense Summary ---");
        System.out.printf("Total Expense: Rs. %.2f%n", total);
        System.out.printf("Each Person's Share: Rs. %.2f%n", share);

        for (int i = 0; i < members.length; i++) {
            double balance = paid[i] - share;

            System.out.printf("%s: Paid Rs. %.2f | ", members[i], paid[i]);

            if (balance > 0.001) {
                System.out.printf("Gets back Rs. %.2f%n", balance);
            }
            else if (balance < -0.001) {
                System.out.printf("Owes Rs. %.2f%n", -balance);
            }
            else {
                System.out.println("Settled");
            }
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        loadExpenses();

        while (true) {
            System.out.println("\n===== GROUP EXPENSE SPLITTER =====");
            System.out.println("1. Add Expense");
            System.out.println("2. Show All Expenses");
            System.out.println("3. Expense Summary");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    addExpense(sc);
                    break;

                case "2":
                    showExpenses();
                    break;

                case "3":
                    showSummary();
                    break;

                case "4":
                    System.out.println("Program closed.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
