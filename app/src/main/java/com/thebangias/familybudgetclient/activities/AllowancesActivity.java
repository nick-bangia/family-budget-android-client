package com.thebangias.familybudgetclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.controls.SubcategoryGridAdapter;
import com.thebangias.familybudgetclient.model.Allowance;
import com.thebangias.familybudgetclient.model.AllowancesResponse;
import com.thebangias.familybudgetclient.model.CategoryAllowance;
import com.thebangias.familybudgetclient.model.SubcategoryAllowance;
import com.thebangias.familybudgetclient.utils.APIUtils;

import java.text.NumberFormat;
import java.util.List;


public class AllowancesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private TextView accountTitle;
    private TextView accountReconciledBalance;
    private TextView categoryName;
    private GridView gridView;
    private GetAllowancesTask getAllowancesTask;
    private List<Allowance> allowances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowances);

        // get the login information for the API
        String packageName = getString(R.string.package_name);
        SharedPreferences prefs = this.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
        String savedRootUrl = prefs.getString(packageName + ".apirooturl", null);
        String savedEmail = prefs.getString(packageName + ".email", null);
        String savedPassword = prefs.getString(packageName + ".password", null);

        // initialize the views, navigation drawer, and toolbar
        initView();
        initDrawer();

        // get the allowance data via an AsyncTask & populate the views
        getAllowancesTask = new GetAllowancesTask(savedRootUrl, savedEmail, savedPassword);
        getAllowancesTask.execute((Void) null);
    }

    private void initView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) { setSupportActionBar(toolbar); }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        accountTitle = (TextView) findViewById(R.id.account_title);
        accountReconciledBalance = (TextView) findViewById(R.id.account_reconciled_balance);
        gridView = (GridView) findViewById(R.id.gridView);
        categoryName = (TextView) findViewById(R.id.categoryName);
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
            Allowance accountBalance = allowances.get(position);
            accountTitle.setText(accountBalance.getAccountName());
            accountReconciledBalance.setText(NumberFormat.getCurrencyInstance().format(accountBalance.getReconciledAmount()));

            CategoryAllowance category = accountBalance.getCategories().get(0);
            List<SubcategoryAllowance> subcategories = category.getSubcategories();
            SubcategoryGridAdapter scAdapter = new SubcategoryGridAdapter(this, subcategories);
            categoryName.setText(category.getCategoryName());
            gridView.setNumColumns(scAdapter.SUBCATEGORY_COLUMN_COUNT);
            gridView.setAdapter(scAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
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
                APIUtils utils = new APIUtils(email, password, apiRootUrl);
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
            getAllowancesTask = null;

            // set the allowances in the activity
            allowances = response.GetStronglyTypedListFrom(response.getData());

            // get the array of accounts to add to the drawer
            String[] leftSliderData = new String[allowances.size()];
            NumberFormat currency = NumberFormat.getCurrencyInstance();
            for (int i=0; i < allowances.size(); i++) {
                Allowance currentAccount = allowances.get(i);
                leftSliderData[i] = currentAccount.getAccountName() +
                        "      " + currency.format(currentAccount.getReconciledAmount());
            }

            // update navigation drawer accordingly & content accordingly
            navigationDrawerAdapter=new ArrayAdapter( AllowancesActivity.this, android.R.layout.simple_list_item_1, leftSliderData);
            leftDrawerList.setAdapter(navigationDrawerAdapter);
        }

        @Override
        protected void onCancelled() {
            getAllowancesTask = null;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectAccount(position);
        }
    }
}