package com.mkulesh.micromath.formula.type;

import com.nstudio.calc.casio.R;

/**
 * Some functions can be triggered from keyboard. This enumeration defines these triggers
 */
public enum FunctionTrigger {
    GENERAL(R.string.formula_function_start_bracket, null, true) {
        @Override
        public String toString() {
            return "(";
        }
    },
    INDEX(R.string.formula_function_start_index, FunctionType.FUNCTION_INDEX, true) {
        @Override
        public String toString() {
            return "[";
        }
    },
    ABS(R.string.formula_function_abs_layout, FunctionType.ABS_LAYOUT, true) {
        @Override
        public String toString() {
            return "Abs";
        }
    },
    SQRT(R.string.formula_function_sqrt_layout, FunctionType.SQRT_LAYOUT, true) {
        @Override
        public String toString() {
            return "Sqrt";
        }
    },
    POWER(R.string.formula_function_power, FunctionType.POWER_LAYOUT, false) {
        @Override
        public String toString() {
            return "Power";
        }
    },
    SURD(R.string.formula_function_nthrt_layout, FunctionType.SURD_LAYOUT, true) {
        @Override
        public String toString() {
            return "Surd";
        }
    },
    FACTORIAL(R.string.formula_function_factorial_layout, FunctionType.FACTORIAL_LAYOUT, false) {
        @Override
        public String toString() {
            return "Factorial";
        }
    },
    CONJUGATE(R.string.formula_function_conjugate_layout, FunctionType.CONJUGATE_LAYOUT, false) {
        @Override
        public String toString() {
            return "Conjugate";
        }
    };

    private final int codeId;
    private final FunctionType functionType;
    private final boolean isBeforeText;

    FunctionTrigger(int codeId, FunctionType functionType, boolean isBeforeText) {
        this.codeId = codeId;
        this.functionType = functionType;
        this.isBeforeText = isBeforeText;
    }

    public int getCodeId() {
        return codeId;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public boolean isBeforeText() {
        return isBeforeText;
    }
}
