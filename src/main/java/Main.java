import java.sql.*;
import java.util.Scanner;

public class Main {


    private static Connection openDatabase(String dbFile)
    {
        Connection db = null;

        try
        {
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile);
            System.out.println("Database connection successfully established.");
        }
        catch (Exception exception)
        {
            System.out.println("Database connection error: " + exception.getMessage());
        }

        return db;

    }

    private static void closeDatabase(Connection db)
    {
        try {
            db.close();
            System.out.println("Disconnected from database.");
        }
        catch (Exception exception)
        {
            System.out.println("Database disconnection error: " + exception.getMessage());
        }
    }

    private static void listThings(Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("SELECT Id, Thing, Quantity FROM Random");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int id = results.getInt(1);
                String thing = results.getString(2);
                int quantity = results.getInt(3);
                System.out.println(id + " : " + thing + " x " + quantity);
            }

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }


    }

    private static void insertThing(int id, String thing, int quantity, Connection db) {

        try {

            PreparedStatement ps = db.prepareStatement("INSERT INTO Random (Id, Thing, Quantity) VALUES (?, ?, ?)");

            ps.setInt(1, id);
            ps.setString(2, thing);
            ps.setInt(3, quantity);

            ps.execute();

        } catch (SQLException sqlEx) {
            System.out.println("Database error: " + sqlEx.getMessage());
        }
    }

    enum Mode {
        MENU,
        LIST,
        INSERT,
        EXIT
    }

    public static void main(String[] args) {

        Connection myDB = openDatabase("Test.db");
        Scanner scanner = new Scanner(System.in);
        Mode mode = Mode.MENU;

        while (mode != Mode.EXIT) {

            System.out.println();

            switch (mode) {

                case MENU:

                    System.out.println("Please choose an option:");
                    System.out.println("L - List all the things.");
                    System.out.println("I - Insert a new thing.");
                    System.out.println("E - Exit.");

                    String choice = scanner.next();
                    if (choice.toUpperCase().equals("L")) {
                        mode = Mode.LIST;
                    } else if (choice.toUpperCase().equals("I")) {
                        mode = Mode.INSERT;
                    } else if (choice.toUpperCase().equals("E")) {
                        mode = Mode.EXIT;
                    } else {
                        System.out.println("Unrecognised choice!");
                    }

                    break;

                case LIST:

                    listThings(myDB);

                    mode = Mode.MENU;
                    break;

                case INSERT:

                    System.out.print("New thing id? ");
                    String id = scanner.next();
                    System.out.print("New thing name? ");
                    String thing = scanner.next();
                    System.out.print("New quantity? ");
                    String quantity = scanner.next();

                    try {
                        insertThing(Integer.parseInt(id), thing, Integer.parseInt(quantity), myDB);
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
