import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Christopher on 7/30/2016.
 */
public class PreparedSelectStatement implements AutoCloseable {
    PreparedStatement stmt;
    PreparedSelectStatement(PreparedStatement preparedStatement){
        stmt = preparedStatement;
    }

    public ResultSet executeQuery() throws SQLException {
        return stmt.executeQuery();
    }

    public void setInt(Integer index, Integer value) throws SQLException {
        stmt.setInt(index, value);
    }

    public void setString(Integer index, String value) throws SQLException {
        stmt.setString(index, value);
    }

    public void setObject(Integer index, Object value) throws SQLException {
        stmt.setObject(index, value);
    }

    public void setArray(Integer index, Array value) throws SQLException {
        stmt.setArray(index, value);
    }

    @Override
    public void close() throws Exception {
        try {
            stmt.close();
        } catch(Exception ex) {
            stmt.close();
            System.out.println("Db failed to close");
            System.out.println(ex);
        }
    }
}
