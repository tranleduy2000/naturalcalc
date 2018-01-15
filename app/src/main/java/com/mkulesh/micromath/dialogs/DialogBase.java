/*******************************************************************************
 * microMathematics Plus - Extended visual calculator
 * *****************************************************************************
 * Copyright (C) 2014-2017 Mikhail Kulesh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.mkulesh.micromath.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.util.Locale;

public class DialogBase extends AppCompatDialog implements OnClickListener {
    protected final SharedPreferences pref;
    final private TextView title;

    public DialogBase(Context context, int layoutId, int titleId) {
        super(context);
        setCanceledOnTouchOutside(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutId, (LinearLayout) findViewById(R.id.dialog_content_panel));

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.drawableDialogWindow, outValue, true);
        getWindow().getDecorView().setBackgroundResource(outValue.resourceId);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        title = findViewById(R.id.dialog_title_text);
        title.setText(titleId);
        prepareTextStyle(title);

        final Button okButton = findViewById(R.id.dialog_button_ok);
        okButton.setOnClickListener(this);
        prepareTextStyle(okButton);
        prepareButtonImage(okButton);

        final Button cancelButton = findViewById(R.id.dialog_button_cancel);
        cancelButton.setOnClickListener(this);
        prepareTextStyle(cancelButton);
        prepareButtonImage(cancelButton);

        final View divider = findViewById(R.id.dialog_divider_view);
        CompatUtils.setDrawableColorAttr(context, divider.getBackground(), R.attr.colorDialogTitle);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    private void prepareTextStyle(TextView v) {
        final String text = v.getText().toString().toUpperCase(Locale.getDefault());
        v.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_button_cancel) {
            closeDialog();
        }
    }

    public LinearLayout getRootLayout() {
        View root = this.findViewById(R.id.dialog_root_layout);
        return (root instanceof LinearLayout) ? (LinearLayout) root : null;
    }

    protected void closeDialog(boolean hideKeyboard) {
        if (!ViewUtils.isHardwareKeyboardAvailable(getContext())) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            ViewUtils.debug(this, "request to hide keyboard from dialog");
            imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
        }
        dismiss();
    }

    protected void closeDialog() {
        closeDialog(/*hideKeyboard=*/ true);
    }

    protected void maximize() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
    }

    protected void setButtonSelected(ImageView b, boolean isSelected) {
        b.setSelected(isSelected);
        ViewUtils.setImageButtonColorAttr(getContext(), b,
                b.isSelected() ? R.attr.colorAccent : R.attr.colorDialogContent);
    }

    protected void setButtonEnabled(ImageView b, boolean isEnabled) {
        b.setEnabled(isEnabled);
        ViewUtils.setImageButtonColorAttr(getContext(), b,
                b.isEnabled() ? R.attr.colorDialogContent : R.attr.colorDialogDisabledElement);
    }

    protected void prepareButtonImage(Button b) {
        for (Drawable d : b.getCompoundDrawables()) {
            CompatUtils.setDrawableColorAttr(getContext(), d,
                    b.isEnabled() ? R.attr.colorDialogContent : R.attr.colorDialogDisabledElement);
        }
    }

}
