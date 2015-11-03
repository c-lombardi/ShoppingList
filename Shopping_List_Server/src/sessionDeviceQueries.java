import java.util.UUID;

/**
 * Created by Christopher on 11/2/2015.
 */
public class sessionDeviceQueries {
    public static final String AUTHORIZE_SESSION_DEVICE (UUID sId, String dId){
        return String.format("SELECT * FROM SessionDevices " +
                "WHERE SessionId = '%s' AND DeviceId = '%s'", sId, dId);
    }
    public static final String CREATE_SESSION_DEVICE (UUID sId, String dId) {
        return String.format("INSERT INTO SessionDevices (SessionId, DeviceId) " +
                "VALUES ('%s', '%s') " +
                "RETURNING SessionId, DeviceId;", sId, dId);
    }
    public static final String GET_ALL_SESSIONS_FOR_DEVICE_ID (String dId) {
        return String.format("SELECT * FROM Sessions " +
                "INNER JOIN SessionDevices ON Sessions.SessionId = SessionDevices.SessionId; ", dId);
    }
}
