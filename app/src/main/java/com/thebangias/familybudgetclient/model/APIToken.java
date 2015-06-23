package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.util.Date;

/**
 * Represents the Data returned by the Ping operation
 */
public class APIToken extends DataObject {

    private String access_token;
    private Date expires_on;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String value) {
        this.access_token = value;
    }

    public Date getExpires_on() { return this.expires_on; }

    public void setExpires_on(Date value) {
        this.expires_on = value;
    }
}
