package com.example.christopher.shopping_list;

import java.util.UUID;

/**
 * Created by Christopher on 10/26/2015.
 */
public class Session {
    private UUID SessionId;
    private String SessionName;
    private String SessionPin;

    private Session (SessionBuilder sb) {
        SessionId = sb.SessionId;
        SessionName = sb.SessionName;
        SessionPin = sb.SessionPin;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public String getSessionName() {
        return SessionName;
    }

    public String getSessionPing() {
        return SessionPin;
    }

    public static class SessionBuilder {
        private final UUID SessionId;
        private final String SessionName;
        private final String SessionPin;

        public SessionBuilder(UUID sid, String sn, String sp) {
            SessionId = sid;
            SessionName = sn;
            SessionPin = sp;
        }

        public Session build() {
            return new Session(this);
        }
    }
}
