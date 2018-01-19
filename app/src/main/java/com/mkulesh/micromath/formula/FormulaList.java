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
package com.mkulesh.micromath.formula;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.CalculatorActivity;
import com.duy.natural.calc.calculator.CalculatorContract;
import com.duy.natural.calc.calculator.calcbutton.Category;
import com.duy.natural.calc.calculator.dialogs.LaTeXFragment;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.mkulesh.micromath.BaseDisplayFragment;
import com.mkulesh.micromath.editstate.Coordinate;
import com.mkulesh.micromath.editstate.DeleteState;
import com.mkulesh.micromath.editstate.FormulaState;
import com.mkulesh.micromath.editstate.InsertState;
import com.mkulesh.micromath.editstate.ReplaceState;
import com.mkulesh.micromath.editstate.UndoState;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData.StoredTerm;
import com.mkulesh.micromath.fman.FileUtils;
import com.mkulesh.micromath.formula.io.FormulaWritter;
import com.mkulesh.micromath.formula.io.XmlLoaderTask;
import com.mkulesh.micromath.formula.type.ActionType;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.type.BasicSymbolType;
import com.mkulesh.micromath.formula.type.FormulaType;
import com.mkulesh.micromath.formula.views.CalculationResultView;
import com.mkulesh.micromath.formula.views.EquationView;
import com.mkulesh.micromath.formula.views.FormulaResultView;
import com.mkulesh.micromath.formula.views.FormulaView;
import com.mkulesh.micromath.formula.views.TermField;
import com.mkulesh.micromath.properties.DocumentProperties;
import com.mkulesh.micromath.properties.OnDocumentPropertiesChangeListener;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.IdGenerator;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.OnListChangeListener;
import com.mkulesh.micromath.widgets.ScaledDimensions;
import com.mkulesh.micromath.widgets.TwoDScrollView;
import com.nstudio.calc.casio.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mkulesh.micromath.formula.io.Constants.STATE_FORMULA_NUMBER;
import static com.mkulesh.micromath.formula.io.Constants.STATE_FORMULA_STATE;
import static com.mkulesh.micromath.formula.io.Constants.STATE_FORMULA_TYPE;
import static com.mkulesh.micromath.formula.io.Constants.STATE_SELECTED_LINE;
import static com.mkulesh.micromath.formula.io.Constants.STATE_UNDO_STATE;

public class FormulaList implements OnClickListener, OnListChangeListener, OnDocumentPropertiesChangeListener {
    private static final String TAG = "FormulaList";

    private final ArrayList<FormulaView> mSelectedEquations = new ArrayList<>();
    private final BaseDisplayFragment mFragment;
    private final AppCompatActivity mActivity;
    private final TwoDScrollView mFormulaScrollView;
    private final FormulaListView mFormulaListView;
    private final DocumentProperties mDocumentSettings;
    private final UndoState mUndoState;
    private final HashMap<Integer, FormulaView> mRootFormulas = new HashMap<>();

    @Nullable
    private FormulaView mSelectedTerm = null;
    private int mSelectedFormulaId = ViewUtils.INVALID_INDEX;

    private XmlLoaderTask mXmlLoaderTask = null;

    private CalculatorContract.IDisplayView mDisplayView;
    private CalculatorContract.IKeyboardView mKeyboardView;

    public FormulaList(BaseDisplayFragment fragment, View rootView) {
        mFragment = fragment;
        mActivity = (AppCompatActivity) fragment.getActivity();

        mFormulaScrollView = rootView.findViewById(R.id.main_scroll_view);
        mFormulaScrollView.setScaleListener(this);
        mFormulaScrollView.setSaveEnabled(false);
        mFormulaListView = new FormulaListView(fragment.getActivity(), mFormulaScrollView.getMainLayout());

        updateButtonState();

        mDocumentSettings = new DocumentProperties(getContext());
        mUndoState = new UndoState(mActivity);
    }

