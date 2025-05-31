package ludoclub.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:ludoclub.db";

    // Tworzy tabele do zapisu wyników
    public static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS wyniki (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nick TEXT, " +
                "points INTEGER, " +
                "date TEXT" +
                ");";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Zapisuje wynik gracza do bazy
    public static void insertResult(String nick, int points, String date) {
        String sql = "INSERT INTO wyniki(nick, points, date) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nick);
            pstmt.setInt(2, points);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NOWOŚĆ: Pobiera ostatnie N wyników do wyświetlenia w tablicy wyników
    public static List<String> getLastResults(int limit) {
        List<String> wyniki = new ArrayList<>();
        String sql = "SELECT nick, points, date FROM wyniki ORDER BY id DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String nick = rs.getString("nick");
                int pts = rs.getInt("points");
                String date = rs.getString("date");
                wyniki.add(nick + " | miejsce: " + pts + " | data: " + date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wyniki;
    }
}