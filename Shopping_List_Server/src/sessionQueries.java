/**
 * Created by Christopher on 10/29/2015.
 */
public class sessionQueries {

    public static final String createSession(final String sessionPhoneNumber, final String sessionAuthCode) {
        return String.format("INSERT INTO %s (SessionPhoneNumber, SessionAuthCode) " +
                "VALUES ('%s', '%s') " +
                "RETURNING SessionId;", Database.SessionsTableName, sessionPhoneNumber, sessionAuthCode);
    }

    public static final String setSessionAuthCodeByPhoneNumber(final String sessionPhoneNumber, final String sessionAuthCode) {
        return String.format("UPDATE %s " +
                "SET SessionAuthCode = '%s' " +
                "WHERE SessionPhoneNumber = '%s';", Database.SessionsTableName, sessionAuthCode, sessionPhoneNumber);
    }

    public static final String getSessionIdByPhoneNumberAndAuthCode(final String sessionPhoneNumber, final String authCode) {
        return String.format("SELECT SessionId " +
                "FROM %s " +
                "WHERE SessionPhoneNumber = '%s' AND SessionAuthCode = '%s';", Database.SessionsTableName, sessionPhoneNumber, authCode);
    }
}
