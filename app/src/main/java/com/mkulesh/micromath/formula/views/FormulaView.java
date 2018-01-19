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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duy.common.utils.DLog;
import com.mkulesh.micromath.editstate.FormulaState;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData;
import com.mkulesh.micromath.formula.FormulaList;
import com.mkulesh.micromath.formula.io.Constants;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.IdGenerator;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.utils.XmlUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.mkulesh.micromath.widgets.ContextMenuHandler;
import com.mkulesh.micromath.widgets.FormulaLayout;
import com.mkulesh.micromath.widgets.OnFocusChangedListener;
import com.mkulesh.micromath.widgets.OnFormulaChangeListener;
import com.mkulesh.micromath.widgets.OnListChangeListener;
import com.mkulesh.micromath.widgets.ScaledDimensions;
import com.nstudio.calc.casio.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;

public abstract class FormulaView extends FormulaLayout implements OnFormulaChangeListener {
    /*
     * Constants used to save/restore the instance state.
     */
    private static final String STATE_TERM = "term_";
    private static final String STATE_FORMULA_ID = "formula_id";
    private static final String STATE_INRIGHTOFPREVIOUS = "in_right_of_previous";
    private static final String TAG = "FormulaView";
    protected final int termDepth;
    private final FormulaList formulaList;
    protected TermField parentField = null;
    protected LinearLayout layout = null;
    protected ArrayList<View> mElements = new ArrayList<>();
    protected ArrayList<TermField> mTerms = new ArrayList<>();
    private boolean mInRightOfPrevious = false;

    public FormulaView(FormulaList formulaList, LinearLayout layout, int termDepth) {
        super(formulaList.getContext());
        this.formulaList = formulaList;
        this.layout = layout;
        this.termDepth = termDepth;
        setSaveEnabled(false);
    }

    /**
     * Procedure checks the given owner is the main equation owner (root view)
     */
    static boolean isEquationOwner(View owner) {
        return !(owner instanceof FormulaEditText);
    }

    /**
     * Procedure checks whether this formula is a root formula
     */
    public boolean isRootFormula() {
        return getBaseType() != BaseType.TERM;
    }

    /**
     * Getter for parent list of formulas
     */
    public FormulaList getFormulaList() {
        return formulaList;
    }

    /**
     * Setter for parent formula field
     */
    public TermField getParentField() {
        return parentField;
    }

    /**
     * Setter for parent formula field
     */
    public void setParentField(TermField parent) {
        this.parentField = parent;
    }

    /**
     * Procedure returns formula terms
     */
    public ArrayList<TermField> getTerms() {
        return mTerms;
    }

    /**
     * Procedure returns whether a new term is enabled for this formula
     */
    public boolean isNewTermEnabled() {
        return false;
    }

    /**
     * Procedure returns whether this formula shall be placed in right of the previous formula
     */
    public boolean isInRightOfPrevious() {
        return mInRightOfPrevious;
    }

    /**
     * Procedure sets that this formula shall be placed in right of the previous formula
     */
    public void setInRightOfPrevious(boolean inRightOfPrevious) {
        this.mInRightOfPrevious = inRightOfPrevious;
    }

    @Override
    public void setSelected(boolean isSelected) {
        setBackgroundColor(CompatUtils.getThemeColorAttr(getContext(),
                (isSelected) ? R.attr.colorFormulaHighlighted : R.attr.colorFormulaBackground));
    }

    /**
     * Getter that returns the type of this base formula
     */
    public abstract BaseType getBaseType();

    @Override
    public void onCreateContextMenu(View owner, ContextMenuHandler handler) {
        // empty
    }

