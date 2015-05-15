package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.DataObject;

/**
 * Represents the Data returned by the Ping operation
 */
public class Ping extends DataObject {

    private boolean isAuthorized;

    public boolean getIsAuthorized() {
        return this.isAuthorized;
    }

    public void setIsAuthorized(boolean value) {
        this.isAuthorized = value;
    }
}
