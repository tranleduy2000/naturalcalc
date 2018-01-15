/*
 * Copyright (c) 2018 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mkulesh.micromath.formula;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mkulesh.micromath.editstate.Coordinate;
import com.mkulesh.micromath.formula.views.EquationView;
import com.mkulesh.micromath.formula.views.FormulaView;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaLayout;
import com.mkulesh.micromath.widgets.OnListChangeListener;
import com.mkulesh.micromath.widgets.OnListChangeListener.Position;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

public class FormulaListView {
    private final Context mContext;
    private final LinearLayout mList;
    private boolean mTermDeleted = false;


    public FormulaListView(Context context, LinearLayout layout) {
        mContext = context;
        mList = layout;
        mList.setSaveEnabled(false);
    }

    /*********************************************************
     * Helper static methods
     *********************************************************/
    private static FormulaView getNextFormula(LinearLayout parent, int index) {
        if (index == ViewUtils.INVALID_INDEX) {
            return null;
        }
        int targetIdx = ViewUtils.INVALID_INDEX;
        if (index < parent.getChildCount()) {
            targetIdx = index;
        } else if ((index - 1) < parent.getChildCount()) {
            targetIdx = index - 1;
        } else if (parent.getChildCount() > 0) {
            targetIdx = parent.getChildCount() - 1;
        }
        FormulaView formulaView = null;
        if (targetIdx != ViewUtils.INVALID_INDEX) {
            View view = parent.getChildAt(targetIdx);
            if (view instanceof ListRow) {
                ListRow row = (ListRow) view;
                if (row.getChildCount() > 0) {
                    view = row.getChildAt(0);
                }
            }
            if (view instanceof FormulaView) {
                formulaView = (FormulaView) view;
            }
        }
        return formulaView;
    }

    private static View getNextView(LinearLayout parent, int index, Position position) {
        View view = null;
        if (index > 0 && (position == OnListChangeListener.Position.BEFORE || position == OnListChangeListener.Position.LEFT)) {
            view = parent.getChildAt(index - 1);
        } else if (index < parent.getChildCount() - 1
                && (position == OnListChangeListener.Position.AFTER || position == OnListChangeListener.Position.RIGHT)) {
            view = parent.getChildAt(index + 1);
        }
        return view;
    }

    /**
     * Procedure returns the list of formulas
     */
    public LinearLayout getList() {
        return mList;
    }

    /**
     * Procedure removes focus from any focusable elements
     */
    public void clearFocus() {
        mList.requestFocus();
    }

    /**
     * Performs clean-up of the list
     */
    public void clear() {
        mList.removeAllViews();
        clearFocus();
    }

    /**
     * Procedure changes the enabled state of the formula list view
     */
    public void setEnabled(boolean enabled) {
        mList.setEnabled(enabled);
    }

    /**
     * Getter for the list of formulas of given type
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getFormulas(Class<T> c) {
        ArrayList<T> retValue = new ArrayList<>();
        final int n = mList.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = mList.getChildAt(i);
            if (v instanceof ListRow) {
                ((ListRow) v).getFormulas(c, retValue);
            } else if (c.isInstance(v)) {
                retValue.add((T) v);
            }
        }
        return retValue;
    }

    /**
     * Procedure adds given formula to the list with respect to the given coordinates
     */
    public void add(FormulaView f, Coordinate coordinate) {
        if (coordinate == null) {
            return;
        }
        if (f == null || coordinate.row == ViewUtils.INVALID_INDEX) {
            return;
        }
        if (coordinate.col == ViewUtils.INVALID_INDEX) {
            addAsRow(f, coordinate.row);
        } else {
            addToRow(f, coordinate.row, coordinate.col);
        }
    }

    /**
     * Procedure adds given formula to the list with respect to the given target formula
     */
    public void add(FormulaView f, FormulaView target, Position position) {
        if (f == null) {
            return;
        }
        int rowIdx = ViewUtils.INVALID_INDEX;
        if (target != null) {
            rowIdx = getRowIndex(target.getId());
        }

        final boolean beforeOrAfter = (position == Position.BEFORE || position == Position.AFTER);
        if (rowIdx == ViewUtils.INVALID_INDEX && !beforeOrAfter) {
            // cannot be inserted left or right since line index is not known
        } else if (rowIdx == ViewUtils.INVALID_INDEX && beforeOrAfter) {
            // add to the end of list
            if (!f.isInRightOfPrevious() || mList.getChildCount() == 0) {
                addAsRow(f, ViewUtils.INVALID_INDEX);
            } else {
                addToRow(f, mList.getChildCount() - 1, ViewUtils.INVALID_INDEX);
            }
        } else if (rowIdx != ViewUtils.INVALID_INDEX) {
            if (beforeOrAfter) {
                addAsRow(f, rowIdx + ((position == Position.BEFORE) ? 0 : 1));
            } else {
                final View v = mList.getChildAt(rowIdx);
                if (v instanceof ListRow) {
                    int colIdx = ((ListRow) v).getFormulaIndex(target.getId());
                    if (colIdx != ViewUtils.INVALID_INDEX) {
                        colIdx += ((position == OnListChangeListener.Position.RIGHT) ? 1 : 0);
                    }
                    addToRow(f, rowIdx, colIdx);
                } else {
                    addToRow(f, rowIdx, ((position == Position.LEFT) ? 0 : 1));
                }
            }
        }
    }

    /**
     * Add given formula as a row with given index
     */
    private void addAsRow(FormulaView f, int rowIdx) {
        if (rowIdx >= 0 && rowIdx <= mList.getChildCount()) {
            mList.addView(f, rowIdx);
        } else {
            mList.addView(f);
        }
    }

    /**
     * Add given formula to the row with given index
     */
    private void addToRow(FormulaView f, int rowIdx, int colIdx) {
        if (rowIdx >= 0 && rowIdx < mList.getChildCount()) {
            setTermDeleted(false);
            View v = mList.getChildAt(rowIdx);
            ListRow row = null;
            if (v instanceof ListRow) {
                row = (ListRow) v;
            } else {
                mList.removeView(v);
                row = new ListRow(mContext);
                mList.addView(row, rowIdx);
                row.addView(v);
                if (v instanceof FormulaView) {
                    // check that the current formula depth has no conflicts with allowed formula depth
                    ((FormulaView) v).checkFormulaDepth();
                }
            }
            if (colIdx == ViewUtils.INVALID_INDEX) {
                row.addView(f);
            } else {
                row.addView(f, colIdx);
            }
            if (f != null && f instanceof FormulaView) {
                // check that the current formula depth has no conflicts with allowed formula depth
                f.checkFormulaDepth();
            }
            if (mTermDeleted) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_max_layout_depth),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setTermDeleted(boolean termDeleted) {
        this.mTermDeleted = termDeleted;
    }

    /**
     * Procedure deletes the given formula from the list
     */
    public FormulaView delete(FormulaView f) {
        final int idx = getRowIndex(f.getId());
        if (idx == ViewUtils.INVALID_INDEX) {
            return null;
        }
        View v = mList.getChildAt(idx);
        if (v instanceof ListRow) {
            ListRow row = (ListRow) v;
            FormulaView selectedFormula = row.deleteFromView(f);
            if (selectedFormula == null && row.getChildCount() == 0) {
                mList.removeView(row);
                return getNextFormula(mList, idx);
            }
            return selectedFormula;
        } else if (v instanceof FormulaView) {
            mList.removeView(v);
            return getNextFormula(mList, idx);
        }
        return null;
    }

    /**
     * Replace the given formula by the new one
     */
    public boolean replace(FormulaView oldFormula, FormulaView newFormula) {
        if (oldFormula == null) {
            return false;
        }
        final int n = mList.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = mList.getChildAt(i);
            if (v instanceof ListRow) {
                if (((ListRow) v).replaceFormula(oldFormula, newFormula)) {
                    return true;
                }
            } else if (v instanceof FormulaView && v == oldFormula) {
                mList.removeView(v);
                mList.addView(newFormula, i);
                return true;
            }
        }
        return false;
    }

    /**
     * Procedure returns a formula with given offset related to the formula with given id
     */
    public FormulaView getFormula(int id, Position position) {
        final int idx = getRowIndex(id);
        if (idx == ViewUtils.INVALID_INDEX) {
            return null;
        }
        View v = mList.getChildAt(idx);
        if (v instanceof ListRow) {
            ListRow row = (ListRow) v;
            FormulaView f = row.getFormula(id, position);
            if (f != null) {
                return f;
            }
        }
        v = getNextView(mList, idx, position);
        if (v instanceof ListRow) {
            ListRow row = (ListRow) v;
            if (row.getChildCount() > 0) {
                v = (position == Position.BEFORE || position == Position.LEFT) ? row
                        .getChildAt(row.getChildCount() - 1) : row.getChildAt(0);
            }
        }
        if (v instanceof FormulaView) {
            return (FormulaView) v;
        }
        return null;
    }

    /**
     * Procedure returns the index of the list line that contains the formula with given ID
     */
    private int getRowIndex(int id) {
        final int n = mList.getChildCount();
        for (int i = 0; i < n; i++) {
            final View v = mList.getChildAt(i);
            if (v instanceof ListRow) {
                if (((ListRow) v).getFormulaIndex(id) != ViewUtils.INVALID_INDEX) {
                    return i;
                }
            } else if (v instanceof FormulaView) {
                if (v.getId() == id) {
                    return i;
                }
            }
        }
        return ViewUtils.INVALID_INDEX;
    }

    /**
     * Procedure returns full coordinates of the formula with given ID
     */
    public Coordinate getCoordinate(FormulaView f) {
        Coordinate coordinate = new Coordinate();
        final int id = f.getId();
        coordinate.row = getRowIndex(id);
        if (coordinate.row != ViewUtils.INVALID_INDEX) {
            View v = mList.getChildAt(coordinate.row);
            if (v instanceof ListRow) {
                coordinate.col = ((ListRow) v).getFormulaIndex(id);
            }
        }
        return coordinate;
    }

    /**
     * Procedure searches a root formula with given properties
     */
    public FormulaView getFormula(String name, int argNumber, int rootId, boolean excludeRoot, boolean searchAll) {
        int idx = getRowIndex(rootId);
        if (idx == ViewUtils.INVALID_INDEX || searchAll) {
            idx = mList.getChildCount() - 1;
        }
        for (int i = idx; i >= 0; i--) {
            final View vRow = mList.getChildAt(i);
            if (vRow instanceof ListRow) {
                ListRow row = ((ListRow) vRow);
                int col = row.getFormulaIndex(rootId);
                if (col == ViewUtils.INVALID_INDEX || searchAll) {
                    col = row.getChildCount() - 1;
                }
                for (int j = col; j >= 0; j--) {
                    final View vCol = row.getChildAt(j);
                    if (vCol instanceof EquationView) {
                        final EquationView f = (EquationView) vCol;
                        if (f.isEqual(name, argNumber, rootId, excludeRoot)) {
                            return f;
                        }
                    }
                }
            } else if (vRow instanceof EquationView) {
                final EquationView f = (EquationView) vRow;
                if (f.isEqual(name, argNumber, rootId, excludeRoot)) {
                    return f;
                }
            }
        }
        return null;
    }


    public final class ListRow extends FormulaLayout {
        /**
         * Default constructor
         */
        public ListRow(Context context) {
            super(context);
            setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            setGravity(Gravity.LEFT);
        }

        /**
         * Default constructor to avoid Lint warning
         */
        public ListRow(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void addView(View child) {
            super.addView(child);
            reIndex();
        }

        @Override
        public void addView(View child, int index) {
            super.addView(child, index);
            reIndex();
        }

        @Override
        public void removeView(View view) {
            if (view instanceof FormulaView) {
                ((FormulaView) view).setInRightOfPrevious(false);
            }
            super.removeView(view);
            reIndex();
        }

        /**
         * Procedure deletes given formula from this row
         */
        public FormulaView deleteFromView(FormulaView f) {
            final int idx = getFormulaIndex(f.getId());
            removeView(f);
            return getNextFormula(this, idx);
        }

        /**
         * Replace the given formula by the new one
         */
        public boolean replaceFormula(FormulaView oldFormula, FormulaView newFormula) {
            boolean retValue = false;
            final int n = getChildCount();
            for (int i = 0; i < n; i++) {
                View v = getChildAt(i);
                if (v == oldFormula) {
                    removeView(v);
                    addView(newFormula, i);
                    retValue = true;
                    break;
                }
            }
            return retValue;
        }

        /**
         * Getter for the list of formulas of given type
         */
        @SuppressWarnings("unchecked")
        public <T> void getFormulas(Class<T> c, ArrayList<T> retValue) {
            final int n = getChildCount();
            for (int i = 0; i < n; i++) {
                View v = getChildAt(i);
                if (c.isInstance(v)) {
                    retValue.add((T) v);
                }
            }
        }

        /**
         * Procedure returns a formula with given offset related to the formula with given id
         */
        public FormulaView getFormula(int id, Position position) {
            if (position == Position.BEFORE || position == Position.AFTER) {
                return null;
            }
            final int idx = getFormulaIndex(id);
            if (idx != ViewUtils.INVALID_INDEX) {
                View v = getNextView(this, idx, position);
                if (v != null && v instanceof FormulaView) {
                    return (FormulaView) v;
                }
            }
            return null;
        }

        /**
         * Procedure returns the index of the formula with given ID
         */
        private int getFormulaIndex(int id) {
            final int n = getChildCount();
            for (int i = 0; i < n; i++) {
                final View v = getChildAt(i);
                if (v instanceof FormulaView) {
                    if (v.getId() == id) {
                        return i;
                    }
                }
            }
            return ViewUtils.INVALID_INDEX;
        }

        /**
         * Procedure updates "InRightOfPrevious" property for all formulas
         */
        private void reIndex() {
            final int n = getChildCount();
            for (int i = 0; i < n; i++) {
                final View v = getChildAt(i);
                if (v instanceof FormulaView) {
                    ((FormulaView) v).setInRightOfPrevious(i > 0);
                }
            }
        }
    }
}
