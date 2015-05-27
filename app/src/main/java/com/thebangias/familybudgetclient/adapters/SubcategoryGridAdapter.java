package com.thebangias.familybudgetclient.adapters;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.model.SubcategoryAllowance;

import java.text.NumberFormat;
import java.util.List;

/**
 * Extends DataAdapter and provides data in a grid-like interface
 */
public class SubcategoryGridAdapter extends BaseAdapter {

    private Activity context = null;
    private List<SubcategoryAllowance> items = null;
    public final int SUBCATEGORY_COLUMN_COUNT = 3;
    private NumberFormat currency;

    // an enum to help define the columns of the subcategory grid
    private enum SubcategoryColumns {

        NAME(0),
        RECONCILED_AMOUNT(1),
        PENDING_AMOUNT(2);

        private SubcategoryColumns(int position) {
            this.position = position;
        }

        // private field & getter for the position of this column
        private int position;
        public int getPosition() {
            return this.position;
        }
    }

    public SubcategoryGridAdapter(Activity context, NumberFormat currency, List<SubcategoryAllowance> items) {
        super();

        this.context = context;
        this.items = items;
        this.currency = currency;
    }

    @Override
    public int getCount() {

        // return the number of items (rows) + 1 (for headers) multiplied by the total
        // number of columns;
        return (items.size() + 1) * SUBCATEGORY_COLUMN_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the row that this position falls in, to test whether we are in the header
        int row = GetRow(position);

        // get the column that this position falls in, to determine what column we are in
        int col = GetCol(position);

        // Get the text for this position
        String text = PositionToString(position);

        // get the TextView control for this position (from convertView)
        TextView control;
        if (convertView != null) {
            control = (TextView) convertView;
        } else {
            control = new TextView(context);
        }

        // set some formatting for the cells given its position in the gridview
        if (row == 0) {
            // set the header's background & text color
            control.setBackgroundColor(context.getResources().getColor(R.color.datagrid_header_bg));
            control.setTextColor(context.getResources().getColor(R.color.datagrid_header_text));

            // check the type of column and set the gravity & padding
            if (col == SubcategoryColumns.NAME.getPosition()) {
                control.setPadding(10, 0, 0, 0);
                control.setGravity(Gravity.START);
            } else if (col == SubcategoryColumns.RECONCILED_AMOUNT.getPosition()) {
                control.setPadding(5, 0, 5, 0);
                control.setGravity(Gravity.END);
            } else if (col == SubcategoryColumns.PENDING_AMOUNT.getPosition()) {
                control.setPadding(0, 0, 10, 0);
                control.setGravity(Gravity.END);
            }
        } else {
            // default the text color for the data
            control.setTextColor(context.getResources().getColor(R.color.datagrid_data_text));

            // if the amount field is negative, then color it red
            if ((col == SubcategoryColumns.RECONCILED_AMOUNT.getPosition() ||
                 col == SubcategoryColumns.PENDING_AMOUNT.getPosition()) &&
                text.startsWith(context.getResources().getString(R.string.negativeCurrencyPrefix))) {
                // this is not the header AND it is the amount position AND it is negative so
                // change the text color to the negative_currency color
                control.setTextColor(context.getResources().getColor(R.color.negative_currency));
            }

            // set the gravity and padding as needed
            if (col == SubcategoryColumns.NAME.getPosition()) {
                control.setPadding(15, 0, 0, 0);
                control.setGravity(Gravity.START);
            } else if (col == SubcategoryColumns.RECONCILED_AMOUNT.getPosition()) {
                control.setPadding(5, 0, 5, 0);
                control.setGravity(Gravity.END);
            } else if (col == SubcategoryColumns.PENDING_AMOUNT.getPosition()) {
                control.setPadding(0, 0, 10, 0);
                control.setGravity(Gravity.END);
            }

            // set the size of the text
            control.setTextSize(10);
        }

        // assign the control it's value
        control.setText(text);

        // return the view
        return control;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public Object getItem(int position) {
        // subtract one from the row # to compensate for the header row
        return items.get(GetRow(position) - 1);
    }

    private int GetCol(int position) {
        // return the modulus of the position & number of columns to get the column position
        return position % SUBCATEGORY_COLUMN_COUNT;
    }

    private int GetRow(int position) {
        // return the dividend of the position & number of columns to get the row that we are
        // currently working with
        return position / SUBCATEGORY_COLUMN_COUNT;
    }

    private String PositionToString(int position) {

        // get the row that is being displayed
        int row = GetRow(position);

        // get the column that is being displayed
        int col = GetCol(position);

        // if the row is 0, then it is the header, so return the name of the column
        if (row == 0) {

            if (col == SubcategoryColumns.NAME.getPosition()) {
                return context.getString(R.string.sc_header_name);
            } else if (col == SubcategoryColumns.RECONCILED_AMOUNT.getPosition()) {
                return context.getString(R.string.sc_header_reconciledamount);
            } else if (col == SubcategoryColumns.PENDING_AMOUNT.getPosition()) {
                return context.getString(R.string.sc_header_pendingamount);
            }
        }

        // otherwise, use the row/col combination to get the data from the list
        // remembering to compensate for the header row by subtracting 1 from the row
        try {
            SubcategoryAllowance thisAllowance = items.get(row - 1);
            if (thisAllowance != null) {

                if (col == SubcategoryColumns.NAME.getPosition()) {
                    return thisAllowance.getSubcategoryName();
                } else if (col == SubcategoryColumns.RECONCILED_AMOUNT.getPosition()) {
                    return currency.format(thisAllowance.getReconciledAmount());
                } else if (col == SubcategoryColumns.PENDING_AMOUNT.getPosition()) {
                    return currency.format(thisAllowance.getPendingAmount());
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            // TODO: log this exception
        }

        // if all fails, return an empty string
        return "";
    }
}
