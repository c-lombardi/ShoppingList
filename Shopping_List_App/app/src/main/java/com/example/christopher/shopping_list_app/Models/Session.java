package com.example.christopher.shopping_list_app.Models;

/**
 * Created by Christopher on 10/26/2015.
 */
public class Session {
    private String SessionId;
    private String SessionPhoneNumber;
    private String SessionAuthCode;

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(final String sId) {
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