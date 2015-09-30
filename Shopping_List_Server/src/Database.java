/**
 * Created by Christopher on 9/1/2015.
 */

import java.sql.*;

public class Database implements AutoCloseable {
    Connection db = null;
    PreparedStatement stmt;
    static final String databaseName = "shopping_list_database";
    static final String url = "jdbc:postgresql://localhost:5432/";
    static final String dbUrl = url+databaseName;
    static final String username = "postgres";
    static final String password = "password";
    static final String createDatabase = "CREATE DATABASE " + databaseName;

    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        try {
            db = DriverManager.getConnection(url, username, password);
            CreateDatabase();
            db = DriverManager.getConnection(dbUrl, username, password);
            CreateTables();
        }
        catch (Exception ex)
        {
            //db may exist
        }
        finally {
            db = DriverManager.getConnection(dbUrl, username, password);
        }
    }
    private void CreateDatabase() throws SQLException {
        stmt = db.prepareStatement(createDatabase);
        stmt.executeUpdate();
        stmt.close();
    }
    private void CreateTables() throws SQLException {
        CreateStoresTable();
        CreateItemsTable();
    }
    private void CreateStoresTable() throws SQLException {
        stmt = db.prepareStatement(DatabaseQueries.createStore);
        stmt.executeUpdate();
        stmt.close();
    }
    private void CreateItemsTable() throws SQLException {
        stmt = db.prepareStatement(DatabaseQueries.createItem);
        stmt.executeUpdate();
        stmt.close();
    }

    public void UpdateTableQuery(String query) throws SQLException {
        stmt = db.prepareStatement(query);
        stmt.executeUpdate();
    }

    public ResultSet SelectTableQuery(String query) throws SQLException {
        stmt = db.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        return rs;
    }

    @Override
    public void close() throws Exception {
        if(!db.isClosed())
            db.close();
    }
}
