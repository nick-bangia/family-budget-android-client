package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the response from the /allowances operation to the API
 */
public class AllowancesResponse extends APIResponseObject {

    private List<Allowance> data;

    public List<? extends DataObject> getData() {
        return this.data;
    }

    public void setData(List<? extends DataObject> value) {
        this.data = GetStronglyTypedListFrom(value);
    }

    public List<Allowance> GetStronglyTypedListFrom(List<? extends DataObject> list) {
        // loop through the List, and insert each element, casted as a PingDataObject,
        // into a new list
        ArrayList<Allowance> newList = new ArrayList();

        for (DataObject dataObject : list) {
            newList.add((Allowance)dataObject);
        }

        return newList;
    }
}
