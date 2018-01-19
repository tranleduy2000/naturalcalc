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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.duy.common.utils.DLog;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.formula.type.IntervalType;
import com.mkulesh.micromath.formula.type.LoopType;
import com.mkulesh.micromath.formula.type.OperatorType;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.mkulesh.micromath.widgets.FormulaLayout;
import com.mkulesh.micromath.widgets.OnFocusChangedListener;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

public abstract class FormulaTermView extends FormulaView implements ICalculable {
    private static final String TAG = "FormulaTermView";
    private final FormulaView mFormulaRoot;
    protected FormulaLayout mFunctionMainLayout = null;

    public FormulaTermView(FormulaView formulaRoot, LinearLayout layout, int termDepth) {
        super(formulaRoot.getFormulaList(), layout, termDepth);
        this.mFormulaRoot = formulaRoot;
    }


    public FormulaTermView() {
        super(null, null, 0);
        this.mFormulaRoot = null;
    }

    public static FormulaTermType.TermType getTermType(Context context, FormulaEditText editText, String text, boolean ensureManualTrigger) {
        if (FormulaBinaryOperatorView.getOperatorType(context, text) != null) {
            return FormulaTermType.TermType.OPERATOR;
        }
        if (editText.isComparatorEnabled() && FormulaComparatorView.getComparatorType(context, text) != null) {
            return FormulaTermType.TermType.COMPARATOR;
        }

        // TermFunction has manual trigger (like "(" or "["): is has to be checked
        final boolean enableFunction = !ensureManualTrigger || FormulaFunctionView.containsFunctionTrigger(context, text);
        if (enableFunction && FormulaFunctionView.getFunctionType(context, text) != null) {
            return FormulaTermType.TermType.FUNCTION;
        }
        if (editText.isIntervalEnabled() && FormulaTermIntervalView.getIntervalType(context, text) != null) {
            return FormulaTermType.TermType.INTERVAL;
        }
        if (FormulaTermLoopView.getLoopType(context, text) != null) {
            return FormulaTermType.TermType.LOOP;
        }
        return null;
    }

    @Nullable
    public static String getOperatorCode(Context context, String code, boolean ensureManualTrigger) {
        OperatorType operatorType = FormulaBinaryOperatorView.getOperatorType(context, code);
        if (operatorType != null) {
            return operatorType.getCode();
        }

        ComparatorType comparatorType = FormulaComparatorView.getComparatorType(context, code);
        if (comparatorType != null) {
            return comparatorType.getCode();
        }

        // TermFunction has manual trigger (like "(" or "["): is has to be checked
        boolean enableFunction = !ensureManualTrigger || FormulaFunctionView.containsFunctionTrigger(context, code);
        FunctionType functionType = FormulaFunctionView.getFunctionType(context, code);
        if (enableFunction && functionType != null) {
            return functionType.getCode();
        }

        IntervalType intervalType = FormulaTermIntervalView.getIntervalType(context, code);
        if (intervalType != null) {
            return intervalType.getCode();
        }

        LoopType loopType = FormulaTermLoopView.getLoopType(context, code);
        if (loopType != null) {
            return loopType.getCode();
        }

        return null;
    }

    public static FormulaTermView createTermView(FormulaTermType.TermType type, TermField termField,
                                                 LinearLayout layout, String text, int viewIndex) throws Exception {
        if (DLog.DEBUG)
            DLog.d(TAG, "createTermView() called with: type = [" + type + "], termField = [" + termField
                    + "] text = [" + text + "], textIndex = [" + viewIndex + "]");
        switch (type) {
            case OPERATOR:
                return new FormulaBinaryOperatorView(termField, layout, text, viewIndex);
            case COMPARATOR:
                return new FormulaComparatorView(termField, layout, text, viewIndex);
            case FUNCTION:
                return new FormulaFunctionView(termField, layout, text, viewIndex);
            case INTERVAL:
                return new FormulaTermIntervalView(termField, layout, text, viewIndex);
            case LOOP:
                return new FormulaTermLoopView(termField, layout, text, viewIndex);
        }
        return null;
    }

