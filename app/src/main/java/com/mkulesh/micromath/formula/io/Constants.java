package com.mkulesh.micromath.formula.io;

/**
 * Created by Duy on 1/14/2018.
 */

public class Constants {
    /**
     * Constants used to write/read the XML file.
     */
    public static final String XML_NS = null;
    public static final String XML_HTTP = "http://micromath.mkulesh.com";
    public static final String XML_PROP_MMT = "mmt";
    public static final String XML_PROP_KEY = "key";
    public static final String XML_PROP_CODE = "code";
    public static final String XML_PROP_TEXT = "text";
    public static final String XML_PROP_INRIGHTOFPREVIOUS = "inRightOfPrevious";
    public static final String XML_MAIN_TAG = "micromath";
    public static final String XML_LIST_TAG = "formulaList";
    public static final String XML_TERM_TAG = "term";

    /**
     * Constants used to save/restore the instance state.
     */
    public static final String STATE_FORMULA_NUMBER = "formula_number";
    public static final String STATE_FORMULA_TYPE = "formula_type_";
    public static final String STATE_FORMULA_STATE = "formula_state_";
    public static final String STATE_SELECTED_LINE = "selected_line";
    public static final String STATE_UNDO_STATE = "undo_state";
}
