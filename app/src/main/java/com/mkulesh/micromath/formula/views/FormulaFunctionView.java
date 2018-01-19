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
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.BracketParser;
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.formula.type.FunctionTrigger;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.mkulesh.micromath.widgets.ScaledDimensions;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

public class FormulaFunctionView extends FormulaTermView {
    public static final String FUNCTION_ARGS_MARKER = ":";
    private static final String TAG = "FormulaFunctionView";
    private FunctionType mFunctionType = null;
    private String mFunctionLinkName = "unknown";
    private FormulaTextView mFunctionTerm = null;
    private EquationView mLinkedFunction = null;

    public FormulaFunctionView(TermField owner, LinearLayout layout, String code, int index) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(code, index);
    }

    public FormulaFunctionView(Context context) {
        super();
    }

    public FormulaFunctionView(Context context, AttributeSet attrs) {
        super();
    }

    @Nullable
    public static FunctionType getFunctionType(Context context, String allText) {
        String funcName = null;
        final Resources res = context.getResources();

        // cat the function name
        int bracketId = ViewUtils.INVALID_INDEX;
        for (int id : BracketParser.START_BRACKET_IDS) {
            final String startBracket = res.getString(id);
            if (allText.contains(startBracket)) {
                funcName = allText.substring(0, allText.indexOf(startBracket)).trim();
                bracketId = id;
                break;
            }
        }

        // search the function name in the types array
        for (FunctionType type : FunctionType.values()) {
            if (type.isLink() && allText.contains(type.getLinkObject())) {
                return type;
            }
            if (allText.equalsIgnoreCase(type.getCode())) {
                return type;
            }
            if (funcName != null && funcName.equalsIgnoreCase(type.getCode())) {
                return type;
            }
        }

        // special case (just brackets)
        if (funcName != null && funcName.length() == 0 && bracketId != ViewUtils.INVALID_INDEX) {
            if (bracketId == BracketParser.START_BRACKET_IDS[BracketParser.FUNCTION_BRACKETS]) {
                // an identity function (just brackets) is a special case of a function
                return FunctionType.IDENTITY_LAYOUT;
            } else {
                // index only valid if fName not empty
                return null;
            }
        }

        // if function is not yet found, check the trigger
        for (FunctionTrigger t : FunctionTrigger.values()) {
            if (t.getFunctionType() != null && allText.contains(res.getString(t.getCodeId()))) {
                return t.getFunctionType();
            }
        }

        // default case: function link
        if (funcName != null) {
            return FunctionType.FUNCTION_LINK;
        }
        return null;
    }

    @NonNull
    public static String getFunctionString(FunctionType type) {
        return type.isLink() ? type.getLinkObject() : type.getCode();
    }

    public static boolean containsFunctionTrigger(Context context, String text) {
        for (FunctionTrigger functionTrigger : FunctionTrigger.values()) {
            if (text.contains(context.getResources().getString(functionTrigger.getCodeId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Procedure creates the formula layout
     */
    private void onCreate(String code, int index) throws Exception {
        if (index < 0 || index > layout.getChildCount()) {
            throw new Exception("cannot create FormulaFunction for invalid insertion index " + index);
        }
        mFunctionType = getFunctionType(getContext(), code);
        if (mFunctionType == null) {
            throw new Exception("cannot create FormulaFunction for unknown function");
        }
        int argNumber = mFunctionType.getArgNumber();
        switch (mFunctionType) {
            case FUNCTION_LINK:
                mFunctionLinkName = getFunctionLinkName(code, mFunctionType,
                        R.string.formula_function_start_bracket);
                if (mFunctionLinkName == null) {
                    throw new Exception("cannot create FormulaFunction(FUNCTION_LINK) since function name is invalid");
                }
                inflateElements(R.layout.formula_function_named, true);
                argNumber = getArgNumber(code, mFunctionLinkName);
                break;
            case FUNCTION_INDEX:
                mFunctionLinkName = getFunctionLinkName(code, mFunctionType,
                        R.string.formula_function_start_index);
                if (mFunctionLinkName == null) {
                    throw new Exception("cannot create FormulaFunction(INDEX) since function name is invalid");
                }
                code = mFunctionLinkName +
                        getContext().getResources().getString(R.string.formula_function_start_index);
                inflateElements(R.layout.formula_function_index, true);
                argNumber = getArgNumber(code, mFunctionLinkName);
                break;

            case SQRT_LAYOUT:
                inflateElements(R.layout.formula_function_sqrt, true);
                break;
            case SURD_LAYOUT:
                inflateElements(R.layout.formula_function_nthrt, true);
                break;
            case FACTORIAL_LAYOUT:
                inflateElements(R.layout.formula_function_factorial, true);
                break;
            case CONJUGATE_LAYOUT:
                inflateElements(R.layout.formula_function_conjugate, true);
                break;
            case ABS_LAYOUT:
            case IDENTITY_LAYOUT:
                inflateElements(R.layout.formula_function_noname, true);
                break;
            case POWER_LAYOUT:
                inflateElements(R.layout.formula_function_pow, true);
                break;
            case Solve:
                inflateElements(R.layout.formula_function_solve, true);
                break;
            case Log:
                inflateElements(R.layout.formula_function_logn, true);
                break;
            default:
                inflateElements(R.layout.formula_function_named, true);
                break;
        }
        initializeElements(index);

        if (mTerms.isEmpty()) {
            throw new Exception("argument list is empty");
        }

        initializeMainLayout();

        // add additional arguments
        while (mTerms.size() < argNumber) {
            TermField newTerm = addArgument(mTerms.get(mTerms.size() - 1), R.layout.formula_function_add_arg,
                    getArgumentDepth());
            if (newTerm == null) {
                break;
            }
        }
        if (mFunctionType.getArgNumber() > 0 && mTerms.size() != mFunctionType.getArgNumber()) {
            throw new Exception("invalid size for argument list");
        }

        // special text properties
        prepareSpecialCase();

        // set texts for left and right parts (in editing mode only)
        for (int brIdx = 0; brIdx < BracketParser.START_BRACKET_IDS.length; brIdx++) {
            final String startBracket = getContext().getResources().getString(BracketParser.START_BRACKET_IDS[brIdx]);
            final String endBracket = getContext().getResources().getString(BracketParser.END_BRACKET_IDS[brIdx]);
            if (code.contains(startBracket) && code.endsWith(endBracket)) {
                code = code.substring(0, code.indexOf(endBracket)).trim();
            }
        }
        for (FunctionTrigger trigger : FunctionTrigger.values()) {
            String triggerCode = getContext().getResources().getString(trigger.getCodeId());
            final int position = code.indexOf(triggerCode);
            final TermField term = getArgumentTerm();
            if (position >= 0 && term != null) {
                try {
                    if (trigger.isBeforeText()) {
                        term.setText(code.subSequence(position + triggerCode.length(), code.length()));
                    } else {
                        term.setText(code.subSequence(0, position));
                    }
                    isContentValid(ValidationPassType.VALIDATE_SINGLE_FORMULA);
                } catch (Exception ex) {
                    // nothing to do
                }
                break;
            }
        }
    }

    private void prepareSpecialCase() {
        // TODO: 1/18/2018
    }

    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        switch (mFunctionType) {
            case IDENTITY_LAYOUT:
                return "(" + argsToString() + ")";
            case POWER_LAYOUT: {
                String left = mTerms.get(0).toExpressionString();
                String right = mTerms.get(1).toExpressionString();
                return "Power(" + left + "," + right + ")";
            }
            case FACTORIAL_LAYOUT:
                return "Factorial(" + mTerms.get(0).toExpressionString() + ")";
            case SQRT_LAYOUT:
                return "Sqrt(" + argsToString() + ")";
            case SURD_LAYOUT:
                return "Surd(" + mTerms.get(1).toExpressionString() + "," + mTerms.get(0).toExpressionString() + ")";
            case ABS_LAYOUT:
                return "Abs(" + mTerms.get(0).toExpressionString() + ")";
            case CONJUGATE_LAYOUT:
                return "Conjugate(" + mTerms.get(0).toExpressionString() + ")";
            case Solve:
                String left = mTerms.get(0).toExpressionString();
                String right = mTerms.get(1).toExpressionString();
                String var = mTerms.get(2).toExpressionString();
                return String.format("Solve(%s==%s,%s)", left, right, var);
            case FUNCTION_LINK:
            case FUNCTION_INDEX:
                return mLinkedFunction.toExpressionString();
            default: {
                return mFunctionType.getFunctionName() + "(" + argsToString() + ")";
            }
        }
    }

    private String argsToString() {
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < mTerms.size(); i++) {
            TermField term = mTerms.get(i);
            args.append(term.toExpressionString());
            if (i != mTerms.size() - 1) {
                args.append(",");
            }
        }
        return args.toString();
    }

    @Override
    public FormulaTermType.TermType getTermType() {
        return FormulaTermType.TermType.FUNCTION;
    }

    @Override
    public String getTermCode() {
        String code = getFunctionString(getFunctionType());
        if (mFunctionType.isLink()) {
            code += "." + mFunctionLinkName;
            if (mTerms.size() > 1) {
                code += FUNCTION_ARGS_MARKER + mTerms.size();
            }
        }
        if (DLog.DEBUG) DLog.d(TAG, "getTermCode() returned: " + code);
        return code;
    }

    @Override
    public boolean isContentValid(ValidationPassType type) {
        boolean isValid = true;
        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                mLinkedFunction = null;
                isValid = super.isContentValid(type);
                if (isValid && mFunctionType.isLink()) {
                    FormulaView f = getFormulaRoot().getFormulaList().getFormula(mFunctionLinkName, mTerms.size(),
                            getFormulaRoot().getId(), false);
                    ErrorCode errorCode = ErrorCode.NO_ERROR;
                    if (f == null || !(f instanceof EquationView)) {
                        errorCode = (mFunctionType == FunctionType.FUNCTION_LINK) ? ErrorCode.UNKNOWN_FUNCTION
                                : ErrorCode.UNKNOWN_ARRAY;
                        isValid = false;
                    } else if (f.getId() == getFormulaRoot().getId()) {
                        errorCode = ErrorCode.RECURSIVE_CALL;
                        isValid = false;
                    } else if (mFunctionType == FunctionType.FUNCTION_LINK && ((EquationView) f).isArray()) {
                        errorCode = ErrorCode.NOT_A_FUNCTION;
                        isValid = false;
                    } else if (mFunctionType == FunctionType.FUNCTION_INDEX && !((EquationView) f).isArray()
                            && !((EquationView) f).isInterval()) {
                        errorCode = ErrorCode.NOT_AN_ARRAY;
                        isValid = false;
                    } else {
                        mLinkedFunction = (EquationView) f;
                    }
                    setErrorCode(errorCode, mFunctionLinkName + "[" + mTerms.size() + "]");
                    if (getFormulaRoot() instanceof LinkHolderView && mLinkedFunction != null) {
                        if (!mLinkedFunction.isInterval()) {
                            ((LinkHolderView) getFormulaRoot()).addLinkedEquation(mLinkedFunction);
                        }
                    }
                }
                break;
            case VALIDATE_LINKS:
                isValid = super.isContentValid(type);
                break;
        }
        return isValid;
    }

    @Override
    protected FormulaTextView initializeSymbol(FormulaTextView view) {
        final Resources res = getContext().getResources();
        if (view.getText() != null) {
            String code = view.getText().toString();
            if (code.equals(res.getString(R.string.formula_operator_key))) {
                view.prepare(FormulaTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
                switch (mFunctionType) {
                    case POWER_LAYOUT:
                        view.setText("_");
                        break;
                    case FACTORIAL_LAYOUT:
                        view.setText(res.getString(R.string.formula_function_factorial_layout));
                        break;
                    case CONJUGATE_LAYOUT:
                        view.prepare(FormulaTextView.SymbolType.HOR_LINE, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText("_");
                        break;
                    default:
                        view.setText(getFunctionLabel());
                        break;
                }
                mFunctionTerm = view;
            } else if (code.equals(res.getString(R.string.formula_left_bracket_key))) {
                FormulaTextView.SymbolType s = (mFunctionType == FunctionType.ABS_LAYOUT)
                        ? FormulaTextView.SymbolType.VERT_LINE
                        : FormulaTextView.SymbolType.LEFT_BRACKET;
                view.prepare(s, getFormulaRoot().getFormulaList().getActivity(), this);
                view.setText("."); // this text defines view width/height
            } else if (code.equals(res.getString(R.string.formula_right_bracket_key))) {
                FormulaTextView.SymbolType s = (mFunctionType == FunctionType.ABS_LAYOUT)
                        ? FormulaTextView.SymbolType.VERT_LINE
                        : FormulaTextView.SymbolType.RIGHT_BRACKET;
                view.prepare(s, getFormulaRoot().getFormulaList().getActivity(), this);
                view.setText("."); // this text defines view width/height
            }
        }
        return view;
    }

    @Override
    protected FormulaEditText initializeTerm(FormulaEditText child, LinearLayout parent) {
        if (child.getText() != null) {
            final String key = child.getText().toString();
            String argTermKey = getContext().getResources().getString(R.string.formula_arg_term_key);
            String leftTermKey = getContext().getResources().getString(R.string.formula_left_term_key);
            String rightTermKey = getContext().getResources().getString(R.string.formula_right_term_key);
            if (mFunctionType == FunctionType.FUNCTION_INDEX) {
                if (key.equals(argTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, getArgumentDepth());
                    t.bracketsType = TermField.BracketsType.NEVER;
                }
            } else if (mFunctionType == FunctionType.SURD_LAYOUT) {

                if (key.equals(leftTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 3);
                    t.bracketsType = TermField.BracketsType.NEVER;

                } else if (key.equals(rightTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 0);
                    t.bracketsType = TermField.BracketsType.NEVER;
                }

            } else if (mFunctionType == FunctionType.POWER_LAYOUT) {
                if (key.equals(leftTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, child, this, false);
                    t.bracketsType = TermField.BracketsType.ALWAYS;

                } else if (key.equals(rightTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 3);
                    t.bracketsType = TermField.BracketsType.NEVER;

                }
            } else if (mFunctionType == FunctionType.Solve) {
                TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 0);
                t.bracketsType = TermField.BracketsType.NEVER;

            } else if (mFunctionType == FunctionType.Log) {
                if (key.equals(leftTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 3);
                    t.bracketsType = TermField.BracketsType.IFNECESSARY;

                } else if (key.equals(rightTermKey)) {
                    TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 0);
                    t.bracketsType = TermField.BracketsType.IFNECESSARY;
                }
            } else if (key.equals(argTermKey)) {
                TermField t = addTerm(getFormulaRoot(), parent, -1, child, this, 0);
                if (mFunctionType == FunctionType.FACTORIAL_LAYOUT
                        || mFunctionType == FunctionType.CONJUGATE_LAYOUT) {
                    t.bracketsType = TermField.BracketsType.ALWAYS;
                } else {
                    t.bracketsType = TermField.BracketsType.NEVER;
                }
            }
        }
        return child;
    }

    @Override
    public void updateTextSize() {
        super.updateTextSize();
        final int hsp = getFormulaList().getDimen().get(ScaledDimensions.Type.HOR_SYMBOL_PADDING);
        if (mFunctionTerm != null) {
            if (mFunctionType == FunctionType.SQRT_LAYOUT || mFunctionType == FunctionType.SURD_LAYOUT) {
                mFunctionTerm.setWidth(getFormulaList().getDimen().get(ScaledDimensions.Type.SMALL_SYMBOL_SIZE));
                mFunctionTerm.setPadding(0, 0, 0, 0);
            } else if (mFunctionType == FunctionType.CONJUGATE_LAYOUT) {
                mFunctionTerm.setPadding(hsp, 0, hsp, 0);
            } else if (mFunctionType == FunctionType.FUNCTION_INDEX) {
                mFunctionTerm.setPadding(0, 0, 0, 0);
            } else {
                mFunctionTerm.setPadding(0, 0, hsp, 0);
            }
        }
        if (mFunctionType == FunctionType.SURD_LAYOUT) {
            View nthrtPoverLayout = layout.findViewById(R.id.nthrt_power_layout);
            if (nthrtPoverLayout != null) {
                nthrtPoverLayout.setPadding(hsp, 0, hsp, 0);
            }
        }
    }

    @Override
    public TermField getArgumentTerm() {
        if (mFunctionType == FunctionType.SURD_LAYOUT) {
            return mTerms.get(1);
        }
        return super.getArgumentTerm();
    }

    private String getFunctionLabel() {
        switch (mFunctionType) {
            case FUNCTION_LINK:
            case FUNCTION_INDEX:
                return mFunctionLinkName;
            case IDENTITY_LAYOUT:
            case POWER_LAYOUT:
            case SQRT_LAYOUT:
            case SURD_LAYOUT:
            case FACTORIAL_LAYOUT:
            case ABS_LAYOUT:
            case CONJUGATE_LAYOUT:
                return "";
            default:
                return mFunctionType.getFunctionName();
        }
    }

    @Override
    public void onDelete(FormulaEditText owner) {
        final TermField ownerTerm = findTerm(owner);
        final Resources res = getResources();
        if (mFunctionType == FunctionType.SURD_LAYOUT
                || owner == null
                || mTerms.size() <= 1
                || !isNewTermEnabled()) {
            // search remaining text or term
            TermField remainingTerm = null;
            CharSequence remainingText = "";
            /*if (ownerTerm != null) {
                if (mFunctionTerm != null) {
                    remainingText = getFunctionLabel();
                }
                for (TermField term : mTerms) {
                    if (term == ownerTerm) {
                        continue;
                    }
                    if (term.isTerm()) {
                        remainingTerm = term;
                    } else if (!term.isEmpty()) {
                        remainingText = term.getText();
                    }
                }
            }*/

            if (parentField != null && remainingTerm != null) {
                parentField.onTermDelete(removeElements(), remainingTerm);
            } else if (parentField != null) {
                parentField.onTermDeleteWithText(removeElements(), remainingText);
            } else {
                super.onDelete(null);
            }
        } else if (isNewTermEnabled()) {
            if (parentField == null || ownerTerm == null) {
                return;
            }
            String separator = res.getString(R.string.formula_term_separator);
            TermField prevTerm = deleteArgument(ownerTerm, separator, true);
            getFormulaRoot().getFormulaList().onManualInput();
            if (prevTerm != null) {
                prevTerm.requestFocus();
            }
        }
    }

    @Override
    public boolean isNewTermEnabled() {
        return mFunctionType.isLink() || mFunctionType.isInfinityArg();
    }

    @Override
    public boolean onNewTerm(TermField owner, String code, boolean requestFocus) {
        if (DLog.DEBUG)
            DLog.d(TAG, "onNewTerm() called with: owner = [" + owner + "], code = [" + code + "], requestFocus = [" + requestFocus + "]");
        final String sep = getContext().getResources().getString(R.string.formula_term_separator);
        if (code == null || code.length() == 0 || !code.contains(sep)) {
            // string does not contains the term separator: can not be processed
            return false;
        }

        // below, we will return true since the string is processed independently from
        // the result
        if (!isNewTermEnabled()) {
            return true;
        }

        TermField newArg = addArgument(owner, R.layout.formula_function_add_arg, getArgumentDepth());
        if (newArg == null) {
            return true;
        }

        updateTextSize();
        if (owner.getText().contains(sep)) {
            TermField.divideString(code, sep, owner, newArg);
        }
        isContentValid(ValidationPassType.VALIDATE_SINGLE_FORMULA);
        if (requestFocus) {
            newArg.getEditText().requestFocus();
        }
        return true;
    }


    private int getArgumentDepth() {
        return mFunctionType == FunctionType.FUNCTION_INDEX ? 3 : 0;
    }

    /**
     * Procedure extracts linked function name from given string
     */
    private String getFunctionLinkName(String s, FunctionType f, int bracketId) {
        final Resources res = getContext().getResources();
        try {
            if (s.contains(res.getString(bracketId))) {
                String opCode = res.getString(bracketId);
                return s.substring(0, s.indexOf(opCode)).trim();
            }
            if (s.contains(f.getLinkObject())) {
                final String opCode = f.getLinkObject() + ".";
                final String nameAndArgs = s.substring(s.indexOf(opCode) + opCode.length(), s.length());
                if (nameAndArgs.length() > 0) {
                    final int argsMarker = nameAndArgs.indexOf(FUNCTION_ARGS_MARKER);
                    if (argsMarker > 0) {
                        return nameAndArgs.substring(0, argsMarker);
                    }
                }
                return nameAndArgs;
            }
            final String fName = f.getCode() + res.getString(R.string.formula_function_start_bracket);
            if (s.contains(fName)) {
                return s.replace(fName, "");
            }
        } catch (Exception ex) {
            // nothing to do
        }
        return null;
    }

    /**
     * Procedure extracts number of arguments from given string
     */
    private int getArgNumber(String s, String functionName) {
        try {
            String opCode = null;
            for (FunctionType f : FunctionType.values()) {
                if (f.isLink() && s.contains(f.getLinkObject())) {
                    opCode = f.getLinkObject() + ".";
                    break;
                }
            }
            if (opCode != null) {
                final String nameAndArgs = s.substring(s.indexOf(opCode) + opCode.length(), s.length());
                if (nameAndArgs.length() > 0) {
                    final int argsMarker = nameAndArgs.indexOf(FUNCTION_ARGS_MARKER);
                    if (argsMarker > 0) {
                        final String argsStr = nameAndArgs.substring(argsMarker + FUNCTION_ARGS_MARKER.length(),
                                nameAndArgs.length());
                        return Integer.parseInt(argsStr);
                    }
                }
                return 1;
            } else if (!s.contains(getContext().getResources().getString(R.string.formula_term_separator))) {
                FormulaView f = getFormulaRoot().getFormulaList().getFormula(functionName, ViewUtils.INVALID_INDEX,
                        getFormulaRoot().getId(), true);
                if (f != null && f instanceof EquationView) {
                    ArrayList<String> args = ((EquationView) f).getArguments();
                    if (args != null && !args.isEmpty()) {
                        return args.size();
                    }
                }
            }
        } catch (Exception ex) {
            // nothing to do
        }
        return 1;
    }

    /**
     * Returns function type
     */
    public FunctionType getFunctionType() {
        return mFunctionType;
    }

    /**
     * Returns function term
     */
    public FormulaTextView getFunctionTerm() {
        return mFunctionTerm;
    }

    private void setErrorCode(ErrorCode errorCode, String addInfo) {
        if (mFunctionTerm != null) {
            mFunctionTerm.setTextColor(CompatUtils.getThemeColorAttr(getContext(), R.attr.colorFormulaNormal));
        }
        if (parentField != null) {
            String errorMsg = null;
            switch (errorCode) {
                case NO_ERROR:
                    // nothing to do
                    break;
                case UNKNOWN_FUNCTION:
                case UNKNOWN_ARRAY:
                case NOT_AN_ARRAY:
                case NOT_A_FUNCTION:
                case NOT_DIFFERENTIABLE:
                    errorMsg = String.format(errorCode.getDescription(getContext()), addInfo);
                    break;
                case RECURSIVE_CALL:
                    errorMsg = errorCode.getDescription(getContext());
                    break;
            }
            parentField.setError(errorMsg, TermField.ErrorNotification.PARENT_LAYOUT, mFunctionMainLayout);
        }
    }


    /**
     * Error codes that can be generated by function term
     */
    public enum ErrorCode {
        NO_ERROR(-1),
        UNKNOWN_FUNCTION(R.string.error_unknown_function),
        UNKNOWN_ARRAY(R.string.error_unknown_array),
        NOT_AN_ARRAY(R.string.error_not_an_array),
        NOT_A_FUNCTION(R.string.error_not_a_function),
        RECURSIVE_CALL(R.string.error_recursive_call),
        NOT_DIFFERENTIABLE(R.string.error_not_differentiable);

        private final int descriptionId;

        ErrorCode(int descriptionId) {
            this.descriptionId = descriptionId;
        }

        public String getDescription(Context context) {
            return context.getResources().getString(descriptionId);
        }
    }
}
