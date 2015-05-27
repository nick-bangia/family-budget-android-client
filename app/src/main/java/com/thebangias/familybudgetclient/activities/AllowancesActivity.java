package com.thebangias.familybudgetclient.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.model.Allowance;
import com.thebangias.familybudgetclient.model.AllowancesResponse;
import com.thebangias.familybudgetclient.model.RefreshAllowancesResponse;
import com.thebangias.familybudgetclient.utils.APIUtils;
import com.thebangias.familybudgetclient.views.AllowancesView;
import com.thebangias.familybudgetclient.views.DrawerAccountItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class AllowancesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private AccountItemAdapter navigationDrawerAdapter;
    private GetAllowancesTask getAllowancesTask;
    private RefreshAllowancesTask refreshAllowancesTask;
    private List<Allowance> allowances;
    private AllowancesView allowancesView;
    private NumberFormat currency;
    private String rootUrl;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowances);

        // get the login information for the API
        String packageName = getString(R.string.package_name);
        SharedPreferences prefs = this.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
        rootUrl = prefs.getString(packageName + ".apirooturl", null);
        email = prefs.getString(packageName + ".email", null);
        password = prefs.getString(packageName + ".password", null);

        // initialize the views, navigation drawer, and toolbar
        initView();
        initDrawer();

        // set up the currency formatter
        currency = NumberFormat.getCurrencyInstance();
        DecimalFormat formatter = (DecimalFormat)currency;
        formatter.setNegativePrefix(getString(R.string.negativeCurrencyPrefix) + formatter.getPositivePrefix());
        formatter.setNegativeSuffix(getString(R.string.negativeCurrencySuffix));

        // get the allowance data via an AsyncTask & populate the views
        getAllowancesTask = new GetAllowancesTask(rootUrl, email, password);
        getAllowancesTask.execute((Void) null);
    }

    private void initView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) { setSupportActionBar(toolbar); }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        allowancesView = (AllowancesView) findViewById(R.id.allowances_view1);
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void selectAccount(int position) {
        // TODO: create a new fragment to use for this account and provide it the data it needs

        if (allowances != null) {
            Allowance allowance = allowances.get(position);
            allowancesView.setAllowances(this, currency, allowance);
            drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.allowances, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        if (id == R.id.menu_refresh) {
            // execute the refresh operation
            refreshAllowancesTask = new RefreshAllowancesTask(rootUrl, email, password);
            refreshAllowancesTask.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous task used to grab latest allowance data from the API
     */
    private class GetAllowancesTask extends AsyncTask<Void, Void, AllowancesResponse> {

        private final String email;
        private final String password;
        private final String apiRootUrl;

        GetAllowancesTask(String apiRootUrl, String email, String password) {
            this.email = email;
            this.password = password;
            this.apiRootUrl = apiRootUrl;
        }

        @Override
        protected AllowancesResponse doInBackground(Void... params) {
            // initialize an AllowanceResponse
            AllowancesResponse response = null;

            try {
                APIUtils utils = new APIUtils(AllowancesActivity.this, email, password, apiRootUrl);
                // retreive the allowances from the API
                response = utils.GetAllowances();
            } catch (Exception ex) {
                response = new AllowancesResponse();
                response.setStatus("failure");
                response.setReason(ex.getMessage());
            }

            // return the AllowancesResponse, whether successful or not
            return response;
        }

        @Override
        protected void onPostExecute(final AllowancesResponse response) {

            // set the allowances in the activity
            allowances = response.GetStronglyTypedListFrom(response.getData());

            // get the array of accounts to add to the drawer
            DrawerAccountItem[] leftSliderData = new DrawerAccountItem[allowances.size()];
            NumberFormat currency = NumberFormat.getCurrencyInstance();
            for (int i=0; i < allowances.size(); i++) {
                Allowance currentAccount = allowances.get(i);
                leftSliderData[i] = new DrawerAccountItem(currentAccount.getAccountName(),
                    currency.format(currentAccount.getReconciledAmount()));
            }

            // update navigation drawer accordingly & content accordingly
            navigationDrawerAdapter=new AccountItemAdapter( AllowancesActivity.this, R.layout.drawer_item, leftSliderData);
            leftDrawerList.setAdapter(navigationDrawerAdapter);
            drawerLayout.openDrawer(Gravity.LEFT);
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Represents an asynchronous task used to refresh allowance data from the API
     */
    private class RefreshAllowancesTask extends AsyncTask<Void, Void, RefreshAllowancesResponse> {

        private final String email;
        private final String password;
        private final String apiRootUrl;

        RefreshAllowancesTask(String apiRootUrl, String email, String password) {
            this.email = email;
            this.password = password;
            this.apiRootUrl = apiRootUrl;
        }

        @Override
        protected RefreshAllowancesResponse doInBackground(Void... params) {
            // initialize an AllowanceResponse
            RefreshAllowancesResponse response = null;

            try {
                APIUtils utils = new APIUtils(AllowancesActivity.this, email, password, apiRootUrl);
                // retreive the allowances from the API
                response = utils.RefreshAllowances();
            } catch (Exception ex) {
                response = new RefreshAllowancesResponse();
                response.setStatus("failure");
                response.setReason(ex.getMessage());
            }

            // return the AllowancesResponse, whether successful or not
            return response;
        }

        @Override
        protected void onPostExecute(final RefreshAllowancesResponse response) {
            refreshAllowancesTask = null;

            if (response != null && response.getStatus().equals("ok")) {
                // if the refresh is ok, kick off the allowancesTask to grab the allowances
                getAllowancesTask = new GetAllowancesTask(this.apiRootUrl, this.email, this.password);
                getAllowancesTask.execute();
            }
        }

        @Override
        protected void onCancelled() {
            refreshAllowancesTask = null;
        }
    }

    public class AccountItemAdapter extends ArrayAdapter<DrawerAccountItem> {

        Context mContext;
        int layoutResourceId;
        DrawerAccountItem data[] = null;

        public AccountItemAdapter(Context mContext, int layoutResourceId, DrawerAccountItem[] data) {

            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItem = convertView;

            LayoutInflater inflater = ((AllowancesActivity) mContext).getLayoutInflater();
            listItem = inflater.inflate(layoutResourceId, parent, false);

            TextView accountLabel = (TextView) listItem.findViewById(R.id.drawer_account_label);
            TextView accountBalance = (TextView) listItem.findViewById(R.id.drawer_account_balance);

            DrawerAccountItem accountItem = data[position];

            accountLabel.setText(accountItem.accountLabel);
            accountBalance.setText(accountItem.accountBalance);

            return listItem;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectAccount(position);
        }
    }
}