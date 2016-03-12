import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Christopher on 10/26/2015.
 */
public class Session implements java.io.Serializable {
    private UUID SessionId;
    private String SessionPhoneNumber;
    private String SessionAuthCode;

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(final UUID sId) {
        SessionId = sId;
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
        if ((sac == null || sac == "")) {
            SessionAuthCode = new SessionAuthCodeGenerator().Generate();
            //updateAuthCode();
        } else {
            SessionAuthCode = sac;
        }
    }

    public boolean CheckSessionForAuthentication() {
        try (final database db = new database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(sessionQueries.getSessionPhoneNumberById(SessionId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String queriedPhoneNumber = rs.getString("SessionPhoneNumber");
                        boolean result = queriedPhoneNumber.equals(SessionPhoneNumber);
                        return result;
                    }
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public void updateAuthCode() {
        try (final database db = new database()) {
            db.updateTableQuery(sessionQueries.setSessionAuthCodeById(SessionId, SessionAuthCode));
        } catch (Exception ex) {
        }
    }

    public void create() {
        try (final database db = new database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(sessionQueries.createSession(SessionPhoneNumber, SessionAuthCode))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String result = rs.getString("SessionId");
                        setSessionId(UUID.fromString(result));
                    }
                }
            }
        } catch (Exception ex) {
            try (final database db = new database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(sessionQueries.getSessionIdByPhoneNumberAndAuthCode(SessionPhoneNumber, SessionAuthCode))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String result = rs.getString("SessionId");
                            setSessionId(UUID.fromString(result));
                        }
                    }
                }
            } catch (Exception ex1) {
                System.out.println(ex1);
            }
        } finally {
            clearAuthCodeAndPhoneNumber();
        }
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
