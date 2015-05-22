package com.thebangias.familybudgetclient.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.TableRow;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;
import com.thebangias.familybudgetclient.adapters.SubcategoryGridAdapter;
import com.thebangias.familybudgetclient.model.CategoryAllowance;
import com.thebangias.familybudgetclient.model.SubcategoryAllowance;
import com.thebangias.familybudgetclient.utils.FXUtils;

import java.text.NumberFormat;
import java.util.List;

/**
 * A wholly-contained control to show one category & and its members
 */
public class CategoryGrid extends TableRow {

    private ToggleText categoryTitle;
    private TextView categoryBalance;
    private TableRow gridRow;
    private GridView subcategoryGrid;

    public CategoryGrid(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.categorygrid, this, true);

        // get the controls in this view by their ID
        categoryTitle = (ToggleText) findViewById(R.id.categoryTitle);
        categoryBalance = (TextView) findViewById(R.id.categoryBalance);
        gridRow = (TableRow) findViewById(R.id.gridRow);
        subcategoryGrid = (GridView) findViewById(R.id.gridView);

        // set up an onclick listener for the toggleButton & categoryTitle
        ToggleListener toggleListener = new ToggleListener();
        categoryTitle.setOnClickListener(toggleListener);
    }

    public CategoryGrid(Context context) {

        // call the main constructor, passing in null for the attribute set
        this(context, null);
    }

    /**
     * A private OnClickListener that shows/hides the grid & toggles the toggle button in this view
     */
    private class ToggleListener implements OnClickListener {

        public void onClick(View v) {
            // based on the state of the gridRow, show or hide it and adjust the
            categoryTitle.Toggle();

            if (gridRow.isShown()) {
                FXUtils.slideUp(gridRow.getContext(), gridRow, new GridAnimationListener());
            } else {
                gridRow.setVisibility(View.VISIBLE);
                FXUtils.slideDown(gridRow.getContext(), gridRow, null);
            }
        }
    }

    public void setCategoryTitle(String value) {
        categoryTitle.setToggleText(value);
    }

    public void setCategoryBalance(String value) {
        categoryBalance.setText(value);
    }

    public void RenderGrid(CategoryAllowance category, NumberFormat currency) {
        // get the activity
        Activity thisActivity = (Activity)this.getContext();

        List<SubcategoryAllowance> subcategories = category.getSubcategories();
        SubcategoryGridAdapter scAdapter = new SubcategoryGridAdapter(thisActivity, currency, subcategories);
        this.setCategoryTitle(category.getCategoryName());
        this.setCategoryBalance(currency.format(category.getReconciledAmount()));
        this.subcategoryGrid.setNumColumns(scAdapter.SUBCATEGORY_COLUMN_COUNT);
        this.subcategoryGrid.setAdapter(scAdapter);
    }

    private class GridAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationRepeat(Animation a) {
            // do nothing
        }

        @Override
        public void onAnimationStart(Animation a) {
            // do nothing
        }

        @Override
        public void onAnimationEnd(Animation a) {
            if (gridRow.isShown()) {
                gridRow.setVisibility(View.GONE);
            }
        }
    }
}
