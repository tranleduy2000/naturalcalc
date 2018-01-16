package com.duy.natural.calc.calculator.keyboard.models;

public final class FunctionItem {
    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_FUNCTION = 2;
    public final int type;
    public final Object data;

    public FunctionItem(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}