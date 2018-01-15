package com.duy.natural.calc.calculator.calcbutton.button;

/**
 * Created by Duy on 1/14/2018.
 */
public interface ICalcButton {
    Category[] getCategories();

    void setCategories(Category[] categories);

    String getCategoryCode();

    void setEnabled(Category category, boolean enabled);

    void initWithParameter(int shortCutId, int descriptionId, String code);
}
