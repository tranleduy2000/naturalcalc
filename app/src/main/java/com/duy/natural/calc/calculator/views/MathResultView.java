package com.duy.natural.calc.calculator.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.duy.natural.calc.calculator.settings.CalculatorSetting;

import io.github.kexanie.library.MathView;

/**
 * Created by Duy on 1/16/2018.
 */

public class MathResultView extends MathView {
    public MathResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public MathResultView(Context context) {
        super(context, null);
        setup(context);
    }

    private void setup(Context context) {
        setEngine(Engine.MATHJAX);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        CalculatorSetting setting = new CalculatorSetting(context);
        String color = !setting.useLightTheme() ? "#FFFFFF" : "#000000";
        String config = String.format("MathJax.Hub.Config({" +
                "\"HTML-CSS\":{styles:{\".MathJax\":{color:\"%s\"}}}});", color);
        config(config);
    }


}
