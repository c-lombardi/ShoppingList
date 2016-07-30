/**
 * Created by Christopher on 10/29/2015.
 */
public class sessionQueries {

    public static final String createSession() {
        return "SELECT createSession(?, ?);";
    }

    public static final String setSessionAuthCodeByPhoneNumber() {
        return "SELECT updateSessionAuthCodeByPhoneNumber(?, ?);";
    }

    public static final String getSessionIdByPhoneNumberAndAuthCode() {
        return "SELECT getSessionIdByPhoneNumberAndAuthCode(?, ?);";
    }
}
