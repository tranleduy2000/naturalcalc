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
package com.mkulesh.micromath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.duy.natural.calc.calculator.CalculatorActivity;
import com.mkulesh.micromath.export.ExportToImage;
import com.mkulesh.micromath.export.Exporter;
import com.mkulesh.micromath.fman.AdapterIf;
import com.mkulesh.micromath.fman.Commander;
import com.mkulesh.micromath.fman.FileType;
import com.mkulesh.micromath.fman.FileUtils;
import com.mkulesh.micromath.formula.FormulaList;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.io.File;
import java.io.FileOutputStream;

abstract public class BaseDisplayFragment extends Fragment implements OnClickListener {
    /**
     * Constants used to save/restore the instance state.
     */
    public static final String EXTERNAL_URI = "external_uri";
    public static final String POST_ACTION_ID = "post_action_id";
    public static final String OPENED_FILE = "opened_file"; // Not used since version 2.14.3
    public static final String OPENED_URI = "opened_uri";
    public static final String OPENED_FILE_EMPTY = "";
    public static final String FILE_READING_OPERATION = "file_reading_operation";

    public final static int INVALID_ACTION_ID = -1;

    protected View rootView = null;
    protected FormulaList mFormulaList = null;
    protected SharedPreferences preferences = null;
    private Menu mainMenu = null;
    private boolean inOperation = false;
    private OnClickListener stopHandler = null;


    abstract public void performAction(int itemId);

    abstract public void setXmlReadingResult(boolean success);

    protected void initializeFragment() {
        mFormulaList = new FormulaList(this, rootView);

        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        boolean delayedSetInOperationCall = (mainMenu != menu);
        mainMenu = menu;
        if (delayedSetInOperationCall) {
            setInOperation(inOperation, stopHandler);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mFormulaList.getXmlLoaderTask() != null) {
            outState.putString(FILE_READING_OPERATION, FILE_READING_OPERATION);
            mFormulaList.stopXmlLoaderTask();
        } else {
            mFormulaList.writeToBundle(outState);
        }
    }

    public Uri getOpenedFile() {
        Uri uri = null;
        // clear settings of previous version
        String str = preferences.getString(OPENED_FILE, null);
        if (str != null) {
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString(OPENED_FILE, null);
            prefEditor.putString("default_directory", null);
            prefEditor.putString("last_selected_file_type", null);
            prefEditor.commit();
            if (!str.equals(OPENED_FILE_EMPTY)) {
                uri = Uri.fromFile(new File(str));
            }
        } else {
            str = preferences.getString(OPENED_URI, OPENED_FILE_EMPTY);
            uri = str.equals(OPENED_FILE_EMPTY) ? null : Uri.parse(str);
        }
        if (uri != null) {
            ViewUtils.debug(this, "currently opened uri: " + uri.toString());
        }
        return uri;
    }

    protected void setOpenedFile(Uri uri) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(OPENED_URI, (uri == null) ? OPENED_FILE_EMPTY : uri.toString());
        prefEditor.commit();
    }

    protected void setWorksheetName(CharSequence name) {
    }

    protected void onSaveFinished() {
        // default implementation is empty
    }

    protected void saveFileAs(final boolean storeOpenedFileInfo) {
        Commander commander = new Commander(getActivity(), R.string.action_save_as, Commander.SelectionMode.SAVE_AS, null,
                new Commander.OnFileSelectedListener() {
                    public void onSelectFile(Uri uri, FileType fileType, final AdapterIf adapter) {
                        uri = FileUtils.ensureScheme(uri);
                        if (mFormulaList.writeToFile(uri)) {
                            if (storeOpenedFileInfo) {
                                setOpenedFile(uri);
                            }
                            onSaveFinished();
                        }
                    }
                });
        commander.setFileName(((CalculatorActivity) getActivity()).getWorksheetName());
        commander.show();
    }

    protected void export() {
        Commander commander = new Commander(getActivity(), R.string.action_export, Commander.SelectionMode.EXPORT, null,
                new Commander.OnFileSelectedListener() {
                    public void onSelectFile(Uri uri, FileType fileType, final AdapterIf adapter) {
                        uri = FileUtils.ensureScheme(uri);
                        mFormulaList.setSelectedFormula(ViewUtils.INVALID_INDEX, false);
                        final boolean res = Exporter.write(mFormulaList, uri, fileType, adapter, null);
                        String mime = null;
                        if (res) {
                            switch (fileType) {
                                case JPEG_IMAGE:
                                    mime = "image/jpeg";
                                    break;
                                case LATEX:
                                    break;
                                case MATHJAX:
                                    mime = "text/html";
                                    break;
                                case PNG_IMAGE:
                                    mime = "image/png";
                                    break;
                            }
                        }
                        if (mime != null) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, mime);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
        commander.show();
    }

    protected void exportImage() {
        mFormulaList.setSelectedFormula(ViewUtils.INVALID_INDEX, false);
        String path = "images" + File.separator + System.currentTimeMillis() + ".png";
        File file = new File(getContext().getFilesDir(), path);
        try {
            if (!file.exists()) if (file.getParentFile().mkdir()) file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            final ExportToImage writer = new ExportToImage(stream);
            writer.write(mFormulaList.getFormulaListView(), Bitmap.CompressFormat.PNG);

            Uri uri = FileProvider.getUriForFile(getContext(), "com.nstudio.calc.casio.FileProvider", file);
            Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                    .getIntent()
                    .setAction(Intent.ACTION_SEND)
                    .setDataAndType(uri, "image/png")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculate() {
        mFormulaList.doCalculate();
    }

    public void setInOperation(boolean inOperation, OnClickListener stopHandler) {
        this.inOperation = inOperation;
        this.stopHandler = stopHandler;
        if (mainMenu == null) {
            return;
        }

        // update menu items
        for (int i = 0; i < mainMenu.size(); i++) {
            MenuItem m = mainMenu.getItem(i);
            m.setEnabled(!inOperation);

            // update undo button
            if (m.getItemId() == R.id.action_undo && !inOperation) {
                mFormulaList.getUndoState().updateMenuItemState(m);
            }

            ViewUtils.updateMenuIconColor(getActivity(), m);
        }
        // update progress bar
        final ProgressBar progressBar = getActivity().findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(inOperation ? View.VISIBLE : View.GONE);
        }
    }

    public boolean isInOperation() {
        return inOperation;
    }

    public boolean isFirstStart() {
        return !preferences.contains(OPENED_FILE) && !preferences.contains(OPENED_URI);
    }

    @Override
    public void onClick(View b) {
    }

    public void updateModeTitle() {
        ActionMode mode = ((CalculatorActivity) getActivity()).getActionMode();
        if (mode != null) {
            final int selected = mFormulaList.getSelectedEquations().size();
            final int total = mFormulaList.getEquationsNumber();
            if (selected == 0) {
                mode.setTitle("");
            } else {
                mode.setTitle(String.valueOf(selected) + "/" + String.valueOf(total));
            }
            Menu m = mode.getMenu();
            if (m != null) {
                MenuItem mi = m.findItem(R.id.context_menu_expand);
                if (mi != null) {
                    mi.setVisible(total > selected);
                }
            }
        }
    }

}
