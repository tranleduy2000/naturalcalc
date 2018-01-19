package com.mkulesh.micromath.formula.type;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/14/2018.
 */

public enum ActionType implements FormulaTermType {
    DELETE(R.string.action_delete, R.id.btn_delete),
    CLEAR(R.string.action_clear_all, R.id.btn_clear),
    CALCULATE(R.string.action_calculate, R.id.btn_calculate),
    MOVE_LEFT(R.string.action_move_left, R.id.btn_arrow_left),
    MOVE_RIGHT(R.string.action_move_right, R.id.btn_arrow_right);

    private final int viewId;
    private int descriptionId;

    ActionType(@StringRes int descriptionId, @IdRes int viewId) {
        this.descriptionId = descriptionId;
        this.viewId = viewId;
    }

    @Nullable
    public static ActionType getActionType(String code) {
        for (ActionType type : values()) {
            if (type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public int getDescriptionId() {
        return descriptionId;
    }

    @NonNull
    @Override
    public String getCode() {
        return name().toLowerCase();
    }

    public int getViewId() {
        return viewId;
    }

    @NonNull
    @Override
    public String getLowerCaseName() {
        return null;
    }

    @Nullable
    @Override
    public TermType getType() {
        return null;
    }
}
