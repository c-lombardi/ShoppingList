import java.util.UUID;

/**
 * Created by Christopher on 10/29/2015.
 */
public class sessionQueries {

    public static String createSession(final String sessionPhoneNumber, final String sessionAuthCode) {
        return String.format("INSERT INTO Sessions (SessionPhoneNumber, SessionAuthCode) " +
                "VALUES ('%s', '%s') " +
                "RETURNING SessionId;", sessionPhoneNumber, sessionAuthCode);
    }

    public static String setSessionAuthCodeByPhoneNumber(final String sessionPhoneNumber, final String sessionAuthCode) {
        return String.format("UPDATE Sessions " +
                "SET SessionAuthCode = '%s' " +
                "WHERE SessionPhoneNumber = '%s';", sessionAuthCode, sessionPhoneNumber);
    }

    public static String getSessionPhoneNumberAndAuthCodeById(final UUID sId) {
        return String.format("SELECT SessionPhoneNumber, SessionAuthCode " +
                "FROM Sessions " +
                "WHERE SessionId = '%s';", sId.toString());
    }

    public static String getSessionIdByPhoneNumberAndAuthCode(final String sessionPhoneNumber, final String authCode) {
        return String.format("SELECT SessionId " +
                "FROM Sessions " +
                "WHERE SessionPhoneNumber = '%s' AND SessionAuthCode = '%s';", sessionPhoneNumber, authCode);
    }
}
