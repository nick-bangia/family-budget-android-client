package com.thebangias.familybudgetclient.model;

import com.thebangias.familybudgetclient.model.abstractions.APIResponseObject;
import com.thebangias.familybudgetclient.model.abstractions.DataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the response from the /allowances operation to the API
 */
public class BudgetAllowancesResponse extends APIResponseObject {

    private List<BudgetAllowance> data;

    public List<? extends DataObject> getData() {
        return this.data;
    }

    public void setData(List<? extends DataObject> value) {
        this.data = GetStronglyTypedListFrom(value);
    }

    public List<BudgetAllowance> GetStronglyTypedListFrom(List<? extends DataObject> list) {
        // loop through the List, and insert each element, casted as a PingDataObject,
        // into a new list
        ArrayList<BudgetAllowance> newList = new ArrayList();

        for (DataObject dataObject : list) {
            newList.add((BudgetAllowance)dataObject);
        }

        return newList;
    }
}
