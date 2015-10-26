/**
 * Created by Christopher on 9/1/2015.
 */

import java.sql.*;

public class database implements AutoCloseable {
    Connection db = null;
    static final String databaseName = "shopping_list_database";
    static final String url = "jdbc:postgresql://localhost:5432/";
    static final String dbUrl = url+databaseName;
    static final String username = "postgres";
    static final String password = "password";
    static final String createDatabase = "CREATE DATABASE " + databaseName;

    public database() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        try {
            /*db = DriverManager.getConnection(url, username, password);
            createDatabase();
            db = DriverManager.getConnection(dbUrl, username, password);
            createTables();*/
        }
        catch (Exception ex)
        {
            //db may exist
        }
        finally {
            db = DriverManager.getConnection(dbUrl, username, password);
        }
    }
    private void createDatabase() throws SQLException {
        try(PreparedStatement stmt = db.prepareStatement(createDatabase)){
            stmt.executeUpdate();
        } catch (Exception ex){}
    }
    private void createTables() throws SQLException {
        createStoresTable();
        createItemsTable();
    }
    private void createStoresTable() throws SQLException {
        try(PreparedStatement stmt = db.prepareStatement(databaseQueries.CREATE_STORE);) {
            stmt.executeUpdate();
        } catch (Exception ex){}
    }
    private void createItemsTable() throws SQLException {
        try (PreparedStatement stmt = db.prepareStatement(databaseQueries.CREATE_ITEM)) {
            stmt.executeUpdate();
        } catch (Exception ex){}
    }

    public void updateTableQuery(String query) throws SQLException {
        try(PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (Exception ex){}
    }

    public PreparedStatement selectTableQuery(String query) throws SQLException {
        PreparedStatement stmt = db.prepareStatement(query);
        return stmt;
    }

    @Override
    public void close() throws Exception {
        while(!db.isClosed()){
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
