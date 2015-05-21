package com.thebangias.familybudgetclient.controls;

import android.app.Activity;
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
    public final int SUBCATEGORY_COLUMN_COUNT = 2;
    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    // an enum to help define the columns of the subcategory grid
    private enum SubcategoryColumns {

        NAME(0),
        AMOUNT(1);

        private SubcategoryColumns(int position) {
            this.position = position;
        }

        // private field & getter for the position of this column
        private int position;
        public int getPosition() {
            return this.position;
        }
    }

    public SubcategoryGridAdapter(Activity context, List<SubcategoryAllowance> items) {
        super();

        this.context = context;
        this.items = items;
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

        // Get the text for this position
        String text = PositionToString(position);

        // get the TextView control for this position (from convertView)
        TextView control;
        if (convertView != null) {
            control = (TextView) convertView;
        } else {
            control = new TextView(context);
        }

        // if this row is a header, set the background color
        if (row == 0) {
            control.setBackgroundColor(context.getResources().getColor(R.color.datagrid_header_bg));
            control.setTextColor(context.getResources().getColor(R.color.datagrid_header_text));
        }

        // assign the control it's value
        control.setText(text, TextView.BufferType.NORMAL);

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
            } else if (col == SubcategoryColumns.AMOUNT.getPosition()) {
                return context.getString(R.string.sc_header_amount);
            }
        }

        // otherwise, use the row/col combination to get the data from the list
        // remembering to compensate for the header row by subtracting 1 from the row
        try {
            SubcategoryAllowance thisAllowance = items.get(row - 1);
            if (thisAllowance != null) {

                if (col == SubcategoryColumns.NAME.getPosition()) {
                    return thisAllowance.getSubcategoryName();
                } else if (col == SubcategoryColumns.AMOUNT.getPosition()) {
                    return currency.format(thisAllowance.getReconciledAmount());
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            // TODO: log this exception
        }

        // if all fails, return an empty string
        return "";
    }
}
