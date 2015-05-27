package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the response from /refreshAllowances operation to the API
 */
public class RefreshAllowancesResponse extends APIResponseObject {

    private List<DataObject> data;

    public List<? extends DataObject> getData() {
        return new ArrayList<DataObject>();
    }

    public void setData(List<? extends DataObject> value) {
        this.data = GetStronglyTypedListFrom(value);
    }

    public List<DataObject> GetStronglyTypedListFrom(List<? extends DataObject> list) {
        // loop through the List, and insert each element, casted as a PingDataObject,
        // into a new list
        ArrayList<DataObject> newList = new ArrayList();

        for (DataObject dataObject : list) {
            newList.add(dataObject);
        }

        return newList;
    }
}