    public static String createOperatorCode(Context context, String code, String allText, int splitIndex) {
        String left = allText;
        String right = "";
        if (splitIndex >= 0 && splitIndex < allText.length()) {
            left = allText.substring(0, splitIndex);
            right = allText.substring(splitIndex);
        }

        String newValue = null;
        // operator
        final OperatorType operatorType = FormulaBinaryOperatorView.getOperatorType(context, code);
        if (operatorType != null) {
            // for an operator, we add operator code to the end of line in order to move
            // existing text in the first term
            String opStr = context.getResources().getString(operatorType.getSymbolId());
            if (left != null) {
                newValue = left + opStr + right;
            }
        }

        // comparator
        final ComparatorType comparatorType = FormulaComparatorView.getComparatorType(context, code);
        if (newValue == null && comparatorType != null) {
            // for a comparator, we add operator code to the end of line in order to move
            // existing text in the first term
            newValue = context.getResources().getString(comparatorType.getSymbolId());
            if (left != null) {
                newValue = left + newValue;
            }
        }

        // function
        final FunctionType t3 = FormulaFunctionView.getFunctionType(context, code);
        if (newValue == null && t3 != null) {
            // for a function, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = (t3 == FunctionType.FUNCTION_LINK) ? code : t3.getCode();
            if (left != null) {
                if (t3 != FunctionType.FUNCTION_LINK) {
                    newValue += context.getResources().getString(R.string.formula_function_start_bracket);
                }
                newValue += left;
            }
        }
        // interval
        final IntervalType t4 = FormulaTermIntervalView.getIntervalType(context, code);
        if (newValue == null && t4 != null) {
            // for an interval, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = context.getResources().getString(t4.getSymbolId());
            if (left != null) {
                newValue += left;
            }
        }
        // loop
        final LoopType t5 = FormulaTermLoopView.getLoopType(context, code);
        if (newValue == null && t5 != null) {
            // for a loop, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = context.getResources().getString(t5.getSymbolId());
            if (left != null) {
                newValue += left;
            }
        }
        return newValue;
    }


    @Override
    public String toString() {
        return "Term " + getTermType().toString() + " " + getTermCode() + ", depth=" + termDepth;
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.TERM;
    }

    /**
     * Procedure returns the type of this term formula
     */
    public abstract FormulaTermType.TermType getTermType();

    /**
     * Procedure returns code of this term. The code must be unique for a given term type
     */
    public abstract String getTermCode();

    /**
     * Procedure will be called for a custom text view initialization
     */
    protected abstract FormulaTextView initializeSymbol(FormulaTextView v);

    /**
     * Procedure will be called for a custom edit term initialization
     */
    protected abstract FormulaEditText initializeTerm(FormulaEditText child, LinearLayout parent);


    @Override
    public void onCopyToClipboard() {
        ClipboardManager.copyToClipboard(getContext(), ClipboardManager.CLIPBOARD_TERM_OBJECT);
        // the difference between this and super implementation: we should store additional term code for term:
        getFormulaList().setStoredFormula(new FormulaClipboardData(getBaseType(), getTermCode(), onSaveInstanceState()));
    }

    @Override
    public int getNextFocusId(FormulaEditText owner, OnFocusChangedListener.FocusType focusType) {
        if (mFormulaRoot != null
                && owner != null
                && (focusType == OnFocusChangedListener.FocusType.FOCUS_UP || focusType == OnFocusChangedListener.FocusType.FOCUS_DOWN)) {
            return mFormulaRoot.getNextFocusId(owner, focusType);
        }
        return super.getNextFocusId(owner, focusType);
    }

    /**
     * Getter for main term
     */
    public TermField getArgumentTerm() {
        return getTerms().size() > 0 ? getTerms().get(0) : null;
    }


    /**
     * Getter for parent root formula
     */
    public FormulaView getFormulaRoot() {
        return mFormulaRoot;
    }

    /**
     * This procedure shall be called in order to prepare all visual elements
     */
    protected void initializeElements(int index) {
        boolean[] isValid = new boolean[mElements.size()];
        for (int i = 0; i < mElements.size(); i++) {
            View child = mElements.get(i);
            if (child instanceof FormulaTextView) {
                isValid[i] = (initializeSymbol((FormulaTextView) child) != null);
            } else if (child instanceof FormulaEditText) {
                isValid[i] = (initializeTerm((FormulaEditText) child, layout) != null);
            } else if (child instanceof LinearLayout) {
                initializeLayout((LinearLayout) child);
                isValid[i] = true;
            }
        }

        for (int i = mElements.size() - 1; i >= 0; i--) {
            View child = mElements.get(i);
            if (isValid[i]) {
                layout.addView(child, index);
            } else {
                mElements.remove(child);
            }
        }
    }

    /**
     * This procedure performs recursive initialization of elements from included layouts
     */
    private void initializeLayout(LinearLayout parent) {
        for (int k = 0; k < parent.getChildCount(); k++) {
            View child = parent.getChildAt(k);
            if (child instanceof FormulaTextView) {
                initializeSymbol((FormulaTextView) child);
            }
            if (child instanceof FormulaEditText) {
                initializeTerm((FormulaEditText) child, parent);
            }
            if (child instanceof LinearLayout) {
                initializeLayout((LinearLayout) child);
            }
        }
    }

