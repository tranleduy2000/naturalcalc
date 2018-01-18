/*******************************************************************************
 * microMathematics Plus - Extended visual calculator
 * *****************************************************************************
 * Copyright (C) 2014-2017 Mikhail Kulesh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.mkulesh.micromath.widgets;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mkulesh.micromath.formula.FormulaList;
import com.nstudio.calc.casio.R;

public class ScaleMenuHandler {
    private FormulaList mOnListChangeListener = null;
    private ActionMode mActionMode = null;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_scale, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (mOnListChangeListener != null) {
                if (processMenu(item.getItemId())) {
                    mode.finish();
                }
            }
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    public ScaleMenuHandler(Context context) {
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void startActionMode(AppCompatActivity activity, FormulaList onListChangeListener) {
        mOnListChangeListener = onListChangeListener;
        mActionMode = activity.startSupportActionMode(mActionModeCallback);
    }

    private boolean processMenu(int itemId) {
        switch (itemId) {
            case R.id.action_zoom_out:
                mOnListChangeListener.onScale(0.9f);
                return false;

            case R.id.action_zoom_in:
                mOnListChangeListener.onScale(1.1f);
                return false;

            case R.id.action_zoom_reset:
                mOnListChangeListener.getDimen().reset();
                mOnListChangeListener.onScale(1);
                return true;
        }
        return true;
    }


}
