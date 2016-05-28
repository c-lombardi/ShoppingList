/**
 * Created by Christopher on 9/1/2015.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database implements AutoCloseable {
    private static final String databaseName = "shopping_list_database";
    private static final String url = "jdbc:postgresql://localhost:5432/";
    private static final String dbUrl = url + databaseName;
    private static final String username = "postgres";
    private static final String password = "password";
    private static final String createDatabase = "CREATE DATABASE " + databaseName;
    private static Connection db = null;

    public static final String SessionsTableName = "Sessions";
    public static final String ItemsTableName = "Items";
    public static final String ShoppingListsTableName = "Shopping_Lists";
    public static final String ShoppingListItemsTableName = "ShoppingListItems";
    public static final String StoresTableName = "Stores";

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
        createShoppingListItemsTables();
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

    private void createShoppingListItemsTables() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(DatabaseQueries.CREATE_SHOPPING_LIST_ITEMS)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    public void updateTableQuery(final String query) throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
            System.out.println("");
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
