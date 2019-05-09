import java.sql.*;
import java.util.Scanner;

public class Main {


    private static Connection openDatabase(String dbFile) {

        Connection db = null;

        try {
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile);
            System.out.println("Database connection successfully established.");
        } catch (Exception exception) {
            System.out.println("Database connection error: " + exception.getMessage());
        }

        return db;

    }

    private static void closeDatabase(Connection db) {

        try {
            db.close();
            System.out.println("Disconnected from database.");
        } catch (Exception exception) {
            System.out.println("Database disconnection error: " + exception.getMessage());
        }

    }

    private static void listThings(Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("SELECT Id, Name, Quantity FROM Things");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int id = results.getInt(1);
                String name = results.getString(2);
                int quantity = results.getInt(3);
                System.out.print("Id: " + id + ",  ");
                System.out.print("Name: " + name + ",  ");
                System.out.print("Quantity: " + quantity + "\n");
            }

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }


    }

    private static void insertThing(int id, String name, int quantity, Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("INSERT INTO Things (Id, Name, Quantity) VALUES (?, ?, ?)");

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, quantity);

            ps.execute();

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }
    }

    private static void updateThing(int id, String name, int quantity, Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("UPDATE Things SET Name = ?, Quantity = ? WHERE Id = ?");

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, id);

            ps.execute();

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }
    }

    private static void deleteThing(int id, Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("DELETE FROM Things WHERE Id = ?");

            ps.setInt(1, id);

            ps.execute();

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }
    }

    enum Mode {
        MENU,
        SELECT,
        INSERT,
        UPDATE,
        DELETE,
        QUIT
    }

    public static void main(String[] args) {

        Connection myDB = openDatabase("Test.db");
        Scanner scanner = new Scanner(System.in);
        Mode mode = Mode.MENU;

        while (mode != Mode.QUIT) {

            System.out.println();

            switch (mode) {

                case MENU:

                    System.out.println("Please choose an option:");
                    System.out.println("L - List all the things.");
                    System.out.println("A - Add a new thing.");
                    System.out.println("E - Edit an existing thing.");
                    System.out.println("D - Delete an existing thing.");
                    System.out.println("Q - Quit.");

                    String choice = scanner.next();
                    if (choice.toUpperCase().equals("L")) {
                        mode = Mode.SELECT;
                    } else if (choice.toUpperCase().equals("A")) {
                        mode = Mode.INSERT;
                    } else if (choice.toUpperCase().equals("E")) {
                        mode = Mode.UPDATE;
                    } else if (choice.toUpperCase().equals("D")) {
                        mode = Mode.DELETE;
                    } else if (choice.toUpperCase().equals("Q")) {
                        mode = Mode.QUIT;
                    } else {
                        System.out.println("Unrecognised choice!");
                    }

                    break;

                case SELECT:

                    listThings(myDB);

                    mode = Mode.MENU;
                    break;

                case INSERT:

                    System.out.print("New thing id? ");
                    String insertId = scanner.next();
                    System.out.print("New name? ");
                    String insertThing = scanner.next();
                    System.out.print("New quantity? ");
                    String insertQuantity = scanner.next();

                    try {
                        insertThing(Integer.parseInt(insertId), insertThing, Integer.parseInt(insertQuantity), myDB);
                    } catch (NumberFormatException ne) {
                        System.out.println("Number format error: " + ne.getMessage());
                    }

                    mode = Mode.MENU;
                    break;

                case UPDATE:

                    System.out.print("Id of thing to edit? ");
                    String updateId = scanner.next();
                    System.out.print("Updated name? ");
                    String updateThing = scanner.next();
                    System.out.print("Updated quantity? ");
                    String updateQuantity = scanner.next();

                    try {
                        updateThing(Integer.parseInt(updateId), updateThing, Integer.parseInt(updateQuantity), myDB);
                    } catch (NumberFormatException ne) {
                        System.out.println("Number format error: " + ne.getMessage());
                    }

                    mode = Mode.MENU;
                    break;

                case DELETE:

                    System.out.print("Id of thing to delete? ");
                    String deleteId = scanner.next();

                    try {
                        deleteThing(Integer.parseInt(deleteId), myDB);
                    } catch (NumberFormatException ne) {
                        System.out.println("Number format error: " + ne.getMessage());
                    }

                    mode = Mode.MENU;
                    break;

            }

        }

        System.out.println("Goodbye!");

        closeDatabase(myDB);

    }

}

