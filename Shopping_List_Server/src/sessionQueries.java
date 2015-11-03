import java.util.UUID;

/**
 * Created by Christopher on 10/29/2015.
 */
public class sessionQueries {

    public static String createSession(String sName) {
        return String.format("INSERT INTO Sessions (SessionName) " +
                "VALUES ('%s') " +
                "RETURNING SessionId;", sName);
    }

    public static String addPinToSession(UUID sId, String pin) {
        return String.format("UPDATE Sessions " +
                "SET SessionPin = '%s' " +
                "WHERE SessionId = '%s';", pin, sId.toString());
    }

    public static String getSessionById(UUID sId) {
        return String.format("SELECT * " +
                "FROM Sessions " +
                "WHERE SessionId = '%s';", sId.toString());
    }

    public static String removeSessionById(UUID sId) {
        return String.format("DELETE FROM Sessions " +
                "WHERE SessionId = '%s';", sId.toString());
    }
}