    /**
     * Procedure adds new argument layout for this function
     */
    protected TermField addArgument(TermField startField, int argLayoutId, int addDepth) {
        // target layout where terms will be added
        View expandable = startField.getLayout();
        if (expandable == null) {
            return null;
        }
        LinearLayout expandableLayout = (LinearLayout) expandable;

        // view index of the field within the target layout and within the terms vector
        int viewIndex = -1;
        if (startField.isTerm()) {
            ArrayList<View> list = new ArrayList<>();
            startField.getTerm().collectElements(expandableLayout, list);
            for (View l : list) {
                viewIndex = Math.max(viewIndex, ViewUtils.getViewIndex(expandableLayout, l));
            }
        } else {
            viewIndex = ViewUtils.getViewIndex(expandableLayout, startField.getEditText());
        }
        int termIndex = mTerms.indexOf(startField);
        if (viewIndex < 0 || termIndex < 0) {
            return null;
        }

        // collect terms to be added
        ArrayList<View> newTerms = new ArrayList<>();
        inflateElements(newTerms, argLayoutId, true);
        TermField newArg = null;
        for (View t : newTerms) {
            if (t instanceof FormulaTextView) {
                ((FormulaTextView) t).prepare(FormulaTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList()
                        .getActivity(), this);
            } else if (t instanceof FormulaEditText) {
                newArg = addTerm(getFormulaRoot(), expandableLayout, ++termIndex, (FormulaEditText) t, this, addDepth);
                newArg.bracketsType = TermField.BracketsType.NEVER;
            }
            expandableLayout.addView(t, ++viewIndex);
        }
        reIndexTerms();
        return newArg;
    }

    /**
     * Procedure deletes argument layout for given term and returns the previous term
     */
    protected TermField deleteArgument(TermField owner, String sep, boolean storeUndoState) {
        // target layout where terms will be deleted
        View expandable = owner.getLayout();
        if (expandable == null) {
            return null;
        }
        LinearLayout expandableLayout = (LinearLayout) expandable;

        // view index of the field within the parent layout
        int startIndex = ViewUtils.getViewIndex(expandableLayout, owner.getEditText());
        if (startIndex < 0) {
            return null;
        }

        // how much views shall be deleted:
        int count = 1;
        {
            final String termKey = getContext().getResources().getString(R.string.formula_arg_term_key);
            final boolean firstTerm = owner.getTermKey().equals(termKey + String.valueOf(1));
            if (firstTerm && startIndex + 1 < expandableLayout.getChildCount()
                    && expandableLayout.getChildAt(startIndex + 1) instanceof FormulaTextView) {
                final FormulaTextView next = ((FormulaTextView) expandableLayout.getChildAt(startIndex + 1));
                if (next.getText().toString().equals(sep)) {
                    count++;
                }
            } else if (!firstTerm && startIndex >= 1
                    && expandableLayout.getChildAt(startIndex - 1) instanceof FormulaTextView) {
                final FormulaTextView prev = ((FormulaTextView) expandableLayout.getChildAt(startIndex - 1));
                if (prev.getText().toString().equals(sep)) {
                    startIndex--;
                    count++;
                }
            }
        }

        if (storeUndoState && parentField != null) {
            getFormulaList().getUndoState().addEntry(parentField.getState());
        }
        int prevIndex = mTerms.indexOf(owner);
        prevIndex--;
        mTerms.remove(owner);
        expandableLayout.removeViews(startIndex, count);
        reIndexTerms();

        return (prevIndex >= 0) ? mTerms.get(prevIndex) : null;
    }

    /**
     * Procedure performs re-index of terms
     */
    private void reIndexTerms() {
        if (mTerms.size() == 1) {
            mTerms.get(0).setTermKey(getContext().getResources().getString(R.string.formula_arg_term_key));
        } else {
            int i = 1;
            for (TermField t : mTerms) {
                t.setTermKey(getContext().getResources().getString(R.string.formula_arg_term_key) + String.valueOf(i++));
            }
        }
    }

    /**
     * Check whether this term depends on given equation
     */
    public boolean dependsOn(EquationView e) {
        for (TermField t : mTerms) {
            if (t.dependsOn(e)) {
                return true;
            }
        }
        return false;
    }

    protected void initializeMainLayout() {
        // store the main layout in order to show errors
        String funTag = getContext().getResources().getString(R.string.function_main_layout);
        View functionMainView = layout.findViewWithTag(funTag);
        if (functionMainView != null) {
            mFunctionMainLayout = (FormulaLayout) functionMainView;
            mFunctionMainLayout.setTag("");
        }
    }


}
