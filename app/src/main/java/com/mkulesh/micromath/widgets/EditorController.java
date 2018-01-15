package com.mkulesh.micromath.widgets;

import android.view.KeyEvent;

/**
 * Created by Duy on 1/14/2018.
 */

interface EditorController {
    /**
     * Insert text at the cursor
     *
     * @param text - text to insert
     */
    void insert(String text);

    /**
     * Move cursor to the left
     *
     * @return true if has been moved
     */
    boolean moveLeft();

    /**
     * Move cursor to the right
     *
     * @return true if has been moved
     */
    boolean moveRight();

    /**
     * handle delete key event
     */
    boolean processDelKey(KeyEvent event);
}
