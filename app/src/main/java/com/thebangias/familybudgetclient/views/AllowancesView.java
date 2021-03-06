package com.thebangias.familybudgetclient.views;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.model.Allowance;
import com.thebangias.familybudgetclient.model.CategoryAllowance;
import com.thebangias.familybudgetclient.utils.DateUtilities;

import java.text.NumberFormat;
import java.util.List;

/**
 * Compound view to display Allowances for a particular account
 */
public class AllowancesView extends TableLayout {

    private TableLayout allowancesLayout;
    private TextView accountTitle;
    private TextView accountBalance;
    private int currentViewCount;
    private TextView lastUpdated;
    private TextView accountPendingBalance;

    public AllowancesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.allowances, this, true);

        allowancesLayout = (TableLayout) findViewById(R.id.allowances_layout);
        accountTitle = (TextView) findViewById(R.id.account_title);
        accountBalance = (TextView) findViewById(R.id.account_reconciled_balance);
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);
        accountPendingBalance = (TextView) findViewById(R.id.account_pending_balance);
    }

    public AllowancesView(Context context) {
        this(context, null);
    }

    public void setAccountTitle(String value) {
        accountTitle.setText(value);
    }

    public void setAccountBalance(String value) {
        accountBalance.setText(value);
    }

    public void setAccountPendingBalance(String value) {
        accountPendingBalance.setText("pending " + value);
        if (value.startsWith(getContext().getString(R.string.negativeCurrencyPrefix))) {
            accountPendingBalance.setTextColor(getResources().getColor(R.color.negative_currency));
        }
    }

    public void setLastUpdated(String value) {
        lastUpdated.setText(value);
    }

    public void setAllowances(Activity activity, NumberFormat currency, Allowance anAllowance) {

        // account info
        this.setAccountTitle(anAllowance.getAccountName());
        this.setAccountBalance(currency.format(anAllowance.getReconciledAmount()));

       this.setLastUpdated(DateUtils.getRelativeTimeSpanString(
            anAllowance.getLatestTransactionDate().getTime(),
            DateUtilities.GetUTCdatetimeAsDate().getTime(),
            DateUtils.MINUTE_IN_MILLIS).toString());
        this.setAccountPendingBalance(currency.format(anAllowance.getPendingAmount()));

        // remove previous category views from the layout
        allowancesLayout.removeViews(2, currentViewCount);

        // get the list of categoryAllowances to generate grids view
        List<CategoryAllowance> categories = anAllowance.getCategories();
        // reset the currentViewCount for this set of categories
        currentViewCount = categories.size();
        // loop through categories, and create a new categoryGrid control for each
        for (int i = 0; i < currentViewCount; i++) {
            // get the category for this iteration
            CategoryAllowance category = categories.get(i);

            // initialize a new category grid control
            CategoryGrid categoryGrid = new CategoryGrid(activity);

            // render the grid
            categoryGrid.RenderGrid(category, currency);

            // add the grid to the allowances table layout, at the correct position (i + 1)
            allowancesLayout.addView(categoryGrid, i+2);
        }
    }
}
