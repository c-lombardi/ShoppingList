/**
 * Created by Christopher on 9/1/2015.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class database implements AutoCloseable {
    Connection db = null;
    PreparedStatement stmt;
    static final String databaseName = "shopping_list_database";
    static final String url = "jdbc:postgresql://localhost:5432/";
    static final String dbUrl = url+databaseName;
    static final String username = "postgres";
    static final String password = "password";
    static final String createDatabase = "CREATE DATABASE " + databaseName;
    private static List<Statement> statementsToClose;

    public database() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        statementsToClose = new ArrayList<>();
        try {
            db = DriverManager.getConnection(url, username, password);
            createDatabase();
            db = DriverManager.getConnection(dbUrl, username, password);
            createTables();
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
        stmt = db.prepareStatement(createDatabase);
        stmt.executeUpdate();
        stmt.close();
    }
    private void createTables() throws SQLException {
        createStoresTable();
        createItemsTable();
    }
    private void createStoresTable() throws SQLException {
        stmt = db.prepareStatement(databaseQueries.CREATE_STORE);
        stmt.executeUpdate();
        stmt.close();
    }
    private void createItemsTable() throws SQLException {
        stmt = db.prepareStatement(databaseQueries.CREATE_ITEM);
        stmt.executeUpdate();
        stmt.close();
    }

    public void updateTableQuery(String query) throws SQLException {
        stmt = db.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
    }

    public ResultSet selectTableQuery(String query) throws SQLException {
        stmt = db.prepareStatement(query);
        final ResultSet rs = stmt.executeQuery();
        statementsToClose.add(stmt);
        return rs;
    }

    @Override
    public void close() throws Exception {
        while(!db.isClosed()){
            try {
                for(Statement s : statementsToClose) {
                    try {
                        s.close();
                    } catch (Exception ex){}
                }
                db.close();
            } catch (Exception ex) {
                db.close();
                System.out.println("Db failed to close");
                System.out.println(ex);
            }
        }
    }
}
