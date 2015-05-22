package com.thebangias.familybudgetclient.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.thebangias.familybudgetclient.R;

/**
 * A textual toggle button
 */
public class ToggleText extends TextView {

    private ToggleState toggleState;
    private String toggleText;
    private final String SPACES = "  ";

    private enum ToggleState {
        EXPANDED,
        COLLAPSED
    }

    public ToggleText(Context context, AttributeSet attrs) {
        super(context, attrs);

        // default toggle state is collapsed
        toggleState = ToggleState.COLLAPSED;
    }

    public ToggleText(Context context) {

        this(context, null);
    }

    public void setToggleText(String text) {
        // upon initially setting the text for the control, set it to be collapsed
        Context context = this.getContext();
        this.toggleText = text;
        this.setText(context.getString(R.string.sc_header_name_collapsed_icon) + SPACES + text);
    }

    public void Toggle() {
        if (toggleState == ToggleState.COLLAPSED) {
            // if the state is currently collapsed, set it to expanded
            toggleState = ToggleState.EXPANDED;
            this.setText(this.getContext().getString(R.string.sc_header_name_expanded_icon) + SPACES + toggleText);

        } else if (toggleState == ToggleState.EXPANDED) {
            // otherwise, if currently expanded, set it to collapsed
            toggleState = ToggleState.COLLAPSED;
            this.setText(this.getContext().getString(R.string.sc_header_name_collapsed_icon) + SPACES + toggleText);
        }
    }
}
