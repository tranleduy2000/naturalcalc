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
package com.mkulesh.micromath.dialogs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;

import com.mkulesh.micromath.formula.type.FormulaType;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.OnListChangeListener;
import com.nstudio.calc.casio.R;

import java.util.HashMap;
import java.util.Map;

public class DialogNewFormula extends DialogBase implements OnLongClickListener {
    public static final String LAST_INSERTED_POSITION = "last_inserted_position";
    public static final String LAST_INSERTED_OBJECT = "last_inserted_object";
    public static final String LAST_INSERTED_EMPTY = "empty";

    private final OnListChangeListener changeIf;
    private final HashMap<OnListChangeListener.Position, ImageButton> positionButtons = new HashMap<>();
    private final HashMap<FormulaType, ImageButton> objectButtons = new HashMap<>();

    public DialogNewFormula(Activity context, OnListChangeListener onListChangeListener) {
        super(context, R.layout.dialog_new_formula, R.string.math_new_element);

        // position buttons
        positionButtons.put(OnListChangeListener.Position.BEFORE, (ImageButton) findViewById(R.id.dialog_button_insert_before));
        positionButtons.put(OnListChangeListener.Position.AFTER, (ImageButton) findViewById(R.id.dialog_button_insert_after));
        positionButtons.put(OnListChangeListener.Position.LEFT, (ImageButton) findViewById(R.id.dialog_button_insert_left));
        positionButtons.put(OnListChangeListener.Position.RIGHT, (ImageButton) findViewById(R.id.dialog_button_insert_right));
        for (ImageButton b : positionButtons.values()) {
            setButtonSelected(b, false);
            b.setOnClickListener(this);
            b.setOnLongClickListener(this);
        }
        String str = pref.getString(LAST_INSERTED_POSITION, LAST_INSERTED_EMPTY);
        try {
            OnListChangeListener.Position insertType = OnListChangeListener.Position.valueOf(str);
            setButtonSelected(positionButtons.get(insertType), true);
        } catch (Exception e) {
            setButtonSelected(positionButtons.get(OnListChangeListener.Position.AFTER), true);
        }

        // object buttons
        objectButtons.put(FormulaType.EQUATION,
                (ImageButton) findViewById(R.id.dialog_button_new_equation));
        objectButtons.put(FormulaType.RESULT, (ImageButton) findViewById(R.id.dialog_button_new_result));
        for (ImageButton b : objectButtons.values()) {
            setButtonSelected(b, false);
            b.setOnClickListener(this);
            b.setOnLongClickListener(this);
        }
        str = pref.getString(LAST_INSERTED_OBJECT, LAST_INSERTED_EMPTY);
        try {
            FormulaType formulaType = FormulaType.valueOf(str);
            setButtonSelected(objectButtons.get(formulaType), true);
        } catch (Exception e) {
            setButtonSelected(objectButtons.get(FormulaType.EQUATION), true);
        }

        this.changeIf = onListChangeListener;
    }

    @Override
    public void onClick(View v) {
        if (positionButtons.containsValue(v)) {
            // position buttons
            for (ImageButton b : positionButtons.values()) {
                setButtonSelected(b, v == b);
            }
            return;
        } else if (objectButtons.containsValue(v)) {
            // object buttons
            for (ImageButton b : objectButtons.values()) {
                setButtonSelected(b, v == b);
            }
            return;
        } else if (v.getId() == R.id.dialog_button_ok && changeIf != null) {
            // inspect position buttons
            OnListChangeListener.Position insertType = OnListChangeListener.Position.AFTER;
            for (Map.Entry<OnListChangeListener.Position, ImageButton> e : positionButtons.entrySet()) {
                if (e.getValue().isSelected()) {
                    insertType = e.getKey();
                    SharedPreferences.Editor prefEditor = pref.edit();
                    prefEditor.putString(LAST_INSERTED_POSITION, insertType.toString());
                    prefEditor.commit();
                }
            }
            // inspect object buttons
            FormulaType formulaType = FormulaType.EQUATION;
            for (Map.Entry<FormulaType, ImageButton> e : objectButtons.entrySet()) {
                if (e.getValue().isSelected()) {
                    formulaType = e.getKey();
                    SharedPreferences.Editor prefEditor = pref.edit();
                    prefEditor.putString(LAST_INSERTED_OBJECT, formulaType.toString());
                    prefEditor.commit();
                }
            }
            changeIf.onNewFormula(insertType, formulaType);
        }
        closeDialog(/*hideKeyboard=*/ false);
    }

    @Override
    public boolean onLongClick(View b) {
        return ViewUtils.showButtonDescription(getContext(), b);
    }

}
