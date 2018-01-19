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
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.mkulesh.micromath.dialogs.DialogResultDetails;
import com.mkulesh.micromath.dialogs.DialogResultSettings;
import com.mkulesh.micromath.editstate.FormulaState;
import com.mkulesh.micromath.formula.FormulaList;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.views.TermField.ErrorNotification;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.math.EquationArrayResult;
import com.mkulesh.micromath.properties.OnResultPropertiesChangeListener;
import com.mkulesh.micromath.properties.ResultProperties;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.mkulesh.micromath.widgets.OnFocusChangedListener;
import com.mkulesh.micromath.widgets.ResultMatrixLayout;
import com.nstudio.calc.casio.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;

public class FormulaResultView extends CalculationResultView implements OnResultPropertiesChangeListener, OnFocusChangedListener, View.OnClickListener {
    public static final String CELL_DOTS = "...";
    private static final String STATE_RESULT_PROPERTIES = "result_properties";
    private final ResultProperties properties = new ResultProperties();
    private FormulaTextView mResultAssign = null;
    private FormulaTextView mLeftBracket = null, mRightBracket = null;

    private CalculatedValue mConstantResult = null;
    private TermField mLeftTerm = null;
    private TermField mConstantResultField = null;
    private ResultType mResultType = ResultType.NONE;
    private View mExpandResult;

    // Array and matrix results
    private EquationArrayResult mArrayArgument = null, mArrayResult = null;
    private ResultMatrixLayout mArrayResultMatrix = null;
    // undo
    private FormulaState mFormulaState = null;
    @Nullable
    private CalculatedResult mResult;


    public FormulaResultView(FormulaList formulaList, int id) {
        super(formulaList, null, 0);
        setId(id);
        onCreate();
    }

    public FormulaResultView(Context context) {
        super(null, null, 0);
    }

    public FormulaResultView(Context context, AttributeSet attrs) {
        super(null, null, 0);
    }

    /**
     * Procedure creates the formula layout
     */
    private void onCreate() {
        inflateRootLayout(R.layout.formula_result, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mExpandResult = layout.findViewById(R.id.btn_expand_result);
        mExpandResult.setOnClickListener(this);
        // create name term
        {
            FormulaEditText v = layout.findViewById(R.id.formula_result_name);
            mLeftTerm = addTerm(this, (LinearLayout) layout.findViewById(R.id.result_function_layout), v, this, false);
            mLeftTerm.bracketsType = TermField.BracketsType.NEVER;
        }
        // create assign character
        {
            mResultAssign = layout.findViewById(R.id.formula_result_assign);
            mResultAssign.prepare(FormulaTextView.SymbolType.TEXT, getFormulaList().getActivity(), this);
        }
        // create result term
        {
            FormulaEditText view = layout.findViewById(R.id.formula_result_value);
            mConstantResultField = addTerm(this, layout, view, this, true);
            mConstantResultField.bracketsType = TermField.BracketsType.NEVER;
            mConstantResultField.isWritable = false;
            mArrayResultMatrix = layout.findViewById(R.id.formula_result_table);
        }
        // brackets
        {
            mLeftBracket = layout.findViewById(R.id.formula_result_left_bracket);
            mLeftBracket.prepare(FormulaTextView.SymbolType.LEFT_SQR_BRACKET, getFormulaList().getActivity(), this);
            mLeftBracket.setText("."); // this text defines view width/height

            mRightBracket = layout.findViewById(R.id.formula_result_right_bracket);
            mRightBracket.prepare(FormulaTextView.SymbolType.RIGHT_SQR_BRACKET, getFormulaList().getActivity(), this);
            mRightBracket.setText("."); // this text defines view width/height
        }
        updateResultView(false);
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.RESULT;
    }

    @Override
    public boolean isContentValid(FormulaView.ValidationPassType type) {
        boolean isValid = super.isContentValid(type);

        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                break;
            case VALIDATE_LINKS:
                // additional checks for intervals validity
                if (isValid && !mLeftTerm.isEmpty()) {
                    String errorMsg = null;
                    final ArrayList<String> indirectIntervals = getIndirectIntervals();
                    if (!indirectIntervals.isEmpty() && !getDirectIntervals().isEmpty()) {
                        isValid = false;
                        errorMsg = String.format(getContext().getResources().getString(R.string.error_indirect_intervals),
                                indirectIntervals.toString());
                    } else if (getAllIntervals().size() > 2) {
                        isValid = false;
                        errorMsg = getContext().getResources().getString(R.string.error_ensure_double_interval);
                    }
                    mLeftTerm.setError(errorMsg, ErrorNotification.LAYOUT_BORDER, null);
                }
                break;
        }

        if (!isValid) {
            clearResult();
            showResult();
        }
        return disableCalculation() || isValid;
    }

