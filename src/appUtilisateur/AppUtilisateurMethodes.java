package appUtilisateur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AppUtilisateurMethodes {
    Scanner scanner = new Scanner(System.in);
    String url;
    Connection con;

    public AppUtilisateurMethodes() {
        // creer connection
        url = "jdbc:postgresql://localhost:5432/projet";
        con = null;

        try {
            con = DriverManager.getConnection(url, "postgres", "Zavo00041504349");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
