package library;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() throws LibraryException {

        try {

            Class.forName(SharedSpec.DB_DRIVER);

            String sourceURL = SharedSpec.DB_URL;
            String user = SharedSpec.DB_USER;
            String password = SharedSpec.DB_PASSWORD;

            Connection databaseConnection =
                DriverManager.getConnection(sourceURL, user, password);

            return databaseConnection;

        } catch (ClassNotFoundException e) {

            throw new LibraryException("MySQL driver not found.", e);

        } catch (SQLException e) {

            throw new LibraryException("Cannot connect to MySQL.", e);
        }
    }

    private DBConnection() { }
}