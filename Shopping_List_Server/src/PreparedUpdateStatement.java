import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Christopher on 7/30/2016.
 */
public class PreparedUpdateStatement implements AutoCloseable {
    private PreparedStatement stmt;
    PreparedUpdateStatement(PreparedStatement preparedStatement) {
        stmt = preparedStatement;
    }

    public int executeUpdate() throws SQLException {
        return stmt.executeUpdate();
    }

    public void setInt(Integer index, Integer value) throws SQLException {
        stmt.setInt(index, value);
    }

    public void setString(Integer index, String value) throws SQLException {
        stmt.setString(index, value);
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
