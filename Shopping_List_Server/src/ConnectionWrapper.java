import jdk.internal.org.objectweb.asm.Type;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Christopher on 7/30/2016.
 */
public class ConnectionWrapper {
    private static Connection db;
    ConnectionWrapper(Connection conn){
        db = conn;
    }
    public PreparedSelectStatement prepareSelectStatement(String sql) throws SQLException{
        return new PreparedSelectStatement(db.prepareStatement(sql));
    }
    public PreparedUpdateStatement prepareUpdateStatement(String sql) throws SQLException{
        return new PreparedUpdateStatement(db.prepareStatement(sql));
    }
    public Array createArrayOf(Object[] array, Type typeOfArray) throws SQLException {
        return db.createArrayOf(typeOfArray.toString(), array);
    }
    public boolean isClosed() throws SQLException {
        return db.isClosed();
    }
    public void close() throws SQLException {
        db.close();
    }
}
