import com.twilio.sdk.TwilioRestException;

import java.sql.ResultSet;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Christopher on 10/26/2015.
 */
public class Session implements java.io.Serializable {
    private String SessionId;
    private String SessionPhoneNumber;
    private String SessionAuthCode;

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(final UUID sId) {
        SessionId = sId.toString();
    }

    public String getSessionPhoneNumber() {
        return SessionPhoneNumber;
    }

    public void setSessionPhoneNumber(final String sessionPhoneNumber) {
        SessionPhoneNumber = sessionPhoneNumber;
    }

    public String getSessionAuthCode() {
        return SessionAuthCode;
    }

    public void setSessionAuthCode(final String sac) {
        SessionAuthCode = sac;
    }

    public boolean CheckSessionForAuthentication() {
        try (final Database db = new Database()) {
            try (final PreparedSelectStatement stmt = db.selectTableQuery(sessionQueries.getSessionIdByPhoneNumberAndAuthCode())) {
                stmt.setString(1, SessionPhoneNumber);
                stmt.setString(2, SessionAuthCode);
                try (final ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        SessionId = rs.getString("SessionId");
                        return true;
                    }
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public void updateAuthCode() {
        SessionAuthCode = new SessionAuthCodeGenerator().Generate();
        try (final Database db = new Database()) {
            try (final PreparedUpdateStatement stmt = db.updateTableQuery(sessionQueries.setSessionAuthCodeByPhoneNumber())){
                stmt.setString(1, SessionPhoneNumber);
                stmt.setString(2, SessionAuthCode);
                stmt.executeUpdate();
            }
            sendAuthCodeToPhoneNumber();
        } catch (Exception ex) {
        }
    }

    public void create() {
        try (final Database db = new Database()) {
            SessionAuthCode = new SessionAuthCodeGenerator().Generate();
            try (final PreparedSelectStatement stmt = db.selectTableQuery(sessionQueries.createSession())) {
                stmt.setString(1, SessionPhoneNumber);
                stmt.setString(2, SessionAuthCode);
                try (final ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        sendAuthCodeToPhoneNumber();
                    }
                }
            }
        } catch (Exception ex) {
        } finally {
            clearAuthCodeAndPhoneNumber();
        }
    }

    private void sendAuthCodeToPhoneNumber() throws TwilioRestException {
        /*final TwilioRestClient client = new TwilioRestClient("AC6a73c702887ab22b3aaf8ff97fc99784", "18221fa9ba8db2b0063f0f8f372161a0");
        final Account account = client.getAccount();
        String availablePhoneNumber = null;
        final Iterator<AvailablePhoneNumber> availablePhoneNumberIterator = account.getAvailablePhoneNumbers().iterator();
        if(availablePhoneNumberIterator.hasNext()){
            availablePhoneNumber = availablePhoneNumberIterator.next().getPhoneNumber();
        }
        if(availablePhoneNumber != null) {
            final com.twilio.sdk.resource.factory.MessageFactory messageFactory = account.getMessageFactory();
            final List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To", SessionPhoneNumber));
            params.add(new BasicNameValuePair("From", "17247395077"));
            params.add(new BasicNameValuePair("Body", SessionAuthCode));
            final com.twilio.sdk.resource.instance.Message sms = messageFactory.create(params);
        }*/
        System.out.println(SessionAuthCode);
    }

    private void clearAuthCodeAndPhoneNumber() {
        SessionPhoneNumber = "";
        SessionAuthCode = "";
    }

    public static class SessionAuthCodeGenerator {
        private static final int min = 0;
        private static final int max = 9;
        private static final int authCodeLength = 6;
        private Random random;
        private StringBuilder authCodeBuilder;

        private String GenerateInt() {
            random = new Random();
            return String.valueOf(random.nextInt(max - min) + min);
        }

        public String Generate() {
            authCodeBuilder = new StringBuilder();
            for (int i = 0; i < authCodeLength; i++) {
                authCodeBuilder.append(GenerateInt());
            }
            return authCodeBuilder.toString();
        }
    }
}
