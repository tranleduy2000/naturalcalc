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
package com.mkulesh.micromath.formula.views;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.calcbutton.Category;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.editstate.FormulaState;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData;
import com.mkulesh.micromath.formula.IArgumentHolder;
import com.mkulesh.micromath.formula.TermParser;
import com.mkulesh.micromath.formula.io.Constants;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.IdGenerator;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaLayout;
import com.mkulesh.micromath.widgets.OnFocusChangedListener;
import com.mkulesh.micromath.widgets.OnTextChangeListener;
import com.mkulesh.micromath.widgets.ScaledDimensions;
import com.nstudio.calc.casio.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.Locale;

public class TermField implements OnTextChangeListener, OnFocusChangedListener, ICalculable {
    public static final int NO_ERROR_ID = -1;
    /*
     * Constants used to save/restore the instance state.
     */
    private static final String STATE_TERM_ID = "_term_id";
    private static final String STATE_TEXT = "_text";
    private static final String STATE_CODE = "_code";
    private static final String STATE_INSTANCE = "_instance";
    private static final String TAG = "TermField";

    private final TermParser mParser = new TermParser();
    private final FormulaEditText mEditText;
    private final FormulaView mFormulaRoot, mParentFormula;
    private final LinearLayout mLayout;

    public int MAX_LAYOUT_DEPTH = 15;
    public int mTermDepth = 0;
    public boolean isWritable = true;
    public BracketsType bracketsType = BracketsType.ALWAYS;
    private String mTermKey = null;
    private boolean emptyOrAutoContent = true; // empty or automatically filled content
    private boolean textChangeDetectionEnabled = true;
    private ContentType mContentType = ContentType.INVALID;
    private String mErrorMsg = null;
    private ErrorNotification mErrorNotification = ErrorNotification.COLOR;
    private int mErrorId = NO_ERROR_ID;

    private FormulaTermView mTermView = null;
    private EquationView mLinkedVariable = null;

    public TermField(FormulaView formulaRoot, FormulaView parentFormula, LinearLayout layout,
                     int termDepth, FormulaEditText editText) {
        super();
        initLayoutDepth();

        mFormulaRoot = formulaRoot;
        mParentFormula = parentFormula;
        mLayout = layout;
        mTermDepth = termDepth;
        mTermKey = editText.getText().toString();
        mEditText = editText;
        mEditText.setText("");
        mEditText.setChangeListener(this, this);
        mEditText.setId(IdGenerator.generateId());

        updateViewColor();
    }

    /**
     * Procedure divides the given source string into two targets using the given divider
     */
    static void divideString(String src, String div, TermField leftTarget, TermField rightTarget) {
        if (div != null && src != null) {
            int opPosition = src.indexOf(div);
            if (opPosition >= 0) {
                try {
                    leftTarget.setText(src.subSequence(0, opPosition));
                    rightTarget.setText(src.subSequence(opPosition + div.length(), src.length()));
                } catch (Exception ex) {
                    // nothing to do
                }
            }
        }
    }


    private void initLayoutDepth() {
        MAX_LAYOUT_DEPTH = 25;
        if (Build.VERSION.SDK_INT < 23) {
            // This maximal layout depth was obtain for Lenovo P780
            MAX_LAYOUT_DEPTH = 15;
        }
        if (Build.VERSION.SDK_INT < 17) {
            // This maximal layout depth was obtain for Motorolla Xoom and Xtreamer Aiki
            MAX_LAYOUT_DEPTH = 9;
        }
        if (Build.VERSION.SDK_INT < 15) {
            // This maximal layout depth was obtain on Alcatel OT 911
            MAX_LAYOUT_DEPTH = 6;
        }

    }

    /**
     * Methods used recursively for the formula tree
     */
    public void collectElements(LinearLayout layout, ArrayList<View> out) {
        if (isTerm() && layout == mLayout) {
            mTermView.collectElements(layout, out);
        }
    }