    public void setKeyboardView(CalculatorContract.IKeyboardView mKeyboardView) {
        this.mKeyboardView = mKeyboardView;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public Context getContext() {
        return mActivity;
    }

    public TwoDScrollView getFormulaScrollView() {
        return mFormulaScrollView;
    }

    public FormulaListView getFormulaListView() {
        return mFormulaListView;
    }

    /**
     * Procedure returns the formula having given id
     */
    @Nullable
    public FormulaView getFormula(int id) {
        return mRootFormulas.get(id);
    }

    public int getSelectedFormulaId() {
        return mSelectedFormulaId;
    }

    public DocumentProperties getDocumentSettings() {
        return mDocumentSettings;
    }

    public ScaledDimensions getDimen() {
        return mDocumentSettings.getScaledDimensions();
    }

    /**
     * Procedure removes focus from any focusable elements
     */
    public void clearFocus() {
        mFormulaListView.clearFocus();
    }

    /**
     * Check whether an operation that blocks the user interface is currently performed
     */
    public boolean isInOperation() {
        return mFragment.isInOperation();
    }

    /**
     * Procedure returns the selected term
     */
    public FormulaView getSelectedTerm() {
        return mSelectedTerm;
    }

    /**
     * Procedure sets the selected term
     */
    public void setSelectedTerm(FormulaView selectedTerm) {
        this.mSelectedTerm = (mSelectedEquations.isEmpty()) ? selectedTerm : null;
    }

    /**
     * Procedure returns the list of selected equations
     */
    public ArrayList<FormulaView> getSelectedEquations() {
        return mSelectedEquations;
    }

    public int getEquationsNumber() {
        return mRootFormulas.size();
    }

    /**
     * Procedure return a stored formula from the internal clipboard
     */
    @Nullable
    public FormulaClipboardData getStoredFormula() {
        if (mActivity instanceof CalculatorActivity) {
            return ((CalculatorActivity) mActivity).getStoredFormula();
        }
        return null;
    }

    /**
     * Procedure stores given formula into the internal clipboard
     */
    public void setStoredFormula(FormulaClipboardData clipboardManager) {
        if (mActivity instanceof CalculatorActivity) {
            ((CalculatorActivity) mActivity).setStoredFormula(clipboardManager);
        }
    }

    /**
     * Procedure enforces the currently active action mode to be finished
     */
    public void finishActiveActionMode() {
        if (mActivity instanceof CalculatorActivity) {
            ((CalculatorActivity) mActivity).finishActiveActionMode();
        }
    }

    @Override
    public void onClick(View view) {
        if (DLog.DEBUG) DLog.d(TAG, "onClick() called with: view = [" + view + "]");

        mSelectedFormulaId = ViewUtils.INVALID_INDEX;
        for (Map.Entry<Integer, FormulaView> f : mRootFormulas.entrySet()) {
            if (f == view) {
                setSelectedFormula(f.getValue().getId(), false);
                break;
            }
        }
    }

    @Override
    public void onNewFormula(Position position, FormulaType formulaType) {
        if (isInOperation()) {
            return;
        }
        FormulaView f = null;
        switch (formulaType) {
            case EQUATION:
                f = addBaseFormula(BaseType.EQUATION);
                break;
            case RESULT:
                f = addBaseFormula(BaseType.RESULT);
                break;
            default:
                break;
        }
        if (f != null) {
            getUndoState().addEntry(new InsertState(f.getId(), mSelectedFormulaId));
            mFormulaListView.add(f, mRootFormulas.get(mSelectedFormulaId), position);
            setSelectedFormula(f.getId(), true);
            f.onNewFormula();
        }
    }

    @Override
    public void onDiscardFormula(int id) {
        if (isInOperation()) {
            return;
        }
        final FormulaView formula = getFormula(id);
        if (formula != null) {
            mFormulaListView.clearFocus();
            DeleteState deleteState = new DeleteState();
            final FormulaView selectedFormula = deleteFormula(formula, deleteState);
            getUndoState().addEntry(deleteState);
            if (selectedFormula != null) {
                setSelectedFormula(selectedFormula.getId(), false);
            } else {
                setSelectedFormula(ViewUtils.INVALID_INDEX, false);
            }
        }
        onManualInput();
    }

    @Override
    public void onScale(float scaleFactor) {
        if (isInOperation()) {
            return;
        }
        getDimen().setScaleFactor(scaleFactor);
        for (Map.Entry<Integer, FormulaView> m : mRootFormulas.entrySet()) {
            m.getValue().updateTextSize();
        }
    }

    /**
     * Handler button click
     */
    @Override
    public void onButtonPressed(String code) {
        if (isInOperation()) return;

        ActionType actionType = ActionType.getActionType(code);
        FormulaType formulaType = FormulaType.getFormulaType(code);
        if (actionType != null) {
            onActionButtonPressed(actionType);
        } else if (formulaType != null) {
            // list operations
            onNewFormula(Position.AFTER, formulaType);
        } else {
            ensureHasFormulaView();
            // term operations
            FormulaView view = mRootFormulas.get(mSelectedFormulaId);
            if (view != null) {
                TermField term = view.findFocusedTerm();
                if (term == null) {
                    view.requestFocus();
                    term = view.findFocusedTerm();
                }
                if (term != null) {
                    if (ClipboardManager.isFormulaObject(code) && term.getTerm() != null) {
                        term.getTerm().onPasteFromClipboard(null, code);
                    } else {
                        BasicSymbolType numberType = BasicSymbolType.getNumberType(code);
                        FormulaEditText editText = term.getEditText();
                        if (numberType != null) {
                            onInsert(numberType.toString(), editText);
                        } else {
                            term.addOperatorCode(code);
                        }

                    }
                }
            }
            onManualInput();
        }
        finishActiveActionMode();
    }

    private void ensureHasFormulaView() {
        //check formula view is empty
        if (isEmptyFormula()) {
            onNewFormula(Position.AFTER, FormulaType.RESULT);
        }
    }

    private boolean isEmptyFormula() {
        return mFormulaListView.isEmpty();
    }

    private void onInsert(String text, FormulaEditText editText) {
        editText.insert(text);
    }

    private void onActionButtonPressed(ActionType actionType) {
        // term operations
        FormulaView view = mRootFormulas.get(mSelectedFormulaId);
        FormulaEditText editText = null;
        if (view != null) {
            TermField tf = view.findFocusedTerm();
            if (tf != null) {
                editText = tf.getEditText();
            }
        }
        switch (actionType) {
            case CALCULATE:
                doCalculate();
                break;
            case CLEAR:
                if (!deleteSelectedEquations()) {
                    clearAll();
                    onNewFormula(Position.AFTER, FormulaType.RESULT);
                }
                break;
            case DELETE:
                if (!deleteSelectedEquations()) {
                    if (editText != null) {
                        editText.processDelKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    }
                }
                break;
            case MOVE_LEFT:
                if (editText != null && !editText.moveLeft()) {
                    int nextFocusLeftId = editText.getNextFocusLeftId();
                    TermField termField = view.findTermWithId(nextFocusLeftId);
                    if (termField != null) {
                        termField.setEditableFocus(FormulaView.FocusType.FOCUS_RIGHT);
                    }
                }
                break;
            case MOVE_RIGHT:
                if (editText != null && !editText.moveRight()) {
                    int nextFocusRightId = editText.getNextFocusRightId();
                    TermField termField = view.findTermWithId(nextFocusRightId);
                    if (termField != null) {
                        termField.setEditableFocus(FormulaView.FocusType.FOCUS_LEFT);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCopyToClipboard() {
        if (mSelectedEquations.isEmpty()) {
            return false;
        }
        ClipboardManager.copyToClipboard(getContext(), ClipboardManager.CLIPBOARD_LIST_OBJECT);
        ArrayList<BaseType> types = new ArrayList<>();
        ArrayList<Parcelable> data = new ArrayList<>();
        final ArrayList<FormulaView> fList = mFormulaListView.getFormulas(FormulaView.class);
        for (FormulaView f : fList) {
            if (mSelectedEquations.contains(f)) {
                types.add(f.getBaseType());
                data.add(f.onSaveInstanceState());
            }
        }
        setStoredFormula(new FormulaClipboardData(types, data));
        return true;
    }

    @Override
    public boolean onPasteFromClipboard(String content) {
        if (mSelectedEquations.isEmpty() || !content.contains(ClipboardManager.CLIPBOARD_LIST_OBJECT)) {
            return false;
        }
        FormulaClipboardData s = getStoredFormula();
        if (s == null) {
            return false;
        }
        StoredTerm[] data = s.getArrayData();
        if (s.getContentType() != FormulaClipboardData.ContentType.LIST || data == null) {
            return false;
        }
        int dataIndex = 0;
        FormulaView lastInserted = null;
        final ArrayList<FormulaView> formulas = mFormulaListView.getFormulas(FormulaView.class);
        ReplaceState replaceState = new ReplaceState();
        for (int viewIndex = 0; viewIndex < formulas.size(); viewIndex++) {
            if (dataIndex >= data.length) {
                break;
            }
            final FormulaView formulaView = formulas.get(viewIndex);
            if (mSelectedEquations.contains(formulaView)) {
                lastInserted = replace(formulaView, null, data[dataIndex].baseType, data[dataIndex].data);
                if (lastInserted != null) {
                    replaceState.store(lastInserted.getId(), formulaView);
                }
                mSelectedEquations.remove(formulaView);
                dataIndex++;
                if (mSelectedEquations.isEmpty()) {
                    for (; dataIndex < data.length; dataIndex++) {
                        lastInserted = replace(null, lastInserted, data[dataIndex].baseType, data[dataIndex].data);
                        if (lastInserted != null) {
                            replaceState.store(lastInserted.getId(), null);
                        }
                    }
                }
            }
        }
        getUndoState().addEntry(replaceState);
        if (lastInserted != null) {
            setSelectedFormula(lastInserted.getId(), false);
        }
        onManualInput();
        return true;
    }

    @Override
    public void onManualInput() {
        if (isInOperation()) {
            return;
        }
        isContentValid();
    }

    @Override
    public void onExpandResult(CalculatedResult result) {
        LaTeXFragment dialog = LaTeXFragment.newInstance(result);
        dialog.show(mActivity.getSupportFragmentManager(), dialog.getClass().getName());
    }

    @Override
    public void onDocumentPropertiesChange(boolean isChanged) {
        if (isChanged) {
            doCalculate();
        }
    }

    /**
     * Procedure reads a file from resource folder
     */
    public void readFromResource(Uri uri, XmlLoaderTask.PostAction postAction) {
        if (!mRootFormulas.isEmpty()) {
            return;
        }
        InputStream is = FileUtils.getInputStream(mActivity, uri);
        if (is != null) {
            readFromStream(is, FileUtils.getFileName(mActivity, uri), postAction);
        }
    }


    /**
     * Parcelable interface: procedure writes the formula state
     */
    public void writeToBundle(Bundle outState) {
        final ArrayList<FormulaView> fList = mFormulaListView.getFormulas(FormulaView.class);
        final int n = fList.size();
        outState.putInt(STATE_FORMULA_NUMBER, n);
        int selectedLine = ViewUtils.INVALID_INDEX;
        for (int i = 0; i < n; i++) {
            FormulaView f = fList.get(i);
            outState.putString(STATE_FORMULA_TYPE + i, f.getBaseType().toString());
            outState.putParcelable(STATE_FORMULA_STATE + i, f.onSaveInstanceState());
            if (f.getId() == mSelectedFormulaId) {
                selectedLine = i;
            }
        }
        outState.putInt(STATE_SELECTED_LINE, selectedLine);
        outState.putParcelable(STATE_UNDO_STATE, mUndoState.onSaveInstanceState());
        mDocumentSettings.writeToBundle(outState);
    }

    /**
     * Parcelable interface: procedure reads the formula state
     */
    public void readFromBundle(Bundle data) {
        clearAll();
        IdGenerator.enableIdRestore = true;
        final int count = data.getInt(STATE_FORMULA_NUMBER, 0);
        final int selectedLine = data.getInt(STATE_SELECTED_LINE, 0);
        mDocumentSettings.readFromBundle(data);

        FormulaView selectedFormula = null;
        for (int index = 0; index < count; index++) {
            final BaseType baseType = BaseType.valueOf(data.getString(STATE_FORMULA_TYPE + index));
            FormulaView formulaView = addBaseFormula(baseType, data.getParcelable(STATE_FORMULA_STATE + index));
            mFormulaListView.add(formulaView, null, Position.AFTER); // add to the end
            if (selectedLine == index) {
                selectedFormula = formulaView;
            }
        }
        if (selectedFormula != null) {
            setSelectedFormula(selectedFormula.getId(), false);
        }
        mUndoState.onRestoreInstanceState(data.getParcelable(STATE_UNDO_STATE));
        IdGenerator.enableIdRestore = false;
        isContentValid();
        updateButtonState();
    }

    /**
     * XML interface: procedure reads this list from the given input stream
     */
    public void readFromStream(InputStream stream, String name, XmlLoaderTask.PostAction postAction) {
        mXmlLoaderTask = new XmlLoaderTask(this, stream, name, postAction);
        ViewUtils.debug(this, "started XML loader task: " + mXmlLoaderTask.toString());
        getUndoState().clear();
        CompatUtils.executeAsyncTask(mXmlLoaderTask);
    }

    /**
     * XML interface: procedure reads this list from the given file Uri
     */
    public boolean readFromFile(Uri uri) {
        InputStream is = FileUtils.getInputStream(mActivity, uri);
        if (is != null) {
            readFromStream(is, FileUtils.getFileName(mActivity, uri), XmlLoaderTask.PostAction.NONE);
            // do not close is since it will be closed by reading thread
            return true;
        }
        return false;
    }

    /**
     * XML interface: procedure writes this list into the given stream
     */
    public boolean writeToStream(OutputStream stream, String name) {
        return new FormulaWritter(mFormulaListView, mDocumentSettings, mActivity).write(stream, name);
    }

    /**
     * XML interface: procedure writes this list into the given file Uri
     */
    public boolean writeToFile(Uri uri) {
        String fName = FileUtils.getFileName(mActivity, uri);
        try {
            OutputStream os = FileUtils.getOutputStream(mActivity, uri);
            if (os == null) {
                return false;
            }
            final boolean retValue = writeToStream(os, fName);
            FileUtils.closeStream(os);
            return retValue;
        } catch (Exception e) {
            final String error = String.format(mActivity.getResources().getString(R.string.error_file_write), fName);
            Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Procedure returns undo state container
     */
    public UndoState getUndoState() {
        return mUndoState;
    }


    /**
     * Procedure performs undo
     */
    public void undo() {
        final Parcelable entry = mUndoState.resumeLastEntry();
        if (entry == null) {
            return;
        }
        IdGenerator.enableIdRestore = true;
        if (entry instanceof FormulaState) {
            final FormulaState s = (FormulaState) entry;
            final FormulaView f = getFormula(s.formulaId);
            if (f != null && s.termId != ViewUtils.INVALID_INDEX) {
                final TermField t = f.findTermWithId(s.termId);
                if (t != null) {
                    t.undo(s);
                }
            } else if (f != null) {
                f.undo(s);
            }
        } else if (entry instanceof InsertState) {
            final InsertState s = (InsertState) entry;
            final FormulaView f = getFormula(s.formulaId);
            if (f != null) {
                mRootFormulas.remove(f.getId());
                mFormulaListView.delete(f);
            }

            setSelectedFormula(s.selectedId, false);
        } else if (entry instanceof DeleteState) {
            final DeleteState ds = (DeleteState) entry;
            for (DeleteState.EntryState s : ds.getEntries()) {
                FormulaView f = addBaseFormula(s.type, s.data);
                if (f != null) {
                    mFormulaListView.add(f, s.coordinate);
                    setSelectedFormula(f.getId(), false);
                }
            }
        } else if (entry instanceof ReplaceState) {
            final ReplaceState rs = (ReplaceState) entry;
            int lastInsertedId = ViewUtils.INVALID_INDEX;
            for (ReplaceState.EntryState s : rs.getEntries()) {
                final FormulaView f = getFormula(s.formulaId);
                if (s.data != null) {
                    FormulaView newFormula = replace(f, null, s.type, s.data);
                    if (newFormula != null) {
                        lastInsertedId = newFormula.getId();
                    }
                } else if (f != null) {
                    mRootFormulas.remove(f.getId());
                    mFormulaListView.delete(f);
                }
            }
            setSelectedFormula(lastInsertedId, false);
        }
        onManualInput();
        IdGenerator.enableIdRestore = false;
    }

    /**
     * Set that an operation blocking the user interface is currently performed
     */
    public void setInOperation(AsyncTask owner, boolean inOperation, OnClickListener stopHandler) {
        mFragment.setInOperation(inOperation, stopHandler);
        mFormulaListView.setEnabled(!inOperation);
        if (mKeyboardView != null) {
            mKeyboardView.setEnabled(!inOperation);
        }
        if (!inOperation && owner instanceof XmlLoaderTask) {
            XmlLoaderTask t = (XmlLoaderTask) owner;
            mFragment.setXmlReadingResult(t.error == null);
            if (t.error != null) {
                isContentValid();
                Toast.makeText(getActivity(), t.error, Toast.LENGTH_LONG).show();
            } else if (t.postAction == XmlLoaderTask.PostAction.CALCULATE) {
                doCalculate();
            } else if (t.postAction == XmlLoaderTask.PostAction.INTERRUPT) {
                // nothing to do
            } else {
                isContentValid();
            }
            if (mXmlLoaderTask != null) {
                ViewUtils.debug(this, "terminated XML loader task: " + mXmlLoaderTask.toString());
                mXmlLoaderTask = null;
            }
            updateButtonState();
        }
    }

    /**
     * Performs clean-up of the list
     */
    public void clearAll() {
        mSelectedFormulaId = ViewUtils.INVALID_INDEX;
        mFormulaListView.clear();
        mRootFormulas.clear();
        getDimen().reset();
        IdGenerator.reset();
    }

    /**
     * Procedure creates a formula with given type
     */
    public FormulaView deleteFormula(FormulaView formulaView, DeleteState state) {
        Coordinate coordinate = mFormulaListView.getCoordinate(formulaView);
        final int prewRowCount = mFormulaListView.getList().getChildCount();
        FormulaView selectedFormula = mFormulaListView.delete(formulaView);
        if (mFormulaListView.getList().getChildCount() != prewRowCount) {
            coordinate.col = ViewUtils.INVALID_INDEX;
        }
        state.store(formulaView, coordinate);
        mRootFormulas.remove(formulaView.getId());
        return selectedFormula;
    }

    /**
     * Procedure creates a formula with given type
     */
    public FormulaView addBaseFormula(BaseType type) {
        return addBaseFormula(type, null);
    }

    /**
     * Procedure creates a formula with given type and given stored date
     */
    public FormulaView addBaseFormula(BaseType type, Parcelable data) {
        FormulaView formulaView = createFormula(type);
        if (formulaView != null) {
            formulaView.onRestoreInstanceState(data);
            formulaView.updateTextSize();
            mRootFormulas.put(formulaView.getId(), formulaView);
        }
        return formulaView;
    }

    /**
     * Procedure creates a formula with given type
     */
    private FormulaView createFormula(BaseType type) {
        FormulaView formulaView = null;
        final int id = IdGenerator.generateId();
        switch (type) {
            case EQUATION:
                formulaView = new EquationView(this, id);
                break;
            case RESULT:
                formulaView = new FormulaResultView(this, id);
                break;
        }
        if (formulaView != null) {
            formulaView.setOnClickListener(this);
        }
        return formulaView;
    }

    /**
     * Procedure searches a root formula with given properties
     */
    public FormulaView getFormula(String name, int argNumber, int rootId, boolean excludeRoot) {
        if (name == null) {
            return null;
        }
        return getFormulaListView().getFormula(name, argNumber, rootId, excludeRoot, !mDocumentSettings.redefineAllowed);
    }

    /**
     * Procedure sets the formula with given ID as selected
     */
    public void setSelectedFormula(int id, boolean setFocus) {
        if (DLog.DEBUG)
            DLog.d(TAG, "setSelectedFormula() called with: id = [" + id + "], setFocus = [" + setFocus + "]");
        if (mSelectedFormulaId == id && id != ViewUtils.INVALID_INDEX) {
            return;
        }
        mSelectedFormulaId = id;
        for (Map.Entry<Integer, FormulaView> m : mRootFormulas.entrySet()) {
            FormulaView f = m.getValue();
            if (f.getId() == mSelectedFormulaId) {
                f.setSelected(true);
                if (setFocus) {
                    f.setEditableFocus(FormulaView.FocusType.FIRST_EDITABLE);
                }
            } else {
                f.setSelected(false);
            }
        }
    }

    /**
     * Procedure marks all equations as selected
     */
    public void selectAll() {
        for (Map.Entry<Integer, FormulaView> m : mRootFormulas.entrySet()) {
            if (mSelectedEquations.contains(m.getValue())) {
                continue;
            }
            m.getValue().onTermSelection(null, true, null);
        }
        mFragment.updateModeTitle();
    }

    /**
     * Procedure sets the selected term or equation
     */
    public void selectEquation(SelectionMode mode, FormulaView toBeSelected) {
        switch (mode) {
            case ADD:
                if (toBeSelected != null && toBeSelected.isRootFormula() && !mSelectedEquations.contains(toBeSelected)) {
                    mSelectedEquations.add(toBeSelected);
                }
                break;
            case CLEAR:
                if (toBeSelected != null && toBeSelected.isRootFormula() && mSelectedEquations.contains(toBeSelected)) {
                    mSelectedEquations.remove(toBeSelected);
                }
                break;
            case CLEAR_ALL:
                if (!mSelectedEquations.isEmpty()) {
                    ArrayList<FormulaView> toBeCleared = new ArrayList<>();
                    toBeCleared.addAll(mSelectedEquations);
                    mSelectedEquations.clear();
                    for (FormulaView view : toBeCleared) {
                        view.onTermSelection(null, false, null);
                    }
                }
                setSelectedTerm(null);
                break;
        }
        mFragment.updateModeTitle();
    }

    /**
     * Procedure deletes all equations stored within the selectedEquations vector
     */
    public boolean deleteSelectedEquations() {
        if (mSelectedEquations.isEmpty()) {
            return false;
        }
        mFormulaListView.clearFocus();
        // search for the last formula before first deleted that will still in the view
        int selectedFormulaId = ViewUtils.INVALID_INDEX;
        final ArrayList<FormulaView> fList = mFormulaListView.getFormulas(FormulaView.class);
        if (mSelectedEquations.size() < fList.size()) {
            boolean equationFound = false;
            for (int i = fList.size() - 1; i >= 0; i--) {
                final FormulaView f = fList.get(i);
                if (mSelectedEquations.contains(f)) {
                    equationFound = true;
                    if (selectedFormulaId != ViewUtils.INVALID_INDEX) {
                        break;
                    }
                } else {
                    selectedFormulaId = f.getId();
                    if (equationFound) {
                        break;
                    }
                }
            }
        }
        // delete all selected formulas
        DeleteState deleteState = new DeleteState();
        for (int i = fList.size() - 1; i >= 0; i--) {
            final FormulaView f = fList.get(i);
            if (mSelectedEquations.contains(f)) {
                mSelectedEquations.remove(f);
                fList.remove(i);
                deleteFormula(f, deleteState);
            }
        }
        getUndoState().addEntry(deleteState);
        mSelectedEquations.clear();
        // restore focus
        setSelectedFormula(selectedFormulaId, false);
        return true;
    }

    /**
     * The given formula will be replaced by given stored term object
     */
    private FormulaView replace(FormulaView oldFormula, FormulaView afterThis, BaseType type,
                                Parcelable data) {
        if (data == null) {
            return null;
        }
        if (oldFormula != null) {
            mRootFormulas.remove(oldFormula.getId());
        }
        FormulaView newFormula = createFormula(type);
        if (newFormula != null) {
            if (!mFormulaListView.replace(oldFormula, newFormula)) {
                newFormula.onRestoreInstanceState(data);
                final Position pos = (newFormula.isInRightOfPrevious() ? Position.RIGHT : Position.AFTER);
                mFormulaListView.add(newFormula, afterThis, pos);
            } else {
                final boolean inRightOfPrevious = newFormula.isInRightOfPrevious();
                newFormula.onRestoreInstanceState(data);
                newFormula.setInRightOfPrevious(inRightOfPrevious);
            }
            newFormula.updateTextSize();
            mRootFormulas.put(newFormula.getId(), newFormula);
        }
        return newFormula;
    }

    /**
     * Procedure performs validity check for all formulas
     */
    private boolean isContentValid() {
        boolean isValid = true;
        ArrayList<FormulaView> formulas = mFormulaListView.getFormulas(FormulaView.class);
        if (DLog.DEBUG) DLog.d(TAG, "formulas = " + formulas);

        ArrayList<Integer> invalidFormulas = new ArrayList<>();

        // first pass - validate single formulas
        for (FormulaView m : formulas) {
            if (!m.isContentValid(FormulaView.ValidationPassType.VALIDATE_SINGLE_FORMULA)) {
                invalidFormulas.add(m.getId());
                isValid = false;
            }
        }

        // second pass - validate links
        for (FormulaView m : formulas) {
            if (invalidFormulas.contains(m.getId())) {
                continue;
            }
            if (!m.isContentValid(FormulaView.ValidationPassType.VALIDATE_LINKS)) {
                isValid = false;
            }
        }
        return isValid;
    }


    /**
     * Procedure performs calculation for all result formulae
     */
    public void doCalculate() {
        final ArrayList<CalculationResultView> formulas = mFormulaListView.getFormulas(CalculationResultView.class);
        if (DLog.DEBUG) DLog.d(TAG, "mFormulaListView = " + formulas);

        for (CalculationResultView f : formulas) {
            f.invalidateResult();
        }
//        if (isContentValid()) {
        CalculateTask calculateTask = new CalculateTask(this, mDisplayView, formulas);
        CompatUtils.executeAsyncTask(calculateTask);
//        }
    }

    /**
     * This procedure is used to enable/disable palette buttons related to current mode/selection
     */
    public void updateButtonState() {
        FormulaView formulaView = mRootFormulas.get(mSelectedFormulaId);
        TermField term = null;
        if (formulaView != null) {
            term = formulaView.findFocusedTerm();
        }
        for (Category category : Category.values()) {
            boolean enabled = false;
            boolean hiddenInputEnabled = false;
            if (term != null) {
                enabled = term.isEnableButton(category);
                if (term.isTerm()) {
                    hiddenInputEnabled = true;
                }
            }
            if (mKeyboardView != null) {
                mKeyboardView.setPaletteBlockEnabled(category, enabled);
                mKeyboardView.enableHiddenInput(hiddenInputEnabled);
            }
        }
    }

    public XmlLoaderTask getXmlLoaderTask() {
        return mXmlLoaderTask;
    }

    public void stopXmlLoaderTask() {
        if (mXmlLoaderTask != null) {
            mXmlLoaderTask.abort();
        }
    }

    public void setDisplayView(CalculatorContract.IDisplayView displayView) {
        this.mDisplayView = displayView;
    }

    /**
     * Enumerations.
     */
    public enum SelectionMode {
        ADD,
        CLEAR,
        CLEAR_ALL
    }
}