    @Override
    public void undo(FormulaState state) {
        super.undo(state);
        updateResultView(true);
        ViewUtils.invalidateLayout(layout, layout);
    }

    @Override
    public void onCalculateResult(CalculatedResult result) {
        mResult = result;
    }

    @Override
    public void onCalculateError(Exception e) {
        mResultType = null;
    }

    @Override
    public void updateTextSize() {
        super.updateTextSize();
        if (isArrayResult()) {
            mArrayResultMatrix.updateTextSize(getFormulaList().getDimen());
        }
    }

    @Override
    public void updateTextColor() {
        super.updateTextColor();
        if (isArrayResult()) {
            mArrayResultMatrix.updateTextColor(R.drawable.formula_term,
                    R.drawable.formula_term_background, R.attr.colorFormulaSelected);
        }
    }

    @Override
    public int getNextFocusId(FormulaEditText owner, OnFocusChangedListener.FocusType focusType) {
        if (isArrayResult()) {
            if (owner == mLeftTerm.getEditText() && focusType == OnFocusChangedListener.FocusType.FOCUS_RIGHT) {
                return mArrayResultMatrix.getFirstFocusId();
            }
            if (owner == null && focusType == OnFocusChangedListener.FocusType.FOCUS_LEFT) {
                return mArrayResultMatrix.getLastFocusId();
            }
        }
        return super.getNextFocusId(owner, focusType);
    }


    @Override
    public int onGetNextFocusId(FormulaEditText owner, OnFocusChangedListener.FocusType focusType) {
        if (owner == null) {
            return R.id.container_display;
        }
        if (mArrayResultMatrix != null) {
            int id = mArrayResultMatrix.getNextFocusId(owner, focusType);
            /*if (id == ViewUtils.INVALID_INDEX) {
                id = getNextFocusId(mConstantResultField.getEditText(), focusType);
            }*/
            return id;
        }
        return getNextFocusId(owner, focusType);
    }


    @Override
    public void invalidateResult() {
        mConstantResultField.setText("");
        mArrayResultMatrix.setText("", getFormulaList().getDimen());
        mExpandResult.setVisibility(GONE);
        mResult = null;
    }

    @Override
    public void calculate(CalculateTask thread) throws CancelException {
        clearResult();
        if (disableCalculation()) {
            return;
        }
        ArrayList<EquationView> linkedIntervals = getAllIntervals();
        if (linkedIntervals.isEmpty()) {
            mResultType = ResultType.CONSTANT;
            mConstantResult = new CalculatedValue();
            /*mLeftTerm.getValue(thread, mConstantResult);*/

        } else if (linkedIntervals.size() == 1) {
            final CalculatedValue[] argValues = new CalculatedValue[1];
            argValues[0] = new CalculatedValue();
            final ArrayList<Double> xValues = linkedIntervals.get(0).getInterval(thread);
            if (xValues != null && xValues.size() > 0) {
                final int xLength = xValues.size();
                mResultType = ResultType.ARRAY_1D;
                mArrayArgument = new EquationArrayResult(xLength);
                mArrayResult = new EquationArrayResult(xLength, 1);
                for (int xIndex = 0; xIndex < xLength; xIndex++) {
                    final Double x = xValues.get(xIndex);
                    argValues[0].setValue(x);
                    mArrayArgument.getValue1D(xIndex).setValue(x);
                    linkedIntervals.get(0).setArgumentValues(argValues);
                    /*mLeftTerm.getValue(thread, mArrayResult.getValue2D(xIndex, 0));*/
                }
            } else {
                mResultType = ResultType.NAN;
            }

        } else if (linkedIntervals.size() == 2) {
            final CalculatedValue[][] argValues = new CalculatedValue[2][1];
            argValues[0][0] = new CalculatedValue();
            final ArrayList<Double> xValues = linkedIntervals.get(0).getInterval(thread);
            argValues[1][0] = new CalculatedValue();
            final ArrayList<Double> yValues = linkedIntervals.get(1).getInterval(thread);
            if (xValues != null && xValues.size() > 0 && yValues != null && yValues.size() > 0) {
                final int xLength = xValues.size();
                final int yLength = yValues.size();
                mResultType = ResultType.ARRAY_2D;
                mArrayResult = new EquationArrayResult(xLength, yLength);
                for (int xIndex = 0; xIndex < xLength; xIndex++) {
                    argValues[0][0].setValue(xValues.get(xIndex));
                    linkedIntervals.get(0).setArgumentValues(argValues[0]);
                    for (int yIndex = 0; yIndex < yLength; yIndex++) {
                        argValues[1][0].setValue(yValues.get(yIndex));
                        linkedIntervals.get(1).setArgumentValues(argValues[1]);
                        /*mLeftTerm.getValue(thread, mArrayResult.getValue2D(xIndex, yIndex));*/
                    }
                }
            } else {
                mResultType = ResultType.NAN;
            }
        }
    }