    /**
     * Procedure checks term field content and sets the corresponding content type
     */
    public ContentType checkContentType() {
        mErrorMsg = null;
        mErrorNotification = ErrorNotification.COLOR;
        mErrorId = NO_ERROR_ID;
        mLinkedVariable = null;

        if (mEditText.isCalculatedValue()) {
            mContentType = ContentType.INFO_TEXT;
            updateViewColor();
            return mContentType;
        }

        if (mEditText.isEmptyEnabled() && isEmpty()) {
            mContentType = ContentType.EMPTY;
            updateViewColor();
            return mContentType;
        }

        if (isTerm()) {
            boolean contentValid = mTermView.isContentValid(FormulaView.ValidationPassType.VALIDATE_SINGLE_FORMULA);
            mContentType = contentValid ? ContentType.TERM : ContentType.INVALID;
            updateViewColor();
            return mContentType;
        }

        mContentType = ContentType.INVALID;
        mParser.setText(this, mFormulaRoot, mEditText);
        if (mEditText.isEquationName()) {
            // in this mode, only a name is allowed and shall be unique
            if (mParser.getFunctionName() != null && mParser.errorId == NO_ERROR_ID) {
                mContentType = ContentType.EQUATION_NAME;
            }
        } else {
            // in this mode, numbers and function pointers are allowed
            if (mParser.getValue() != null) {
                mContentType = ContentType.NUMBER;
            } else if (mParser.getArgumentHolder() != null && mParser.getArgumentIndex() != ViewUtils.INVALID_INDEX) {
                mContentType = ContentType.ARGUMENT;
            } else if (mParser.getLinkedVariableId() >= 0) {
                final FormulaView lv = mFormulaRoot.getFormulaList().getFormula(mParser.getLinkedVariableId());
                if (lv != null && lv instanceof EquationView) {
                    mContentType = ContentType.VARIABLE_LINK;
                    mLinkedVariable = (EquationView) lv;
                    if (mFormulaRoot instanceof LinkHolderView) {
                        ((LinkHolderView) mFormulaRoot).addLinkedEquation(mLinkedVariable);
                    }
                }
            }
        }
        updateViewColor();
        return mContentType;
    }

    /**
     * Procedure returns true if the calculation and content checking shall be skipped for this formula
     */
    private boolean disableCalculation() {
        return (getFormulaRoot() instanceof CalculationResultView)
                && ((CalculationResultView) getFormulaRoot()).disableCalculation();
    }

