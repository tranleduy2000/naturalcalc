package com.mkulesh.micromath.formula.io;

import java.io.OutputStream;

/**
 * Created by Duy on 1/14/2018.
 */

interface IFormulaWritter {
    boolean write(OutputStream stream, String fileName);
}
