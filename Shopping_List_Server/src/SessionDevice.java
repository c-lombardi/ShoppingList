import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher on 11/2/2015.
 */
public class SessionDevice {
    private UUID SessionId;
    private String DeviceId;

    private SessionDevice(SessionDeviceBuilder sdb) {
        SessionId = sdb.SessionId;
        DeviceId = sdb.DeviceId;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(SessionId).trim());
        sb.append(",");
        sb.append(DeviceId.trim());
        return sb.toString();
    }

    public static SessionDevice fromString(String sessionDeviceString) throws SQLException, ClassNotFoundException {
        final String [] partStrings = sessionDeviceString.split(",");
        final SessionDeviceBuilder sdb = new SessionDeviceBuilder(UUID.fromString(partStrings[0]), partStrings[1]);
        return sdb.build();
    }

    public static class SessionDeviceBuilder {
        private UUID SessionId;
        private String DeviceId;

        public SessionDeviceBuilder(UUID sId, String dId) {
            SessionId = sId;
            DeviceId = dId;
        }

        public SessionDevice.SessionDeviceBuilder authorizeSession() {
            try(final database db = new database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(sessionDeviceQueries.AUTHORIZE_SESSION_DEVICE(SessionId, DeviceId)))
                {
                    try (final ResultSet rs = stmt.executeQuery()){
                        SessionId = UUID.fromString(rs.getString("SessionId"));
                        DeviceId = rs.getString("DeviceId");
                    }
                    catch(Exception ex) {
                        SessionId = null;
                        DeviceId = null;
                    }
                }
            } catch (Exception ex) {

            }
            return this;
        }

        public SessionDeviceBuilder create() {
            try (final database db = new database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(sessionDeviceQueries.CREATE_SESSION_DEVICE(SessionId, DeviceId)))
                {
                    try (final ResultSet rs = stmt.executeQuery()){
                        SessionId = UUID.fromString(rs.getString("SessionId"));
                        DeviceId = rs.getString("DeviceId");
                    }
                }
            } catch (Exception ex) {

            }
            return this;
        }

        public List<Session.SessionBuilder> getAuthorizedSessionsForDevice() {
            final List<Session.SessionBuilder> returnList = new ArrayList<>();
            try (final database db = new database()) {
                try(final PreparedStatement stmt = db.selectTableQuery(sessionDeviceQueries.GET_ALL_SESSIONS_FOR_DEVICE_ID(DeviceId))) {
                    try(final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            returnList.add(new Session.SessionBuilder(UUID.fromString(rs.getString("SessionId")), rs.getString("SessionName")));
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Fail");
                System.out.println(ex);
            }finally {
                return returnList;
            }
        }


        public SessionDevice build () {
            return new SessionDevice(this);
        }
    }
}