    /**
     * Procedure calculates recursively the formula value
     */
    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        if (isTerm()) {
            return mTermView.toExpressionString();
        } else {
            return mEditText.getText().toString();
        }
    }

    /**
     * Procedure searches the focused term recursively
     */
    public TermField findFocusedTerm() {
        if (isTerm()) {
            if (mTermView == mFormulaRoot.getFormulaList().getSelectedTerm()) {
                return this;
            }
            return mTermView.findFocusedTerm();
        }
        return (mEditText.isFocused() ? this : null);
    }

    /**
     * Procedure sets the focused term recursively
     */
    public boolean setEditableFocus(FormulaView.FocusType type) {
        if (isTerm()) {
            return mTermView.setEditableFocus(type);
        } else if (type == FormulaView.FocusType.FIRST_EDITABLE || isEmpty()) {
            mEditText.requestFocus();
            return true;
        } else if (type == FormulaView.FocusType.FOCUS_LEFT) {
            mEditText.requestFocus();
            mEditText.setSelection(0);
        } else if (type == FormulaView.FocusType.FOCUS_RIGHT) {
            mEditText.requestFocus();
            mEditText.setSelection(mEditText.length());
        }
        return false;
    }

    /**
     * Procedure updates the text size of this term depending on layout depth
     */
    public void updateTextSize() {
        if (isTerm()) {
            mTermView.updateTextSize();
        } else {
            mEditText.updateTextSize(mFormulaRoot.getFormulaList().getDimen(), mTermDepth,
                    ScaledDimensions.Type.HOR_TEXT_PADDING);
        }
    }

    /**
     * Procedure updates the text color of this term depending on layout depth
     */
    public void updateTextColor() {
        if (isTerm()) {
            mTermView.updateTextColor();
        } else {
            updateViewColor();
        }
    }


    @Override
    public void beforeTextChanged(String text, boolean isManualInput) {
        if (isManualInput) {
            if (mEditText.isNewTermEnabled()) {
                mFormulaRoot.getFormulaList().getUndoState().addEntry(mParentFormula.getState());
            } else {
                mFormulaRoot.getFormulaList().getUndoState().addEntry(getState());
            }
        }
    }

    @Override
    public void onTextChanged(String text, boolean isManualInput) {
        boolean converted = false;
        final boolean isEmpty = (text == null || text.length() == 0);
        if (textChangeDetectionEnabled) {
            emptyOrAutoContent = isEmpty;
        }
        if (!isEmpty && this.mEditText.isConversionEnabled()) {
            mTermView = convertToTerm(text, null, true);
            if (mTermView != null) {
                converted = true;
                requestFocus();
            }
        }
        if (!isEmpty && !converted && this.mEditText.isNewTermEnabled()) {
            if (mParentFormula.onNewTerm(this, text, true)) {
                return;
            }
        }
        if (!isTerm()) {
            // Do not call isContentValid since it deletes content of edit text
            // that causes unlimited recursive call of onTextChanged
            checkContentType();
        }
    }

    @Override
    public void onSizeChanged() {
        if (!isTerm() && mEditText.isFocused()) {
            mFormulaRoot.getFormulaList().getFormulaScrollView().scrollToChild(mEditText);
        }
    }

    @Override
    public int onGetNextFocusId(FormulaEditText owner, FocusType focusType) {
        return mParentFormula.getNextFocusId(mEditText, focusType);
    }


    /**
     * Parcelable interface: procedure writes the formula state
     */
    public void writeToBundle(Bundle bundle, String pref) {
        if (DLog.DEBUG)
            DLog.d(TAG, "writeToBundle() called with: bundle = [" + bundle + "], pref = [" + pref + "]");
        bundle.putInt(pref + STATE_TERM_ID, getTermId());
        if (!isTerm()) {
            bundle.putString(pref + STATE_TEXT, isEmptyOrAutoContent() ? "" : getText());
            bundle.putString(pref + STATE_CODE, "");
        } else {
            bundle.putString(pref + STATE_TEXT, "");
            bundle.putString(pref + STATE_CODE, mTermView.getTermCode());
            bundle.putParcelable(pref + STATE_INSTANCE, mTermView.onSaveInstanceState());
        }
    }

    /**
     * Parcelable interface: procedure reads the formula state
     */
    public void readFromBundle(Bundle bundle, String pref) {
        if (IdGenerator.enableIdRestore) {
            mEditText.setId(bundle.getInt(pref + STATE_TERM_ID));
            IdGenerator.compareAndSet(getTermId());
        }
        setText(bundle.getString(pref + STATE_TEXT));
        final String termCode = bundle.getString(pref + STATE_CODE);
        if (termCode != null && termCode.length() > 0) {
            mTermView = convertToTerm(termCode, bundle.getParcelable(pref + STATE_INSTANCE), false);
        }
    }

    /**
     * XML interface: procedure reads the formula state
     */
    public void readFromXml(XmlPullParser parser) throws Exception {
        final String text = parser.getAttributeValue(null, Constants.XML_PROP_TEXT);
        final String termCode = parser.getAttributeValue(null, Constants.XML_PROP_CODE);
        parser.require(XmlPullParser.START_TAG, Constants.XML_NS, parser.getName());
        boolean finishTag = true;
        if (termCode == null) {
            setText(text == null ? "" : text);
        } else {
            mTermView = convertToTerm(termCode, null, false);
            if (isTerm()) {
                setText("");
                mTermView.readFromXml(parser);
                finishTag = false;
            } else {
                throw new Exception("can not create term");
            }
        }
        if (finishTag) {
            while (parser.next() != XmlPullParser.END_TAG) ;
        }
    }

    /**
     * XML interface: procedure returns string that contains XML representation of this term
     */
    public void writeToXml(XmlSerializer serializer) throws Exception {
        if (!isTerm()) {
            serializer
                    .attribute(Constants.XML_NS, Constants.XML_PROP_TEXT, isEmptyOrAutoContent() ? "" : getText());
        } else {
            serializer.attribute(Constants.XML_NS, Constants.XML_PROP_CODE, mTermView.getTermCode());
            mTermView.writeToXml(serializer, getTermKey());
        }
    }

    /**
     * Procedure stores undo state for this term
     */
    public FormulaState getState() {
        Bundle bundle = new Bundle();
        writeToBundle(bundle, "");
        return new FormulaState(mFormulaRoot.getId(), getTermId(), bundle);
    }

    /*********************************************************
     * Undo feature
     *********************************************************/

    public void undo(FormulaState state) {
        if (state.data instanceof Bundle) {
            clear();
            readFromBundle((Bundle) state.data, "");
        }
    }

    /**
     * Procedure returns the context for this term field
     */
    public Context getContext() {
        return mFormulaRoot.getFormulaList().getContext();
    }

    /**
     * Procedure returns the parent layout
     */
    public LinearLayout getLayout() {
        return mLayout;
    }

    /**
     * Procedure returns root formula for this term
     */
    public FormulaView getFormulaRoot() {
        return mFormulaRoot;
    }

    /**
     * Procedure returns parent formula
     */
    public FormulaView getParentFormula() {
        return mParentFormula;
    }

    /**
     * Procedure actual text for this term field
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    /**
     * Procedure sets given text for this term field
     */
    public void setText(CharSequence text) {
        if (textChangeDetectionEnabled) {
            // this check duplicates the same check in the onTextChanged since onTextChanged is not always called
            emptyOrAutoContent = (text == null || text.length() == 0);
        }
        mEditText.setTextWatcherActive(false);
        if (text == null) {
            text = "";
        }
        mEditText.setText(text);
        onTextChanged(text.toString(), false);
        mEditText.setTextWatcherActive(true);
    }

    /**
     * Procedure returns associated edit text component
     */
    public FormulaEditText getEditText() {
        return mEditText;
    }

    /**
     * Procedure checks whether there is a valid term
     */
    public boolean isTerm() {
        return mTermView != null;
    }

    /**
     * Procedure checks whether this term is empty
     */
    public boolean isEmpty() {
        return !isTerm() && mEditText.getText().length() == 0;
    }

    /**
     * Procedure returns the associated term
     */
    public FormulaTermView getTerm() {
        return mTermView;
    }

    /**
     * Procedure returns the term id
     */
    public int getTermId() {
        return mEditText.getId();
    }

    /**
     * Procedure returns the unique key of this term
     */
    public String getTermKey() {
        return mTermKey;
    }

    /**
     * Procedure sets the unique key of this term
     */
    public void setTermKey(String termKey) {
        this.mTermKey = termKey;
    }

    /**
     * Procedure returns the type of parsed content
     */
    public ContentType getContentType() {
        return mContentType;
    }

    /**
     * Procedure returns associated parser
     */
    public TermParser getParser() {
        return mParser;
    }

    /**
     * Procedure returns whether this field empty or contains automatically filled content
     */
    public boolean isEmptyOrAutoContent() {
        return !isTerm() && (isEmpty() || emptyOrAutoContent);
    }

    public void setTextChangeDetectionEnabled(boolean textChangeDetectionEnabled) {
        this.textChangeDetectionEnabled = textChangeDetectionEnabled;
    }

    /**
     * Procedure updates the border and color of edit text depends on its content
     */
    private void updateViewColor() {
        // flag whether an error is detected: for a formula that will be not calculated,
        // errors will be not shown
        final boolean errorDetected = !disableCalculation() && (mErrorId != NO_ERROR_ID || mErrorMsg != null);

        // layout border
        if (mLayout instanceof FormulaLayout) {
            if (isTerm() && mErrorNotification == ErrorNotification.LAYOUT_BORDER && errorDetected) {
                ((FormulaLayout) mLayout).setContentValid(false);
                return;
            } else {
                ((FormulaLayout) mLayout).setContentValid(true);
            }
        }

        // text border
        if (mEditText.isSelected()) {
            CompatUtils.updateBackgroundAttr(getContext(), mEditText,
                    R.drawable.formula_term_background, R.attr.colorFormulaSelected);
        } else if (errorDetected) {
            CompatUtils.updateBackgroundAttr(getContext(), mEditText,
                    R.drawable.formula_term_border, R.attr.colorFormulaInvalid);
        } else if (isEmpty()) {
            final int attrId = (mEditText.isEmptyEnabled()) ?
                    R.attr.colorFormulaEmpty : R.attr.colorFormulaInvalid;
            CompatUtils.updateBackgroundAttr(getContext(), mEditText,
                    R.drawable.formula_term_border, attrId);
        } else {
            CompatUtils.updateBackground(getContext(), mEditText, R.drawable.formula_term);
        }

        // text color
        {
            int resId = R.attr.colorFormulaNormal;
            if (!disableCalculation() && mContentType == ContentType.INVALID
                    && mErrorNotification == ErrorNotification.COLOR) {
                resId = R.attr.colorFormulaInvalid;
            } else if (mEditText.isCalculatedValue() || (!isEmpty() && isEmptyOrAutoContent())) {
                resId = R.attr.colorFormulaCalculatedValue;
            }
            mEditText.setTextColor(CompatUtils.getThemeColorAttr(getContext(), resId));
        }

        // update minimum width depending on content
        mEditText.updateMinimumWidth(mFormulaRoot.getFormulaList().getDimen());
    }

    /**
     * Procedure check that the current formula depth has no conflicts with allowed formula depth
     */
    public boolean checkFormulaDepth() {
        repairTermDepth(false);
        final int layoutDepth = ViewUtils.getLayoutDepth(mLayout);
        return (layoutDepth <= MAX_LAYOUT_DEPTH);
    }

    /**
     * Procedure check that the term has no conflicts with allowed formula depth
     */
    private void repairTermDepth(boolean showToast) {
        if (isTerm() && !mTermView.checkFormulaDepth()) {
            clear();
            if (showToast) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.error_max_layout_depth),
                        Toast.LENGTH_SHORT).show();
            } else {
                mFormulaRoot.getFormulaList().getFormulaListView().setTermDeleted(true);
            }
        }
    }

    /**
     * Procedure converts this term field to an other term
     */
    private FormulaTermView convertToTerm(String termCode, Parcelable restore, boolean ensureManualTrigger) {
        FormulaTermType.TermType targetType = FormulaTermView.getTermType(getContext(), mEditText,
                termCode, ensureManualTrigger);
        mTermView = null;
        if (targetType != null) {
            try {
                final int textIndex = ViewUtils.getViewIndex(mLayout, mEditText); // store view index before it will be removed
                mEditText.setTextWatcher(false);
                if (mEditText.isFocused()) {
                    mFormulaRoot.getFormulaList().clearFocus();
                }
                mLayout.removeView(mEditText);

                mTermView = FormulaTermView.createTermView(targetType, this, mLayout, termCode, textIndex);
                mTermView.updateTextSize();
            } catch (Exception ex) {
                ViewUtils.debug(this, ex.getLocalizedMessage());
                mLayout.addView(mEditText);
                mEditText.setTextWatcher(true);
            }
        }
        repairTermDepth(true);
        if (isTerm()) {
            setText("");
            if (restore != null) {
                mTermView.onRestoreInstanceState(restore);
            }
        }
        return mTermView;
    }

    /**
     * Procedure adds the given operator code to this term
     */
    public void addOperatorCode(@NonNull String code) {
        if (DLog.DEBUG) DLog.d(TAG, "addOperatorCode() called with: code = [" + code + "]");

        if (BaseType.TERM.toString().equals(code.toUpperCase(Locale.ENGLISH))) {
            code = getContext().getResources().getString(R.string.formula_term_separator);
        }
        if (mEditText.isNewTermEnabled()) {
            mFormulaRoot.getFormulaList().getUndoState().addEntry(mParentFormula.getState());
            if (mParentFormula.onNewTerm(this, code, true)) {
                return;
            }
        }

        if (FormulaTermView.getOperatorCode(getContext(), code, false) == null) {
            return;
        }

        mFormulaRoot.getFormulaList().getUndoState().addEntry(getState());

        //comparator change
        ComparatorType comparatorType = FormulaComparatorView.getComparatorType(getContext(), code);
        if (isTerm() && comparatorType != null) {
            if (getTerm() instanceof FormulaComparatorView) {
                if (((FormulaComparatorView) getTerm()).changeComparatorType(comparatorType)) {
                    return;
                }
            }
        }

        mEditText.setRequestFocusEnabled(false);
        Bundle savedState = null;
        final String saveKey = "savedState";
        if (isTerm()) {
            savedState = new Bundle();
            writeToBundle(savedState, saveKey);
            clear();
        }

        int splitIndex = mEditText.getSelectionStart() != mEditText.getSelectionEnd() ? -1 : mEditText.getSelectionStart();
        String newValue = FormulaTermView.createOperatorCode(getContext(), code, getText(), splitIndex);
        if (newValue != null) {
            onTextChanged(newValue, false);
        }
        if (isTerm() && !mTermView.getTerms().isEmpty() && savedState != null) {
            final TermField args = mTermView.getArgumentTerm();
            if (args != null && args.getEditText() != null && args.getEditText().isConversionEnabled()) {
                args.readFromBundle(savedState, saveKey);
            }
        }
        repairTermDepth(true);
        mEditText.setRequestFocusEnabled(true);
        requestFocus();
    }

    /**
     * Procedure reads this term field from the given bundle
     */
    public void readStoredFormula(FormulaClipboardData data) {
        if (mEditText.isConversionEnabled()) {
            mTermView = convertToTerm(data.getSingleData().termCode, data.getSingleData().data, false);
        } else {
            final String error = mFormulaRoot.getFormulaList().getActivity().getResources()
                    .getString(R.string.error_paste_term_into_text);
            Toast.makeText(mFormulaRoot.getFormulaList().getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback used to indicate that a child term terminates itself
     */
    public void onTermDelete(int index, TermField remainingTerm) {
        mFormulaRoot.getFormulaList().getUndoState().addEntry(getState());
        deleteTerm(index, remainingTerm, "");
    }

    public void onTermDeleteWithText(int idx, CharSequence newText) {
        mFormulaRoot.getFormulaList().getUndoState().addEntry(getState());
        deleteTerm(idx, null, newText);
    }

    /**
     * Procedure deletes the term and restores the edit text field
     */
    private void deleteTerm(int index, TermField remainingTerm, CharSequence newText) {
        if (!isTerm()) {
            // onTermDelete can be only called from the valid term
            return;
        }
        mTermView = null;
        mLayout.addView(mEditText, index);
        mEditText.setSelected(false);
        if (remainingTerm != null) {
            setText(remainingTerm.getText());
            if (remainingTerm.mTermView != null) {
                Parcelable p = remainingTerm.mTermView.onSaveInstanceState();
                mTermView = convertToTerm(remainingTerm.mTermView.getTermCode(), p, false);
            }
        } else {
            setText(newText);
        }
        if (!isTerm()) {
            mEditText.setTextWatcher(true);
            updateTextSize();
        }
        checkContentType();
        requestFocus();
    }

    /**
     * Procedure deletes content of this term field
     */
    public void clear() {
        boolean flag = mEditText.isRequestFocusEnabled();
        mEditText.setRequestFocusEnabled(false);
        if (isTerm()) {
            deleteTerm(mTermView.removeElements(), null, "");
        } else {
            setText("");
        }
        mEditText.setRequestFocusEnabled(flag);
    }

    /**
     * If there is a parsing error, it will be shown
     */
    public void showParsingError() {
        /*if (DLog.DEBUG) DLog.d(TAG, "showParsingError() called");
        final String errMsg = findErrorMsg();
        if (errMsg != null) {
            Toast.makeText(mFormulaRoot.getContext(), errMsg, Toast.LENGTH_SHORT).show();
        } else if (mErrorId != NO_ERROR_ID) {
            Toast.makeText(mFormulaRoot.getContext(), mFormulaRoot.getContext().getResources().getString(mErrorId),
                    Toast.LENGTH_SHORT).show();
        } else if (!isEmpty() && mContentType == ContentType.INVALID && mParser.errorId != NO_ERROR_ID) {
            Toast.makeText(mFormulaRoot.getContext(), mFormulaRoot.getContext().getResources().getString(mParser.errorId),
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    /**
     * Procedure sets the error data into the term
     */
    public void setError(String errorMsg, ErrorNotification errorNotification, FormulaLayout parentLayout) {
        this.mErrorId = NO_ERROR_ID;
        this.mErrorMsg = errorMsg;
        this.mErrorNotification = errorNotification;
        if (parentLayout != null) {
            parentLayout.setContentValid(true);
        }
        if (mErrorId != NO_ERROR_ID || errorMsg != null) {
            mContentType = ContentType.INVALID;
            if (!disableCalculation() && errorNotification == ErrorNotification.PARENT_LAYOUT) {
                parentLayout.setContentValid(false);
            }
        }
        updateViewColor();
    }

    public void requestFocus() {
        if (mEditText.isRequestFocusEnabled()) {
            if (!setEditableFocus(FormulaView.FocusType.FIRST_EMPTY)) {
                setEditableFocus(FormulaView.FocusType.FIRST_EDITABLE);
            }
        }
    }

    /**
     * Check whether this term is enabled for the given palette
     */
    public boolean isEnableButton(Category category) {
        if (isTerm() && mTermView instanceof FormulaTermIntervalView && category != Category.NEW_TERM) {
            return false;
        }
        switch (category) {
            case NEW_TERM:
                return mParentFormula.isNewTermEnabled() && mEditText.isNewTermEnabled();
            case TOP_LEVEL_TERM:
                return mEditText.isIntervalEnabled();
            case CONVERSION:
                return mEditText.isConversionEnabled();
            case COMPARATOR:
                return mEditText.isComparatorEnabled();
        }
        return false;
    }

    /**
     * Check whether this term depends on given equation
     */
    public boolean dependsOn(EquationView view) {
        if (isTerm()) {
            return mTermView.dependsOn(view);
        } else if (mContentType == ContentType.VARIABLE_LINK && mLinkedVariable != null) {
            return mLinkedVariable.getId() == view.getId();
        }
        return false;
    }

    /**
     * Procedure search an owner argument holder that defines (holds) the given argument
     */
    public IArgumentHolder findArgumentHolder(String argumentName) {
        FormulaView parent = getParentFormula();
        while (parent != null) {
            if (parent instanceof IArgumentHolder) {
                IArgumentHolder argHolder = (IArgumentHolder) parent;
                if (argHolder.getArgumentIndex(argumentName) != ViewUtils.INVALID_INDEX) {
                    return argHolder;
                }
            }
            if (parent.getParentField() != null) {
                parent = parent.getParentField().getParentFormula();
            } else {
                break;
            }
        }
        return null;
    }

    /**
     * Procedure search an externally set error in the parent terms
     */
    public String findErrorMsg() {
        if (mErrorMsg != null) {
            return mErrorMsg;
        }
        FormulaView parent = getParentFormula();
        while (parent != null) {
            final TermField tf = parent.getParentField();
            if (tf != null) {
                if (tf.mErrorMsg != null) {
                    return tf.mErrorMsg;
                }
                parent = tf.getParentFormula();
            } else {
                break;
            }
        }
        return null;
    }

    // content type and content parser
    public enum ContentType {
        INVALID,
        EMPTY,
        INFO_TEXT,
        TERM,
        EQUATION_NAME,
        NUMBER,
        ARGUMENT,
        VARIABLE_LINK
    }

    public enum BracketsType {
        NEVER,
        IFNECESSARY,
        ALWAYS
    }

    // custom errors that can be set externally
    public enum ErrorNotification {
        COLOR,
        LAYOUT_BORDER,
        PARENT_LAYOUT
    }

}
