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
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

public class ContextMenuHandler {
    private final boolean[] mEnabled = new boolean[Type.values().length];
    private final Context context;
    private OnFormulaChangeListener mOnFormulaChangeListener = null;
    private ActionMode mActionMode = null;
    private View mActionModeOwner = null;
    private Menu mMenu = null;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            ContextMenuHandler.this.mMenu = menu;
            for (int i = 0; i < menu.size(); i++) {
                ViewUtils.updateMenuIconColor(context, menu.getItem(i));
            }
            for (int i = 0; i < Type.values().length; i++) {
                menu.findItem(Type.values()[i].getResId()).setVisible(mEnabled[i]);
            }
            if (mOnFormulaChangeListener != null) {
                mOnFormulaChangeListener.onCreateContextMenu(mActionModeOwner, ContextMenuHandler.this);
            }
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
            if (mOnFormulaChangeListener != null) {
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
            if (mOnFormulaChangeListener != null) {
                mOnFormulaChangeListener.finishActionMode(mActionModeOwner);
            }
        }
    };

    public ContextMenuHandler(Context context) {
        this.context = context;
        for (int i = 0; i < Type.values().length; i++) {
            mEnabled[i] = true;
        }
    }

    public void initialize(TypedArray data) {
        mEnabled[Type.EXPAND.ordinal()] = data.getBoolean(R.styleable.FormulaEditText_contextMenuExpand, true);
        mEnabled[Type.CUT.ordinal()] = data.getBoolean(R.styleable.FormulaEditText_contextMenuCut, true);
        mEnabled[Type.COPY.ordinal()] = data.getBoolean(R.styleable.FormulaEditText_contextMenuCopy, true);
        mEnabled[Type.PASTE.ordinal()] = data.getBoolean(R.styleable.FormulaEditText_contextMenuPaste, true);
    }

    public boolean isMenuEmpty() {
        for (int i = 0; i < Type.values().length; i++) {
            if (mEnabled[i]) {
                return false;
            }
        }
        return true;
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void startActionMode(AppCompatActivity activity, View actionModeOwner, OnFormulaChangeListener onFormulaChangeListener) {
        mActionModeOwner = actionModeOwner;
        mOnFormulaChangeListener = onFormulaChangeListener;

        ArrayList<View> list = null;
        if (mActionModeOwner != null && mActionModeOwner instanceof FormulaEditText) {
            list = new ArrayList<>();
            list.add(mActionModeOwner);
        }

        mOnFormulaChangeListener.onTermSelection(mActionModeOwner, true, list);
        if (isMenuEmpty()) {
            mOnFormulaChangeListener.onObjectProperties(mActionModeOwner);
            mOnFormulaChangeListener.onTermSelection(mActionModeOwner, false, list);
            return;
        }
        mActionMode = activity.startSupportActionMode(mActionModeCallback);
    }

    private boolean processMenu(int itemId) {
        switch (itemId) {
            case R.id.context_menu_expand:
                OnFormulaChangeListener onFormulaChangeListener = mOnFormulaChangeListener.onExpandSelection(mActionModeOwner, this);
                if (onFormulaChangeListener != null) {
                    mOnFormulaChangeListener = onFormulaChangeListener;
                    mOnFormulaChangeListener.onTermSelection(null, true, null);
                    mActionModeOwner = null;
                }
                return false;

            case R.id.context_menu_cut:
                if (mActionModeOwner != null && mActionModeOwner instanceof FormulaEditText) {
                    FormulaEditText t = (FormulaEditText) mActionModeOwner;
                    ClipboardManager.copyToClipboard(context, t.getText().toString());
                    if (t.getText().length() == 0) {
                        mOnFormulaChangeListener.onDelete(t);
                        mActionModeOwner = null;
                    } else {
                        t.setText("");
                    }
                } else {
                    mOnFormulaChangeListener.onCopyToClipboard();
                    mOnFormulaChangeListener.onDelete(null);
                }
                break;

            case R.id.context_menu_copy:
                if (mActionModeOwner != null && mActionModeOwner instanceof FormulaEditText) {
                    FormulaEditText t = (FormulaEditText) mActionModeOwner;
                    ClipboardManager.copyToClipboard(context, t.getText().toString());
                } else {
                    mOnFormulaChangeListener.onCopyToClipboard();
                }
                break;

            case R.id.context_menu_paste:
                mOnFormulaChangeListener.onPasteFromClipboard(mActionModeOwner, ClipboardManager.readFromClipboard(context, true));
                break;
            default:
                break;
        }
        return true;
    }

    public void setMenuVisible(int id, boolean visible) {
        for (int i = 0; i < mMenu.size(); i++) {
            if (id == mMenu.getItem(i).getItemId()) {
                mMenu.getItem(i).setVisible(visible);
                break;
            }
        }
    }

    enum Type {
        EXPAND(R.id.context_menu_expand),
        CUT(R.id.context_menu_cut),
        COPY(R.id.context_menu_copy),
        PASTE(R.id.context_menu_paste);

        private final int resId;

        Type(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

}
