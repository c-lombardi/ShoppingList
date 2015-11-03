import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Christopher on 10/26/2015.
 */
public class Session {
    private UUID SessionId;
    private String SessionName;

    private Session (SessionBuilder sb) {
        SessionId = sb.SessionId;
        SessionName = sb.SessionName;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public String getSessionName() {
        return SessionName;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(SessionId).trim());
        sb.append(",");
        sb.append(SessionName.trim());
        return sb.toString();
    }

    public static Session fromString(String sessionString) throws SQLException, ClassNotFoundException {
        final String [] partStrings = sessionString.split(",");
        final SessionBuilder ib = new SessionBuilder(UUID.fromString(partStrings[0]), partStrings[1]);
        return ib.build();
    }

    public static class SessionBuilder {
        private UUID SessionId;
        private String SessionName;

        public SessionBuilder(UUID sid, String sn) {
            SessionId = sid;
            SessionName = sn;
        }

        public SessionBuilder create() {
            try (final database db = new database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(sessionQueries.createSession(SessionName)))
                {
                    try (final ResultSet rs = stmt.executeQuery()){
                        SessionId = UUID.fromString(rs.getString("SessionId"));
                    }
                }
            } catch (Exception ex) {

            }
            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}