    @Override
    public void onFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            if (!(view instanceof FormulaEditText)) {
                getFormulaList().clearFocus();
            }
            final FormulaView eq = (this instanceof FormulaTermView) ? ((FormulaTermView) this).getFormulaRoot() : this;
            getFormulaList().setSelectedFormula(eq.getId(), false);
            if (eq.isRootFormula() && !getFormulaList().getSelectedEquations().isEmpty()) {
                boolean isSelected = getFormulaList().getSelectedEquations().contains(eq);
                if (!isSelected) {
                    eq.onTermSelection(null, true, null);
                } else if (getFormulaList().getSelectedEquations().size() > 1) {
                    eq.onTermSelection(null, false, null);
                }
            } else {
                // we shall finish active action mode if focus is moved to an elements
                // that is not an owner of this mode
                boolean finishActionMode = false;
                if (view instanceof FormulaEditText && ((FormulaEditText) view).getActionMode() == null) {
                    finishActionMode = true;
                } else if (view instanceof FormulaTextView && ((FormulaTextView) view).getActionMode() == null) {
                    finishActionMode = true;
                }
                if (finishActionMode) {
                    getFormulaList().finishActiveActionMode();
                }
            }
        }
        for (TermField t : mTerms) {
            if (t.getEditText() == view) {
                if (hasFocus) {
                    t.showParsingError();
                } else {
                    getFormulaList().onManualInput();
                }
            }
        }
        getFormulaList().updateButtonState();
    }

    @Override
    public void onTermSelection(@Nullable View owner, boolean isSelected, ArrayList<View> list) {
        if (DLog.DEBUG)
            DLog.d(TAG, "onTermSelection() called with: owner = [" + owner + "], isSelected = [" + isSelected + "], list = [" + list + "]");
        // list is not empty and contains a single CustomEditText if we start action mode from EditText.
        // In this case, it shall be marked as selected
        if (list == null) {
            list = new ArrayList<>();
            collectElements(layout, list);
        }

        if (isSelected) {
            if (owner != null) {
                // null owner is used in case of selection expansion
                getFormulaList().finishActiveActionMode();
            }
            if (this instanceof FormulaTermView) {
                getFormulaList().setSelectedFormula(((FormulaTermView) this).getFormulaRoot().getId(), false);
            } else {
                getFormulaList().setSelectedFormula(getId(), false);
            }
            if (!(owner instanceof FormulaEditText)) {
                getFormulaList().clearFocus();
            }
        } else {
            ViewUtils.collectElemets(layout, list);
        }

        for (View v : list) {
            if (v instanceof FormulaEditText) {
                v.setSelected(isSelected);
            } else if (isSelected) {
                CompatUtils.updateBackgroundAttr(getContext(), v,
                        R.drawable.formula_term_background, R.attr.colorFormulaSelected);
            } else {
                CompatUtils.updateBackground(getContext(), v, R.drawable.formula_term);
            }
        }
        if (isRootFormula()) {
            if (isEquationOwner(owner)) {
                if (isSelected) {
                    getFormulaList().selectEquation(FormulaList.SelectionMode.ADD, this);
                } else {
                    getFormulaList().selectEquation(FormulaList.SelectionMode.CLEAR, this);
                }
            }
        }
        getFormulaList().setSelectedTerm(isSelected && isEquationOwner(owner) ? this : null);
        getFormulaList().updateButtonState();
        updateTextColor();
    }

    @Override
    public void finishActionMode(View owner) {
        getFormulaList().selectEquation(FormulaList.SelectionMode.CLEAR_ALL, null);
        onTermSelection(owner, false, null);
    }

    @Override
    public OnFormulaChangeListener onExpandSelection(View owner, ContextMenuHandler handler) {
        FormulaView retValue = null;
        if (isRootFormula() && (owner == null || isEquationOwner(owner))) {
            getFormulaList().selectAll();
        } else if (owner != null && owner instanceof FormulaEditText) {
            retValue = this;
        } else if (parentField != null) {
            retValue = parentField.getParentFormula();
        }
        return retValue;
    }

    @Override
    public void onCopyToClipboard() {
        if (getFormulaList().onCopyToClipboard()) {
            return;
        }
        ClipboardManager.copyToClipboard(getContext(), ClipboardManager.CLIPBOARD_TERM_OBJECT);
        getFormulaList().setStoredFormula(new FormulaClipboardData(getBaseType(), onSaveInstanceState()));
    }

    @Override
    public void onPasteFromClipboard(View owner, String content) {
        if (DLog.DEBUG)
            DLog.d(TAG, "onPasteFromClipboard() called with:  content = [" + content + "]");

        if (content == null) {
            return;
        }
        if (getFormulaList().onPasteFromClipboard(content)) {
            return;
        }

        TermField term = null;
        final boolean pasteIntoEditText = (owner != null && owner instanceof FormulaEditText);
        if (pasteIntoEditText) {
            // paste into text edit
            term = findTerm((FormulaEditText) owner);
        } else if (!isRootFormula()) {
            // paste into parent term
            term = parentField;
        }

        if (term != null) {
            if (content.contains(ClipboardManager.CLIPBOARD_TERM_OBJECT)) {
                FormulaClipboardData storedFormula = getFormulaList().getStoredFormula();
                if (storedFormula == null) {
                    ViewUtils.debug(this, "can not paste: stored formula is empty");
                    return;
                }
                if (storedFormula.getContentType() != FormulaClipboardData.ContentType.FORMULA || storedFormula.getSingleData() == null) {
                    ViewUtils.debug(this, "can not paste: clipboard object is not a formula");
                    return;
                }

                if (storedFormula.getSingleData().getBaseType() == BaseType.TERM) {
                    // restore TERM
                    final boolean restoreFocus = (pasteIntoEditText && owner.isFocused());
                    if (pasteIntoEditText) {
                        // we shall store the term state before operation
                        getFormulaList().getUndoState().addEntry(term.getState());
                    } else if (term == parentField) {
                        // onTermDelete stores the term state
                        term.onTermDelete(removeElements(), null);
                    }

                    term.readStoredFormula(storedFormula);

                    if (restoreFocus) {
                        term.setEditableFocus(FormulaView.FocusType.FIRST_EDITABLE);
                    }
                } else {
                    // error: cannot paste a root formula into term
                    String error = getFormulaList().getActivity().getResources()
                            .getString(R.string.error_paste_root_into_term);
                    Toast.makeText(getFormulaList().getActivity(), error, Toast.LENGTH_LONG).show();
                }

            } else if (content.contains(ClipboardManager.CLIPBOARD_LIST_OBJECT)) {
                String error = getFormulaList().getActivity().getResources()
                        .getString(R.string.error_paste_root_into_term);
                Toast.makeText(getFormulaList().getActivity(), error, Toast.LENGTH_LONG).show();
            } else {
                if (pasteIntoEditText) {
                    // we shall store the term state before operation
                    getFormulaList().getUndoState().addEntry(term.getState());
                } else if (term == parentField) {
                    // onTermDelete stores the term state
                    term.onTermDelete(removeElements(), null);
                }
                // restore text
                term.setText(content);
            }
        }
        getFormulaList().onManualInput();
    }

    @Override
    public void onDelete(FormulaEditText owner) {
        if (isRootFormula() && getFormulaList().deleteSelectedEquations()) {
            getFormulaList().onManualInput();
            return;
        }
        if (parentField != null) {
            parentField.onTermDelete(removeElements(), null);
        }
        getFormulaList().onManualInput();
    }

    @Override
    public void onObjectProperties(View owner) {
        // empty
    }

    @Override
    public void onNewFormula() {
        // empty
    }

    @Override
    public void onDetails(View owner) {
        // empty
    }

    @Override
    public boolean onNewTerm(TermField field, String s, boolean requestFocus) {
        return false;
    }

    @Override
    public boolean enableDetails() {
        return false;
    }

    /**
     * Parcelable interface: procedure writes the formula state
     */
    @SuppressLint("MissingSuperCall")
    public Parcelable onSaveInstanceState() {
        if (DLog.DEBUG) DLog.d(TAG, "onSaveInstanceState() called");
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_FORMULA_ID, getId());
        bundle.putBoolean(STATE_INRIGHTOFPREVIOUS, mInRightOfPrevious);
        for (int i = 0; i < mTerms.size(); i++) {
            if (DLog.DEBUG) DLog.d(TAG, "onSaveInstanceState() i = " + i);
            mTerms.get(i).writeToBundle(bundle, STATE_TERM + i);
        }
        return bundle;
    }

    /**
     * Parcelable interface: procedure reads the formula state
     */
    @SuppressLint("MissingSuperCall")
    public void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state == null) {
            return;
        }
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            if (IdGenerator.enableIdRestore) {
                setId(bundle.getInt(STATE_FORMULA_ID));
                IdGenerator.compareAndSet(getId());
            }
            mInRightOfPrevious = bundle.getBoolean(STATE_INRIGHTOFPREVIOUS);
            for (int i = 0; i < mTerms.size(); i++) {
                if (mTerms.get(i).isWritable) {
                    mTerms.get(i).readFromBundle(bundle, STATE_TERM + i);
                }
            }
        }
    }

    /**
     * XML interface: callback on start of an xml tag reading
     */
    public boolean onStartReadXmlTag(XmlPullParser parser) {
        if (getBaseType().toString().equalsIgnoreCase(parser.getName())) {
            String attr = parser.getAttributeValue(null, Constants.XML_PROP_INRIGHTOFPREVIOUS);
            if (attr != null) {
                mInRightOfPrevious = Boolean.valueOf(attr);
            }
        }
        return false;
    }

    /**
     * XML interface: procedure reads the formula state
     */
    public void readFromXml(XmlPullParser parser) throws Exception {
        if (onStartReadXmlTag(parser)) {
            return;
        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String n = parser.getName();
            boolean termFound = false;
            if (onStartReadXmlTag(parser)) {
                termFound = true;
            } else if (n.equalsIgnoreCase(Constants.XML_TERM_TAG)) {
                final String key = parser.getAttributeValue(null, Constants.XML_PROP_KEY);
                for (TermField term : mTerms) {
                    if (term.getTermKey() != null && key != null && term.getTermKey().equals(key)) {
                        term.readFromXml(parser);
                        termFound = true;
                        break;
                    }
                }
            }
            if (!termFound) {
                XmlUtils.skipEntry(parser);
            }
        }
    }

    /**
     * XML interface: callback on start of an xml tag reading
     */
    public boolean onStartWriteXmlTag(XmlSerializer serializer, String key) throws Exception {
        if (mInRightOfPrevious && getBaseType().toString().equalsIgnoreCase(serializer.getName())) {
            serializer.attribute(Constants.XML_NS, Constants.XML_PROP_INRIGHTOFPREVIOUS,
                    String.valueOf(mInRightOfPrevious));
        }
        return false;
    }

    /**
     * XML interface: procedure returns string that contains XML representation of this formula
     */
    public void writeToXml(XmlSerializer serializer, String key) throws Exception {
        if (onStartWriteXmlTag(serializer, key)) {
            return;
        }
        for (TermField term : mTerms) {
            serializer.startTag(Constants.XML_NS, Constants.XML_TERM_TAG);
            serializer.attribute(Constants.XML_NS, Constants.XML_PROP_KEY, term.getTermKey());
            if (onStartWriteXmlTag(serializer, term.getTermKey())) {
                continue;
            }
            if (term.isWritable) {
                term.writeToXml(serializer);
            }
            serializer.endTag(Constants.XML_NS, Constants.XML_TERM_TAG);
        }
    }

    /**
     * Procedure returns undo state for this formula
     */
    public FormulaState getState() {
        if (isRootFormula()) {
            return new FormulaState(getId(), ViewUtils.INVALID_INDEX, onSaveInstanceState());
        } else if (getParentField() != null) {
            return getParentField().getState();
        }
        return null;
    }

    /**
     * Procedure applies the given undo state
     */
    public void undo(FormulaState state) {
        if (state == null) {
            return;
        }
        if (state.data instanceof Bundle) {
            for (TermField t : mTerms) {
                t.clear();
            }
            onRestoreInstanceState(state.data);
        }
    }


    /**
     * Procedure inflates layout with given resource ID
     */
    protected void inflateRootLayout(int resId, int width, int height) {
        layout = this;
        layout.setLayoutParams(new LayoutParams(width, height));
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resId, layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            mElements.add(layout.getChildAt(i));
        }
    }

    /**
     * Procedure inflates elements from the given resource into elements vector
     */
    protected void inflateElements(int resId, boolean removeFromLayout) {
        inflateElements(mElements, resId, removeFromLayout);
    }


    /**
     * Procedure inflates elements from the given resource into the given vector
     */
    protected void inflateElements(ArrayList<View> out, int resId, boolean removeFromLayout) {
        final int lastIdx = layout.getChildCount();
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resId, layout);
        for (int i = lastIdx; i < layout.getChildCount(); i++) {
            out.add(layout.getChildAt(i));
        }
        if (removeFromLayout) {
            for (View v : out) {
                layout.removeView(v);
            }
        }
    }

    /**
     * Procedure collects all elements that belong to the given layout into output vector
     */
    public void collectElements(LinearLayout layout, ArrayList<View> out) {
        for (View v : mElements) {
            if (v.getParent() == layout) {
                if (!out.contains(v)) {
                    out.add(v);
                }
            }
        }
        for (TermField t : mTerms) {
            t.collectElements(layout, out);
        }
    }

    /**
     * Procedure removes all elements for this term and returns the index of the first removed element
     */
    public int removeElements() {
        int minIdx = layout.getChildCount() + 1;
        ArrayList<View> toRemove = new ArrayList<>();
        collectElements(layout, toRemove);
        for (View v : toRemove) {
            int idx = ViewUtils.getViewIndex(layout, v);
            if (idx >= 0) {
                minIdx = Math.min(minIdx, idx);
            }
            layout.removeView(v);
        }
        return minIdx;
    }

    /**
     * Procedure adds a term to the term list
     */
    protected TermField addTerm(FormulaView formulaView, LinearLayout parent, FormulaEditText editText,
                                OnFormulaChangeListener onFormulaChangeListener, boolean addDepth) {
        return addTerm(formulaView, parent, -1, editText, onFormulaChangeListener, ((addDepth) ? 1 : 0));
    }

    /**
     * Procedure adds a term to the term list with given index
     */
    protected TermField addTerm(FormulaView formulaView, LinearLayout l, int idx, FormulaEditText editText,
                                OnFormulaChangeListener onFormulaChangeListener, int addDepth) {
        editText.setup(formulaView.getFormulaList().getActivity(), onFormulaChangeListener);
        TermField termField = new TermField(formulaView, this, l, termDepth + addDepth, editText);
        if (idx < 0) {
            mTerms.add(termField);
        } else {
            mTerms.add(idx, termField);
        }
        return termField;
    }

    /**
     * Procedure check that the current formula depth has no conflicts with allowed formula depth
     */
    public boolean checkFormulaDepth() {
        boolean retValue = true;
        for (TermField t : mTerms) {
            if (!t.checkFormulaDepth()) {
                retValue = false;
            }
        }
        return retValue;
    }

    /**
     * Returns term field object related to the given edit
     */
    protected TermField findTerm(FormulaEditText editText) {
        for (TermField t : mTerms) {
            if (editText == t.getEditText()) {
                return t;
            }
        }
        return null;
    }

    /**
     * Returns term field object with given key
     */
    public TermField findTermWithKey(int keyId) {
        try {
            String key = getContext().getResources().getString(keyId);
            for (TermField t : mTerms) {
                if (t.getTermKey() != null && t.getTermKey().equals(key)) {
                    return t;
                }
            }
        } catch (Exception ex) {
            // nothing to do
        }
        return null;
    }

    /**
     * Returns term field object with given id
     */
    @Nullable
    public TermField findTermWithId(int termId) {
        for (TermField t : mTerms) {
            if (t.getTermId() == termId) {
                return t;
            }
            if (t.getTerm() != null) {
                TermField f = t.getTerm().findTermWithId(termId);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Procedure searches the focused term recursively
     */
    @Nullable
    public TermField findFocusedTerm() {
        for (TermField term : mTerms) {
            TermField focusedTerm = term.findFocusedTerm();
            if (focusedTerm != null) {
                return focusedTerm;
            }
        }
        return null;
    }

    /**
     * Procedure checks that all declared terms are valid
     */
    public boolean isContentValid(ValidationPassType type) {
        boolean isValid = true;
        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                // an empty root formula will be considered as valid
                if (isRootFormula() && isEmpty()) {
                    return true;
                }
                // for a non-empty formula, we check the content validity
                for (TermField t : mTerms) {
                    if (t.checkContentType() == TermField.ContentType.INVALID) {
                        isValid = false;
                    }
                }
                break;
            case VALIDATE_LINKS:
                isValid = true;
                break;
        }
        return isValid;
    }

    /**
     * Procedure checks that all declared terms are empty
     */
    public boolean isEmpty() {
        for (TermField t : mTerms) {
            if (!t.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Procedure sets the focus to the first editable element
     */
    public boolean setEditableFocus(FocusType type) {
        for (TermField t : mTerms) {
            if (t.setEditableFocus(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Procedure recursively updates the text size of the given view
     */
    private void updateTextSize(View v, ScaledDimensions dimen) {
        if (isRootFormula()) {
            final int hor = dimen.get(ScaledDimensions.Type.HOR_ROOT_PADDING);
            final int vert = dimen.get(ScaledDimensions.Type.VERT_ROOT_PADDING);
            layout.setPadding(hor, vert, hor, vert);
        }
        if (v instanceof FormulaTextView) {
            ((FormulaTextView) v).updateTextSize(dimen, termDepth);
        }
        if (v instanceof FormulaLayout) {
            ((FormulaLayout) v).updateTextSize(dimen, termDepth);
        }
        if (v instanceof ViewGroup) {
            final ViewGroup l = (ViewGroup) v;
            for (int k = 0; k < l.getChildCount(); k++) {
                updateTextSize(l.getChildAt(k), dimen);
            }
        }
    }

    /**
     * Procedure updates the text size of this formula depending on layout depth
     */
    public void updateTextSize() {
        final ScaledDimensions dimen = getFormulaList().getDimen();
        for (View v : mElements) {
            updateTextSize(v, dimen);
        }
        for (TermField t : mTerms) {
            t.updateTextSize();
        }
    }

    /**
     * Procedure updates the text color of this formula
     */
    public void updateTextColor() {
        for (TermField t : mTerms) {
            t.updateTextColor();
        }
    }

    /**
     * Procedure returns the ID of the next focused EditText relative to the given owner
     */
    public int getNextFocusId(FormulaEditText owner, OnFocusChangedListener.FocusType focusType) {
        FormulaView f = null;
        // Process UP/DOWN
        if (isRootFormula() && owner != null) {
            if (focusType == OnFocusChangedListener.FocusType.FOCUS_UP) {
                f = getFormulaList().getFormulaListView().getFormula(getId(), OnListChangeListener.Position.BEFORE);
            } else if (focusType == OnFocusChangedListener.FocusType.FOCUS_DOWN) {
                f = getFormulaList().getFormulaListView().getFormula(getId(), OnListChangeListener.Position.AFTER);
            }
            if (f != null) {
                return f.getNextFocusId(null, focusType);
            }
        }
        // Process LEFT/RIGHT
        final int n = mTerms.size();
        int i = 0;
        for (i = 0; i < n; i++) {
            TermField t = mTerms.get(i);
            if (t.getEditText() == owner) {
                break;
            }
        }
        TermField t = null;
        if (i < n) {
            if (focusType == OnFocusChangedListener.FocusType.FOCUS_LEFT && i > 0) {
                t = mTerms.get(i - 1);
            } else if (focusType == OnFocusChangedListener.FocusType.FOCUS_RIGHT && i < n - 1) {
                t = mTerms.get(i + 1);
            }
        } else if (owner == null && mTerms.size() > 0) {
            if (focusType == OnFocusChangedListener.FocusType.FOCUS_LEFT) {
                t = mTerms.get(n - 1);
            }
            if (focusType == OnFocusChangedListener.FocusType.FOCUS_RIGHT || focusType == OnFocusChangedListener.FocusType.FOCUS_UP
                    || focusType == OnFocusChangedListener.FocusType.FOCUS_DOWN) {
                t = mTerms.get(0);
            }
        }
        if (t != null) {
            return t.isTerm() ? t.getTerm().getNextFocusId(null, focusType) : t.getTermId();
        } else if (parentField != null) {
            return parentField.onGetNextFocusId(null, focusType);
        } else if (isRootFormula()) {
            if (focusType == OnFocusChangedListener.FocusType.FOCUS_LEFT) {
                f = getFormulaList().getFormulaListView().getFormula(getId(), OnListChangeListener.Position.LEFT);
            } else if (focusType == OnFocusChangedListener.FocusType.FOCUS_RIGHT) {
                f = getFormulaList().getFormulaListView().getFormula(getId(), OnListChangeListener.Position.RIGHT);
            }
            if (f != null) {
                return f.getNextFocusId(null, focusType);
            }
        }
        return R.id.container_display;
    }


    public enum FocusType {
        FIRST_EDITABLE,
        FIRST_EMPTY,
        FOCUS_RIGHT,
        FOCUS_LEFT
    }

    public enum ValidationPassType {
        VALIDATE_SINGLE_FORMULA,
        VALIDATE_LINKS
    }
}
