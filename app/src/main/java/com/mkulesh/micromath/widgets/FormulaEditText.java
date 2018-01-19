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
package com.mkulesh.micromath.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.utils.FontManager;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

public class FormulaEditText extends AppCompatEditText implements OnLongClickListener,
        View.OnFocusChangeListener, EditorController {

    private static final String TAG = "CustomEditText";
    private AppCompatActivity mActivity = null;
    private OnTextChangeListener mOnTextChangeListener = null;
    private OnFocusChangedListener mOnFocusChangedListener = null;

    private TextWatcher textWatcher = new EditTextWatcher();
    private boolean textWatcherActive = true;
    private boolean toBeDeleted = false;
    private boolean equationName = false;
    private boolean indexName = false;
    private boolean intermediateArgument = false;
    private boolean calculatedValue = false;
    private boolean requestFocusEnabled = true;

    // custom content types
    private boolean emptyEnabled = false;
    private boolean intervalEnabled = false;
    private boolean complexEnabled = true;
    private boolean comparatorEnabled = true;
    private boolean newTermEnabled = false;

    // context menu handling
    private ContextMenuHandler mMenuHandler = null;
    @Nullable
    private OnFormulaChangeListener mOnFormulaChangeListener = null;

    public FormulaEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    public FormulaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public FormulaEditText(Context context) {
        super(context);
        setup(context, null);
    }

    protected void setup(Context context, AttributeSet attrs) {
        ViewUtils.setShowSoftInputOnFocus(this, false);
        FontManager.setDefaultFont(context, this);

        mMenuHandler = new ContextMenuHandler(getContext());
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FormulaEditText, 0, 0);
            equationName = a.getBoolean(R.styleable.FormulaEditText_equationName, false);
            indexName = a.getBoolean(R.styleable.FormulaEditText_indexName, false);
            intermediateArgument = a.getBoolean(R.styleable.FormulaEditText_intermediateArgument, false);
            calculatedValue = a.getBoolean(R.styleable.FormulaEditText_calculatedValue, false);

            // custom content types
            emptyEnabled = a.getBoolean(R.styleable.FormulaEditText_emptyEnabled, false);
            intervalEnabled = a.getBoolean(R.styleable.FormulaEditText_intervalEnabled, false);
            complexEnabled = a.getBoolean(R.styleable.FormulaEditText_complexEnabled, true);
            comparatorEnabled = a.getBoolean(R.styleable.FormulaEditText_comparatorEnabled, comparatorEnabled);
            newTermEnabled = a.getBoolean(R.styleable.FormulaEditText_newTermEnabled, newTermEnabled);
            // menu
            mMenuHandler.initialize(a);
            a.recycle();
        }
    }

    public void setup(AppCompatActivity activity, OnFormulaChangeListener onFormulaChangeListener) {
        mActivity = activity;
        mOnFormulaChangeListener = onFormulaChangeListener;
        setOnLongClickListener(this);
        setOnFocusChangeListener(this);
        setSaveEnabled(false);
    }


    public boolean isEmptyEnabled() {
        return emptyEnabled;
    }

    public boolean isIntervalEnabled() {
        return intervalEnabled;
    }

    public boolean isComplexEnabled() {
        return complexEnabled;
    }

    public boolean isComparatorEnabled() {
        return comparatorEnabled;
    }

    public void setComparatorEnabled(boolean comparatorEnabled) {
        this.comparatorEnabled = comparatorEnabled;
    }

    public void setEquationEnable(boolean enable) {
        this.equationName = enable;
    }

    public boolean isNewTermEnabled() {
        return newTermEnabled;
    }


    public boolean isEquationName() {
        return equationName;
    }

    public boolean isIndexName() {
        return indexName;
    }

    public boolean isIntermediateArgument() {
        return intermediateArgument;
    }

    public boolean isCalculatedValue() {
        return calculatedValue;
    }

    public boolean isConversionEnabled() {
        return !isEquationName() && !isIndexName() && !isIntermediateArgument() && !isCalculatedValue();
    }

    public void updateTextSize(ScaledDimensions dimen, int termDepth, ScaledDimensions.Type paddingType) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen.getTextSize(termDepth));
        final int p = dimen.get(paddingType);
        setPadding(p, 0, p, 0);
        updateMinimumWidth(dimen);
    }

    public void updateMinimumWidth(ScaledDimensions dimen) {
        setMinimumWidth(length() == 0 ? dimen.get(ScaledDimensions.Type.TEXT_MIN_WIDTH) : 0);
    }

    public boolean isRequestFocusEnabled() {
        return requestFocusEnabled;
    }

    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        this.requestFocusEnabled = requestFocusEnabled;
    }


    @Override
    public int getBaseline() {
        return (this.getMeasuredHeight() - getPaddingBottom() + getPaddingTop()) / 2;
    }

    /**
     * Set the text watcher interface
     */
    public void setChangeListener(OnTextChangeListener onTextChangeListener, OnFocusChangedListener onFocusChangedListener) {
        this.mOnTextChangeListener = onTextChangeListener;
        this.mOnFocusChangedListener = onFocusChangedListener;
        setTextWatcher(true);
    }

    /**
     * Procedure activates/deactivates text watcher for this term field
     */
    public void setTextWatcher(boolean active) {
        if (active) {
            addTextChangedListener(textWatcher);
        } else {
            removeTextChangedListener(textWatcher);
        }
    }

    /**
     * Temporary activating/deactivating of text watcher
     */
    public void setTextWatcherActive(boolean textWatcherActive) {
        this.textWatcherActive = textWatcherActive;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOnTextChangeListener != null) {
            mOnTextChangeListener.onSizeChanged();
        }
    }

    @Override
    public boolean processDelKey(KeyEvent event) {
        if (DLog.DEBUG) DLog.d(TAG, "processDelKey() called with: event = [" + event + "]");
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            if (showRemoveThis()) {
                if (!toBeDeleted) {
                    toBeDeleted = true;
                    if (mOnFormulaChangeListener != null) {
                        mOnFormulaChangeListener.onDelete(this);
                        return true;
                    }
                }
            } else {
                backspace();
            }
        }
        return false;
    }

    private boolean showRemoveThis() {
        return getText().length() == 0 || getSelectionStart() == 0 && getSelectionEnd() == 0;
    }

    private void backspace() {
        int start = Math.max(getSelectionStart(), 0);
        int end = Math.max(getSelectionEnd(), 0);
        if (start != end) {
            getEditableText().delete(start, end);
        } else {
            getEditableText().delete(end - 1, end);
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (processDelKey(event)) {
            return true;
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (mOnFormulaChangeListener != null && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_V) {
            final String input = ClipboardManager.readFromClipboard(getContext(), true);
            mOnFormulaChangeListener.onPasteFromClipboard(this, input);
            return true;
        }
        return super.dispatchKeyShortcutEvent(event);
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!requestFocusEnabled) {
            return;
        }
        if (hasFocus && mOnFocusChangedListener != null) {
            setNextFocusDownId(mOnFocusChangedListener.onGetNextFocusId(this, OnFocusChangedListener.FocusType.FOCUS_DOWN));
            setNextFocusLeftId(mOnFocusChangedListener.onGetNextFocusId(this, OnFocusChangedListener.FocusType.FOCUS_LEFT));
            setNextFocusRightId(mOnFocusChangedListener.onGetNextFocusId(this, OnFocusChangedListener.FocusType.FOCUS_RIGHT));
            setNextFocusUpId(mOnFocusChangedListener.onGetNextFocusId(this, OnFocusChangedListener.FocusType.FOCUS_UP));
            setNextFocusForwardId(mOnFocusChangedListener.onGetNextFocusId(this, OnFocusChangedListener.FocusType.FOCUS_RIGHT));
        }
        if (mOnFormulaChangeListener != null) {
            mOnFormulaChangeListener.onFocus(view, hasFocus);
        }
    }

    /**
     * Procedure returns the parent action mode or null if there are no related mode
     */
    public ActionMode getActionMode() {
        return mMenuHandler.getActionMode();
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnFormulaChangeListener == null) {
            return false;
        }
        if (mMenuHandler.getActionMode() != null) {
            return true;
        }
        mMenuHandler.startActionMode(mActivity, view, mOnFormulaChangeListener);
        return true;
    }

    /*********************************************************
     * Context menu handling
     *********************************************************/

    @Override
    public boolean onTextContextMenuItem(int id) {
        return super.onTextContextMenuItem(id);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selEnd, selEnd);
    }

    @Override
    public void insert(String text) {
        if (!hasFocus()) requestFocus();
        int start = Math.max(getSelectionStart(), 0);
        int end = Math.max(getSelectionEnd(), 0);
        getEditableText().replace(start, end, "");
        getEditableText().insert(start, text);
    }

    @Override
    public boolean moveLeft() {
        int selectionStart = getSelectionStart();
        if (selectionStart == -1 || selectionStart == 0 || length() == 0) {
            return false;
        }
        setSelection(selectionStart - 1);
        return true;
    }

    @Override
    public boolean moveRight() {
        int selectionEnd = getSelectionEnd();
        if (selectionEnd == -1 || selectionEnd == length() || length() == 0) {
            return false;
        }
        setSelection(selectionEnd + 1);
        return true;
    }

    /**
     * Text change processing class
     */
    private class EditTextWatcher implements TextWatcher {
        EditTextWatcher() {
            // empty
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!textWatcherActive) {
                return;
            }
            if (mOnTextChangeListener != null) {
                mOnTextChangeListener.beforeTextChanged(s.toString(), true);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!textWatcherActive) {
                return;
            }
            if (mOnTextChangeListener != null) {
                mOnTextChangeListener.onTextChanged(s.toString(), true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
