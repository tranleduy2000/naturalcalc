package com.duy.natural.calc.calculator;

import com.duy.natural.calc.calculator.calcbutton.Category;

/**
 * Created by Duy on 1/13/2018.
 */

public class CalculatorContract {
    public interface IPresenter {
        IDisplayView getDisplayView();

        IKeyboardView getKeyboardView();

        void onCreate();

        void onPause();

        void onResume();
    }

    public interface IDisplayView {
        void setPresenter(IPresenter presenter);

        void showProgressBar();

        void hideProgressBar();

        void onButtonPressed(String code);
    }


    public interface IKeyboardView {
        void setPresenter(IPresenter presenter);

        void onButtonPressed(String code);

        void setPaletteBlockEnabled(Category category, boolean enabled);

        void enableHiddenInput(boolean hiddenInputEnabled);

        void setEnabled(boolean enable);
    }
}
