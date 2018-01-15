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
import android.widget.LinearLayout;

import com.mkulesh.micromath.formula.FormulaList;

import java.util.ArrayList;

public abstract class LinkHolderView extends FormulaView {
    private final ArrayList<EquationView> directIntervals = new ArrayList<>();
    private final ArrayList<EquationView> allIntervals = new ArrayList<>();

    private final ArrayList<EquationView> directFunctions = new ArrayList<>();
    private final ArrayList<EquationView> allFunctions = new ArrayList<>();


    public LinkHolderView(FormulaList formulaList, LinearLayout layout, int termDepth) {
        super(formulaList, layout, termDepth);
    }

    public LinkHolderView(Context context) {
        super(null, null, 0);
    }


    @Override
    public boolean isContentValid(ValidationPassType type) {
        boolean isValid = true;
        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                directIntervals.clear();
                allIntervals.clear();
                directFunctions.clear();
                allFunctions.clear();
                isValid = super.isContentValid(type);
                break;
            case VALIDATE_LINKS:
                isValid = super.isContentValid(type);
                collectAllIntervals(null);
                collectAllFunctions(null);
                break;
        }
        return isValid;
    }


    /**
     * Procedure returns the list of directly linked intervals
     */
    public ArrayList<EquationView> getDirectIntervals() {
        return directIntervals;
    }

    /**
     * Procedure returns the list of all linked intervals
     */
    public ArrayList<EquationView> getAllIntervals() {
        return allIntervals;
    }

    /**
     * Procedure returns the list of all linked functions
     */
    public ArrayList<EquationView> getAllFunctions() {
        return allFunctions;
    }

    /**
     * Procedure returns the list of indirectly linked intervals
     */
    public ArrayList<String> getIndirectIntervals() {
        ArrayList<String> retValue = new ArrayList<>();
        if (getDirectIntervals().size() != getAllIntervals().size()) {
            for (EquationView li : allIntervals) {
                if (!directIntervals.contains(li)) {
                    retValue.add(li.getName());
                }
            }
        }
        return retValue;
    }

    /**
     * Procedure shall be called from a child term in order to inform this object that it depends on an interval or
     * function
     */
    public void addLinkedEquation(EquationView linkedEquationView) {
        if (linkedEquationView == null) {
            return;
        }
        if (linkedEquationView.isInterval() && !directIntervals.contains(linkedEquationView)) {
            directIntervals.add(linkedEquationView);
        } else if (!linkedEquationView.isInterval() && !directFunctions.contains(linkedEquationView)) {
            directFunctions.add(linkedEquationView);
        }
    }

    /**
     * Procedure recursively collects linked intervals
     */
    protected ArrayList<EquationView> collectAllIntervals(ArrayList<LinkHolderView> callStack) {
        for (EquationView li : directIntervals) {
            if (!allIntervals.contains(li)) {
                allIntervals.add(li);
            }
        }

        // stack is used to prevent unlimited recursive calls
        ArrayList<LinkHolderView> stack = new ArrayList<>();
        if (callStack != null) {
            stack.addAll(callStack);
        }
        stack.add(this);

        for (EquationView e : directFunctions) {
            if (stack.contains(e)) {
                continue;
            }
            ArrayList<EquationView> tmpIntervals = e.collectAllIntervals(stack);
            for (EquationView li : tmpIntervals) {
                if (!allIntervals.contains(li)) {
                    allIntervals.add(li);
                }
            }
        }
        return allIntervals;
    }

    /**
     * Procedure recursively collects all linked functions
     */
    protected ArrayList<EquationView> collectAllFunctions(ArrayList<LinkHolderView> callStack) {
        // stack is used to prevent unlimited recursive calls
        ArrayList<LinkHolderView> stack = new ArrayList<>();
        if (callStack != null) {
            stack.addAll(callStack);
        }
        stack.add(this);

        for (EquationView e : directFunctions) {
            if (!allFunctions.contains(e)) {
                allFunctions.add(e);
            }
            if (stack.contains(e)) {
                continue;
            }
            ArrayList<EquationView> tmpFunctions = e.collectAllFunctions(stack);
            for (EquationView li : tmpFunctions) {
                if (!allFunctions.contains(li)) {
                    allFunctions.add(li);
                }
            }
        }
        return allFunctions;
    }
}