    @NonNull
    @Override
    public String toExpressionString() {
        return mLeftTerm.toExpressionString();
    }

    public TermField getLeftTerm() {
        return mLeftTerm;
    }

    @Override
    public void showResult() {
        final int visibility = isResultVisible() ? View.VISIBLE : View.GONE;
        mResultAssign.setVisibility(visibility);

        /*switch (mResultType) {
            case NONE:
            case NAN:
            case CONSTANT: {*/
        mConstantResultField.getEditText().setVisibility(visibility);
        mLeftBracket.setVisibility(View.GONE);
        mArrayResultMatrix.setVisibility(View.GONE);
        mRightBracket.setVisibility(View.GONE);

        mConstantResultField.setText(fillResultString());
        if (mResult != null) {
            mExpandResult.setVisibility(VISIBLE);
        } else {
            mExpandResult.setVisibility(GONE);
        }
              /*  break;
            }
            case ARRAY_1D:
            case ARRAY_2D: {
                mLeftBracket.setVisibility(visibility);
                mConstantResultField.getEditText().setVisibility(View.GONE);
                mArrayResultMatrix.setVisibility(visibility);
                mRightBracket.setVisibility(visibility);
                fillResultMatrix();
                mArrayResultMatrix.prepare(getFormulaList().getActivity(), this, this);
                mArrayResultMatrix.updateTextSize(getFormulaList().getDimen());
                break;
            }
        }*/
    }

    @Override
    public boolean disableCalculation() {
        return properties.disableCalculation;
    }


    @Override
    public void onDetails(View owner) {
        if (enableDetails()) {
            DialogResultDetails d = new DialogResultDetails(getFormulaList().getActivity(),
                    mArrayArgument, mArrayResult,
                    getFormulaList().getDocumentSettings());
            d.show();
        }
    }

    @Override
    public void onObjectProperties(View owner) {
        if (owner == this) {
            properties.showArrayLenght = isArrayResult();
            DialogResultSettings d = new DialogResultSettings(getFormulaList().getActivity(), this, properties);
            mFormulaState = getState();
            d.show();
        }
        super.onObjectProperties(owner);
    }

    @Override
    public void onResultPropertiesChange(boolean isChanged) {
        getFormulaList().finishActiveActionMode();
        if (!isChanged) {
            mFormulaState = null;
            return;
        }
        if (mFormulaState != null) {
            getFormulaList().getUndoState().addEntry(mFormulaState);
            mFormulaState = null;
        }
        if (properties.disableCalculation) {
            clearResult();
        }
        updateResultView(true);
        ViewUtils.invalidateLayout(layout, layout);
    }

    @Override
    public boolean enableDetails() {
        return mResultType == ResultType.ARRAY_1D;
    }

