package com.example.christopher.shopping_list;

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
        SessionAuthCode = sac;
    }
}