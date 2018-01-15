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
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.formula.type.IntervalType;
import com.mkulesh.micromath.formula.type.LoopType;
import com.mkulesh.micromath.formula.type.OperatorType;
import com.mkulesh.micromath.formula.type.TermType;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.CalcEditText;
import com.mkulesh.micromath.widgets.CalcTextView;
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

    public static TermType getTermType(Context context, CalcEditText editText, String text, boolean ensureManualTrigger) {
        if (FormulaTermOperatorView.getOperatorType(context, text) != null) {
            return TermType.OPERATOR;
        }
        if (editText.isComparatorEnabled() && FormulaTermComparatorView.getComparatorType(context, text) != null) {
            return TermType.COMPARATOR;
        }

        // TermFunction has manual trigger (like "(" or "["): is has to be checked
        final boolean enableFunction = !ensureManualTrigger || FormulaTermFunctionView.containsFunctionTrigger(context, text);
        if (enableFunction && FormulaTermFunctionView.getFunctionType(context, text) != null) {
            return TermType.FUNCTION;
        }
        if (editText.isIntervalEnabled() && FormulaTermIntervalView.getIntervalType(context, text) != null) {
            return TermType.INTERVAL;
        }
        if (FormulaTermLoopView.getLoopType(context, text) != null) {
            return TermType.LOOP;
        }
        return null;
    }

    @Nullable
    public static String getOperatorCode(Context context, String code, boolean ensureManualTrigger) {
        OperatorType operatorType = FormulaTermOperatorView.getOperatorType(context, code);
        if (operatorType != null) {
            return operatorType.getLowerCaseName();
        }

        ComparatorType comparatorType = FormulaTermComparatorView.getComparatorType(context, code);
        if (comparatorType != null) {
            return comparatorType.getLowerCaseName();
        }

        // TermFunction has manual trigger (like "(" or "["): is has to be checked
        boolean enableFunction = !ensureManualTrigger || FormulaTermFunctionView.containsFunctionTrigger(context, code);
        FunctionType functionType = FormulaTermFunctionView.getFunctionType(context, code);
        if (enableFunction && functionType != null) {
            return functionType.getLowerCaseName();
        }

        IntervalType intervalType = FormulaTermIntervalView.getIntervalType(context, code);
        if (intervalType != null) {
            return intervalType.getLowerCaseName();
        }

        LoopType loopType = FormulaTermLoopView.getLoopType(context, code);
        if (loopType != null) {
            return loopType.getLowerCaseName();
        }

        return null;
    }

    public static FormulaTermView createTermView(TermType type, TermField termField, LinearLayout layout, String text,
                                                 int textIndex) throws Exception {
        if (DLog.DEBUG)
            DLog.d(TAG, "createTermView() called with: type = [" + type + "], termField = [" + termField
                    + "] text = [" + text + "], textIndex = [" + textIndex + "]");
        switch (type) {
            case OPERATOR:
                return new FormulaTermOperatorView(termField, layout, text, textIndex);
            case COMPARATOR:
                return new FormulaTermComparatorView(termField, layout, text, textIndex);
            case FUNCTION:
                return new FormulaTermFunctionView(termField, layout, text, textIndex);
            case INTERVAL:
                return new FormulaTermIntervalView(termField, layout, text, textIndex);
            case LOOP:
                return new FormulaTermLoopView(termField, layout, text, textIndex);
        }
        return null;
    }

    public static String createOperatorCode(Context contex, String code, String prevText) {
        String newValue = null;
        // operator
        final OperatorType t1 = FormulaTermOperatorView.getOperatorType(contex, code);
        if (t1 != null) {
            // for an operator, we add operator code to the end of line in order to move
            // existing text in the first term
            newValue = contex.getResources().getString(t1.getSymbolId());
            if (prevText != null) {
                newValue = prevText + newValue;
            }
        }
        // comparator
        final ComparatorType t2 = FormulaTermComparatorView.getComparatorType(contex, code);
        if (newValue == null && t2 != null) {
            // for a comparator, we add operator code to the end of line in order to move
            // existing text in the first term
            newValue = contex.getResources().getString(t2.getSymbolId());
            if (prevText != null) {
                newValue = prevText + newValue;
            }
        }

        // function
        final FunctionType t3 = FormulaTermFunctionView.getFunctionType(contex, code);
        if (newValue == null && t3 != null) {
            // for a function, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = (t3 == FunctionType.FUNCTION_LINK) ? code : t3.getLowerCaseName();
            if (prevText != null) {
                if (t3 != FunctionType.FUNCTION_LINK) {
                    newValue += contex.getResources().getString(R.string.formula_function_start_bracket);
                }
                newValue += prevText;
            }
        }
        // interval
        final IntervalType t4 = FormulaTermIntervalView.getIntervalType(contex, code);
        if (newValue == null && t4 != null) {
            // for an interval, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = contex.getResources().getString(t4.getSymbolId());
            if (prevText != null) {
                newValue += prevText;
            }
        }
        // loop
        final LoopType t5 = FormulaTermLoopView.getLoopType(contex, code);
        if (newValue == null && t5 != null) {
            // for a loop, we add operator code at the beginning of line in order to move
            // existing text in the function argument term
            newValue = contex.getResources().getString(t5.getSymbolId());
            if (prevText != null) {
                newValue += prevText;
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
    public abstract TermType getTermType();

    /**
     * Procedure returns code of this term. The code must be unique for a given term type
     */
    public abstract String getTermCode();

    /**
     * Procedure will be called for a custom text view initialization
     */
    protected abstract CalcTextView initializeSymbol(CalcTextView v);

    /**
     * Procedure will be called for a custom edit term initialization
     */
    protected abstract CalcEditText initializeTerm(CalcEditText v, LinearLayout l);


    @Override
    public void onCopyToClipboard() {
        ClipboardManager.copyToClipboard(getContext(), ClipboardManager.CLIPBOARD_TERM_OBJECT);
        // the difference between this and super implementation: we should store additional term code for term:
        getFormulaList().setStoredFormula(new FormulaClipboardData(getBaseType(), getTermCode(), onSaveInstanceState()));
    }

    @Override
    public int getNextFocusId(CalcEditText owner, OnFocusChangedListener.FocusType focusType) {
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
    protected void initializeElements(int idx) {
        boolean[] isValid = new boolean[mElements.size()];
        for (int i = 0; i < mElements.size(); i++) {
            View v = mElements.get(i);
            if (v instanceof CalcTextView) {
                isValid[i] = (initializeSymbol((CalcTextView) v) != null);
            } else if (v instanceof CalcEditText) {
                isValid[i] = (initializeTerm((CalcEditText) v, layout) != null);
            } else if (v instanceof LinearLayout) {
                initializeLayout((LinearLayout) v);
                isValid[i] = true;
            }
        }
        for (int i = mElements.size() - 1; i >= 0; i--) {
            View v = mElements.get(i);
            if (isValid[i]) {
                layout.addView(v, idx);
            } else {
                mElements.remove(v);
            }
        }
    }

    /**
     * This procedure performs recursive initialization of elements from included layouts
     */
    private void initializeLayout(LinearLayout l) {
        for (int k = 0; k < l.getChildCount(); k++) {
            View v = l.getChildAt(k);
            if (v instanceof CalcTextView) {
                initializeSymbol((CalcTextView) v);
            }
            if (v instanceof CalcEditText) {
                initializeTerm((CalcEditText) v, l);
            }
            if (v instanceof LinearLayout) {
                initializeLayout((LinearLayout) v);
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
            if (t instanceof CalcTextView) {
                ((CalcTextView) t).prepare(CalcTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList()
                        .getActivity(), this);
            } else if (t instanceof CalcEditText) {
                newArg = addTerm(getFormulaRoot(), expandableLayout, ++termIndex, (CalcEditText) t, this, addDepth);
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
                    && expandableLayout.getChildAt(startIndex + 1) instanceof CalcTextView) {
                final CalcTextView next = ((CalcTextView) expandableLayout.getChildAt(startIndex + 1));
                if (next.getText().toString().equals(sep)) {
                    count++;
                }
            } else if (!firstTerm && startIndex >= 1
                    && expandableLayout.getChildAt(startIndex - 1) instanceof CalcTextView) {
                final CalcTextView prev = ((CalcTextView) expandableLayout.getChildAt(startIndex - 1));
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
