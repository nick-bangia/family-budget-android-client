package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the response from the /ping operation to the FamilyBudget API
 */
public class LoginResponse extends APIResponseObject {

    private List<APIToken> data;

    public List<? extends DataObject> getData() {
        return this.data;
    }

    public void setData(List<? extends DataObject> value) {
        this.data = GetStronglyTypedListFrom(value);
    }

    public List<APIToken> GetStronglyTypedListFrom(List<? extends DataObject> list) {
        // loop through the List, and insert each element, casted as a PingDataObject,
        // into a new list
        ArrayList<APIToken> newList = new ArrayList();

        for (DataObject dataObject : list) {
            newList.add((APIToken)dataObject);
        }

        return newList;
    }
}
