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
        } catch (final Exception ignored) {
        }
    }

    private void createTables() throws SQLException {
        createSessionsTable();
        createStoresTable();
        createShoppingListTable();
        createItemsTable();
    }

    private void createSessionsTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_SESSION)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    private void createShoppingListTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_SHOPPING_LISTS)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    private void createStoresTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_STORE)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    private void createItemsTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_ITEM)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    public void updateTableQuery(final String query) throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    public PreparedStatement selectTableQuery(final String query) throws SQLException {
        return db.prepareStatement(query);
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
