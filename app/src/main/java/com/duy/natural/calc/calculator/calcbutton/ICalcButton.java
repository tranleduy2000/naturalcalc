package com.duy.natural.calc.calculator.calcbutton;

import android.support.annotation.Nullable;

/**
 * Created by Duy on 1/14/2018.
 */
public interface ICalcButton {
    Category[] getCategories();

    void setCategories(Category[] categories);

    @Nullable
    String getCategoryCode();

    void setEnabled(Category category, boolean enabled);

    void initWithParameter(int shortCutId, int descriptionId, String code);

    boolean isEnabled();

    void setTint(int color);

    @Nullable
    String getDocumentPath();
}
