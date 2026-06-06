import java.sql.*;
import java.util.Scanner;

public class SaaSDBManager {
    static final String DB_URL = "jdbc:mysql://localhost:3306/new_data";
    static final String USER = "root";
    static final String PASS = "12345";
    static final Scanner sc = new Scanner(System.in);

    static void insertData() {
        System.out.println("Enter the tool name: ");
        String name = sc.nextLine();

        System.out.println("Enter the department ID: ");
        int deptId = Integer.parseInt(sc.nextLine());

        System.out.println("Enter monthly cost: ");
        double cost = Double.parseDouble(sc.nextLine());

        System.out.print("Enter Billing Cycle (Monthly / Annually): ");
        String billing = sc.nextLine();
        
        System.out.print("Enter Next Renewal Date (YYYY-MM-DD): ");
        String renewalDate = sc.nextLine();
        
        System.out.print("Enter Status (Active / Trial / Paused): ");
        String status = sc.nextLine();
        
        System.out.print("Enter Employee Seat Count: ");
        int seats = Integer.parseInt(sc.nextLine());

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String STMT = "INSERT INTO subscription(name, dept_id, monthly_cost, billing_cycle, renewal_date, status, seat_count) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(STMT);

            stmt.setString(1, name);
            stmt.setInt(2, deptId);
            stmt.setDouble(3, cost);
            stmt.setString(4, billing);
            stmt.setString(5, renewalDate);
            stmt.setString(6, status); 
            stmt.setInt(7, seats);

            stmt.executeUpdate();
            System.out.println("Subscription record inserted successfully");

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error during insertion: " + e.getMessage());            
        }
    }

    static void readData() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            String sql = "SELECT s.subs_id, s.name, d.dept_name, s.monthly_cost, s.billing_cycle, s.renewal_date, s.status, s.seat_count " +
                         "FROM subscription s JOIN department d ON s.dept_id = d.dept_id";
            ResultSet rs = stmt.executeQuery(sql);
            
            printResultSet(rs);

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error during reading: " + e.getMessage());
        }
    }

    // NEW METHOD: Search tool by name using PreparedStatement to prevent SQL injection
    static void searchData() {
        System.out.print("\nEnter the Tool Name (or partial name) to search for: ");
        String searchQuery = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "SELECT s.subs_id, s.name, d.dept_name, s.monthly_cost, s.billing_cycle, s.renewal_date, s.status, s.seat_count " +
                         "FROM subscription s JOIN department d ON s.dept_id = d.dept_id " +
                         "WHERE s.name LIKE ?";
                         
            PreparedStatement stmt = conn.prepareStatement(sql);
            // Using % wildcards so it matches partial text (e.g., "slack" matches "Slack Pro")
            stmt.setString(1, "%" + searchQuery + "%");
            
            ResultSet rs = stmt.executeQuery();
            printResultSet(rs);

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    static void updateData() {
        System.out.print("\nEnter the Subscription ID (subs_id) you want to update: ");
        int id = Integer.parseInt(sc.nextLine());
        
        System.out.print("Enter New Monthly Cost: ");
        double newCost = Double.parseDouble(sc.nextLine());
        
        System.out.print("Enter New Seat Count: ");
        int newSeats = Integer.parseInt(sc.nextLine());
        
        System.out.print("Enter New Status (Active / Trial / Paused): ");
        String newStatus = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String STMT = "UPDATE subscription SET monthly_cost = ?, seat_count = ?, status = ? WHERE subs_id = ?";
            PreparedStatement stmt = conn.prepareStatement(STMT);
            
            stmt.setDouble(1, newCost);
            stmt.setInt(2, newSeats);
            stmt.setString(3, newStatus);
            stmt.setInt(4, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Subscription records updated!");
            } else {
                System.out.println("No subscription found with that ID.");
            }
            
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error during update: " + e.getMessage());
        }
    }

    static void deleteData() {
        System.out.print("\nEnter the Subscription ID (subs_id) you want to delete: ");
        int id = Integer.parseInt(sc.nextLine());

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String STMT = "DELETE FROM subscription WHERE subs_id = ?";
            PreparedStatement stmt = conn.prepareStatement(STMT);
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tool removed from corporate system records.");
            } else {
                System.out.println("No tool found with that ID.");
            }
            
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error during deletion: " + e.getMessage());
        }
    }

    // Helper method to eliminate duplicate printing logic between Read and Search
    private static void printResultSet(ResultSet rs) throws SQLException {
        System.out.println("\nID\t|\tTool Name\t|\tDepartment\t|\tCost\t|\tStatus\t|\tSeats");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        boolean hasRecords = false;
        while (rs.next()) {
            hasRecords = true;
            int id = rs.getInt("subs_id"); 
            String name = rs.getString("name");
            String deptName = rs.getString("dept_name");
            double cost = rs.getDouble("monthly_cost");
            String status = rs.getString("status");
            int seats = rs.getInt("seat_count");
            
            String output = id + "\t|\t" + name + "\t\t|\t" + deptName + "\t|\t$" + cost + "\t|\t" + status + "\t|\t" + seats;
            System.out.println(output);
        }
        
        if (!hasRecords) {
            System.out.println("No matching records found.");
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n::: CORPORATE SAAS & SUBSCRIPTION AUDITOR MENU :::");
            System.out.println("1. Add New Tool (CREATE)");
            System.out.println("2. View All Subscriptions (READ)");
            System.out.println("3. Search Subscriptions (SEARCH)");
            System.out.println("4. Modify Subscription Parameters (UPDATE)");
            System.out.println("5. Delete Subscription (DELETE)");
            System.out.println("6. Exit System");
            System.out.print("Select an option (1-6): ");
            
            String input = sc.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    insertData();
                    break;
                case 2:
                    readData();
                    break;
                case 3:
                    searchData();
                    break;
                case 4:
                    updateData();
                    break;
                case 5:
                    deleteData();
                    break;
                case 6:
                    System.out.println("Exiting SaaS Audit System. Goodbye!");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid selection! Please enter a number between 1 and 6.");
            }
        }
    }
}
