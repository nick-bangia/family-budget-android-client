package com.thebangias.familybudgetclient.model.abstractions;

import java.util.List;

/**
 * Models the Response returned by the FamilyBudget Data API
 */
public abstract class APIResponseObject {

    private String status;
    private String reason;

    // constructor
    public APIResponseObject() {
    }

    // getters & setters
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String value) {
        this.reason = value;
    }

    public abstract List<? extends DataObject> getData();

    public abstract void setData(List<? extends DataObject> value);

    public abstract List<? extends DataObject> GetStronglyTypedListFrom(List<? extends DataObject> list);
}