/**
 * Created by Christopher on 9/1/2015.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database implements AutoCloseable {
    static final String databaseName = "shopping_list_database";
    static final String url = "jdbc:postgresql://localhost:5432/";
    static final String dbUrl = url + databaseName;
    static final String username = "postgres";
    static final String password = "password";
    static final String createDatabase = "CREATE DATABASE " + databaseName;
    Connection db = null;

    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        try {
            db = DriverManager.getConnection(dbUrl, username, password);
        } catch (Exception ex) {
            db = DriverManager.getConnection(url, username, password);
            createDatabase();
            db = DriverManager.getConnection(dbUrl, username, password);
            createTables();
        }
    }

    private void createDatabase() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(createDatabase)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
        }
    }

    private void createTables() throws SQLException {
        createSessionsTable();
        createStoresTable();
        createItemsTable();
    }

    private void createSessionsTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_SESSION)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
        }
    }

    private void createStoresTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_STORE)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
        }
    }

    private void createItemsTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_ITEM)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
        }
    }

    public void updateTableQuery(final String query) throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
        }
    }

    public PreparedStatement selectTableQuery(final String query) throws SQLException {
        PreparedStatement stmt = db.prepareStatement(query);
        return stmt;
    }

    @Override
    public void close() throws Exception {
        while (!db.isClosed()) {
            try {
                db.close();
            } catch (Exception ex) {
                db.close();
                System.out.println("Db failed to close");
                System.out.println(ex);
            }
        }
    }
}
