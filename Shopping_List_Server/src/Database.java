/**
 * Created by Christopher on 9/1/2015.
 */

import jdk.internal.org.objectweb.asm.Type;

import java.sql.*;

public class Database implements AutoCloseable {
    private static final String databaseName = "shopping_list_database";
    private static final String url = "jdbc:postgresql://localhost:5432/";
    private static final String dbUrl = url + databaseName;
    private static final String username = "postgres";
    private static final String password = "password";
    private static final String createDatabase = "CREATE DATABASE " + databaseName;
    private static ConnectionWrapper db;

    public static String ItemIdAndType = "ItemId INT";
    public static String ItemNameAndType = "ItemName VARCHAR(50)";
    public static String BestPriceAndType = "BestPrice REAL";
    public static String ItemStatusAndType = "ItemStatus VARCHAR(8)";

    public static String StoreIdAndType = "StoreId INT";
    public static String StoreNameAndType = "StoreName VARCHAR(50)";

    public static String SessionIdAndType = "SessionId UUID";
    public static String SessionPhoneNumberAndType = "SessionPhoneNumber VARCHAR(12)";
    public static String SessionAuthCode = "SessionAuthCode VARCHAR(6)";

    public static String ShoppingListIdAndType = "ShoppingListId INT";
    public static String ShoppingListNameAndType = "ShoppingListName VARCHAR(50)";

    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
        } catch (Exception ex) {
            connection = DriverManager.getConnection(url, username, password);
            createDatabase();
            connection = DriverManager.getConnection(dbUrl, username, password);
            createTables();
        }
        db = new ConnectionWrapper(connection);
    }

    private void createDatabase() throws SQLException {
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(createDatabase)) {
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
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(DatabaseQueries.CREATE_SESSION)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
        for(String sql : SessionStoredProcedures.StoredProcedures){
            try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(sql)){
                stmt.executeUpdate();
            }catch (final Exception ignored) {
            }
        }
    }

    private void createShoppingListTable() throws SQLException {
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(DatabaseQueries.CREATE_SHOPPING_LISTS)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
        for(String sql : ShoppingListStoredProcedures.StoredProcedures){
            try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(sql)){
                stmt.executeUpdate();
            }catch (final Exception ignored) {
            }
        }
    }

    private void createStoresTable() throws SQLException {
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(DatabaseQueries.CREATE_STORE)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
        for(String sql : StoreStoredProcedures.StoredProcedures){
            try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(sql)){
                stmt.executeUpdate();
            }catch (final Exception ignored) {
            }
        }
    }

    private void createItemsTable() throws SQLException {
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(DatabaseQueries.CREATE_ITEM)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
        for(String sql : ItemStoredProcedures.StoredProcedures){
            try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(sql)){
                stmt.executeUpdate();
            }catch (final Exception ignored) {
            }
        }
    }

    private void createShoppingListItemsTables() throws SQLException {
        try (final PreparedUpdateStatement stmt = db.prepareUpdateStatement(DatabaseQueries.CREATE_SHOPPING_LIST_ITEMS)) {
            stmt.executeUpdate();
        } catch (final Exception ignored) {
        }
    }

    public PreparedUpdateStatement updateTableQuery(final String query) throws SQLException {
        return db.prepareUpdateStatement(query);
    }

    public PreparedSelectStatement selectTableQuery(final String query) throws SQLException {
        return db.prepareSelectStatement(query);
    }

    public Array CreateArray(final Object[] array, final Type arrayObjectTypes) throws SQLException {
        return db.createArrayOf(array, arrayObjectTypes);
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