    /**
     * Parcelable interface: procedure writes the formula state
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            ResultProperties rp = new ResultProperties();
            rp.assign(properties);
            bundle.putParcelable(STATE_RESULT_PROPERTIES, rp);
        }
        return state;
    }


    /**
     * Parcelable interface: procedure reads the formula state
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null) {
            return;
        }
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            properties.assign((ResultProperties) bundle.getParcelable(STATE_RESULT_PROPERTIES));
            super.onRestoreInstanceState(bundle);
            updateResultView(false);
        }
    }

    @Override
    public boolean onStartReadXmlTag(XmlPullParser parser) {
        super.onStartReadXmlTag(parser);
        if (getBaseType().toString().equalsIgnoreCase(parser.getName())) {
            properties.readFromXml(parser);
            updateResultView(false);
        }
        return false;
    }

    @Override
    public boolean onStartWriteXmlTag(XmlSerializer serializer, String key) throws Exception {
        super.onStartWriteXmlTag(serializer, key);
        if (getBaseType().toString().equalsIgnoreCase(serializer.getName())) {
            properties.writeToXml(serializer);
        }
        return false;
    }


    public boolean isResultVisible() {
        return !properties.hideResultField;
    }

    public boolean isArrayResult() {
        return mResultType == ResultType.ARRAY_1D || mResultType == ResultType.ARRAY_2D;
    }


    private void updateResultView(boolean checkContent) {
        if (checkContent) {
            if (isContentValid(FormulaView.ValidationPassType.VALIDATE_SINGLE_FORMULA)) {
                isContentValid(FormulaView.ValidationPassType.VALIDATE_LINKS);
            }
        }
        showResult();
    }

    public void clearResult() {
        mResultType = ResultType.NONE;
        mConstantResult = null;
        mArrayArgument = null;
        mArrayResult = null;
        mExpandResult.setVisibility(GONE);
    }

    private void fillResultMatrix() {
        if (!isArrayResult()) {
            return;
        }
        final int xValuesNumber = mArrayResult.getDimensions()[0];
        final int rowsNumber = Math.min(xValuesNumber, properties.arrayLength + 1);
        final int yValuesNumber = mArrayResult.getDimensions()[1];
        final int colsNumber = Math.min(yValuesNumber, properties.arrayLength + 1);

        mArrayResultMatrix.resize(rowsNumber, colsNumber, R.layout.formula_result_cell);
        for (int r = 0; r < rowsNumber; r++) {
            int dataRowIdx = r;
            if (xValuesNumber > properties.arrayLength) {
                // before the last line
                if (r + 2 == rowsNumber) {
                    for (int c = 0; c < colsNumber; c++) {
                        mArrayResultMatrix.setText(r, c, CELL_DOTS);
                    }
                    continue;
                }
                // the last line
                if (r + 1 == rowsNumber) {
                    dataRowIdx = xValuesNumber - 1;
                }
            }
            for (int c = 0; c < colsNumber; c++) {
                int dataColIdx = c;
                if (yValuesNumber > properties.arrayLength) {
                    // before the last column
                    if (c + 2 == colsNumber) {
                        mArrayResultMatrix.setText(r, c, CELL_DOTS);
                        continue;
                    }
                    // the last line
                    if (c + 1 == colsNumber) {
                        dataColIdx = yValuesNumber - 1;
                    }
                }
                String resultStr = mArrayResult.getValue2D(dataRowIdx, dataColIdx).getResultDescription(
                        getFormulaList().getDocumentSettings());
                mArrayResultMatrix.setText(r, c, resultStr);
            }
        }
    }

    public ArrayList<ArrayList<String>> fillResultMatrixArray() {
        if (!isArrayResult()) {
            return null;
        }
        final int xValuesNumber = mArrayResult.getDimensions()[0];
        final int rowsNumber = Math.min(xValuesNumber, properties.arrayLength + 1);
        final int yValuesNumber = mArrayResult.getDimensions()[1];
        final int colsNumber = Math.min(yValuesNumber, properties.arrayLength + 1);

        ArrayList<ArrayList<String>> res = new ArrayList<>(rowsNumber);
        for (int r = 0; r < rowsNumber; r++) {
            int dataRowIdx = r;
            res.add(new ArrayList<String>(colsNumber));
            if (xValuesNumber > properties.arrayLength) {
                // before the last line
                if (r + 2 == rowsNumber) {
                    for (int c = 0; c < colsNumber; c++) {
                        res.get(r).add(CELL_DOTS);
                    }
                    continue;
                }
                // the last line
                if (r + 1 == rowsNumber) {
                    dataRowIdx = xValuesNumber - 1;
                }
            }
            for (int c = 0; c < colsNumber; c++) {
                int dataColIdx = c;
                if (yValuesNumber > properties.arrayLength) {
                    // before the last column
                    if (c + 2 == colsNumber) {
                        res.get(r).add(CELL_DOTS);
                        continue;
                    }
                    // the last line
                    if (c + 1 == colsNumber) {
                        dataColIdx = yValuesNumber - 1;
                    }
                }
                res.get(r).add(mArrayResult.getValue2D(dataRowIdx, dataColIdx).getResultDescription(
                        getFormulaList().getDocumentSettings()));
            }
        }
        return res;
    }

    private String fillResultString() {
        if (mResult != null) {
            return mResult.fractionToString();
        }
       /* if (mResultType == ResultType.NAN) {
            return TermParser.CONST_NAN;
        }

        if (mResultType == ResultType.CONSTANT) {
            return mConstantResult.getResultDescription(getFormulaList().getDocumentSettings());
        }

        if (isArrayResult()) {
            final ArrayList<ArrayList<String>> res = fillResultMatrixArray();
            if (res != null) {
                return res.toString();
            }
        }*/
        return "";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_expand_result) {
            if (mResult != null) {
                FormulaList formulaList = getFormulaList();
                formulaList.onExpandResult(mResult);
            }
        }
    }

    public enum ResultType {
        NONE,
        NAN,
        CONSTANT,
        ARRAY_1D,
        ARRAY_2D
    }
}

