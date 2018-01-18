package com.duy.natural.calc.calculator.display;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.duy.natural.calc.calculator.CalculatorContract;
import com.mkulesh.micromath.BaseDisplayFragment;
import com.mkulesh.micromath.dialogs.DialogNewFormula;
import com.mkulesh.micromath.fman.AdapterIf;
import com.mkulesh.micromath.fman.Commander;
import com.mkulesh.micromath.fman.FileType;
import com.mkulesh.micromath.fman.FileUtils;
import com.mkulesh.micromath.formula.io.XmlLoaderTask;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.ScaleMenuHandler;
import com.nstudio.calc.casio.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Duy on 1/13/2018.
 */

public class DisplayFragment extends BaseDisplayFragment implements CalculatorContract.IDisplayView {
    public static final String AUTOSAVE_FILE_NAME = "autosave.mmt";
    protected boolean invalidateFile = false;
    private CalculatorContract.IPresenter mPresenter;
    private Uri externalUri = null;
    private int postActionId = INVALID_ACTION_ID;
    private ProgressBar mProgressBar;

    public static DisplayFragment newInstance() {
        Bundle args = new Bundle();
        DisplayFragment fragment = new DisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(CalculatorContract.IPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonPressed(String code) {
        mFormulaList.onButtonPressed(code);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.progress_bar);
    }

    @CallSuper
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_display, container, false);
        externalUri = getArguments().getParcelable(EXTERNAL_URI);
        postActionId = getArguments().getInt(POST_ACTION_ID, INVALID_ACTION_ID);
        // remove POST_ACTION_ID from bundle sinc fragment activation event, seee it will be kept
        // until the next issue #33
        getArguments().putInt(POST_ACTION_ID, INVALID_ACTION_ID);
        initializeFragment();
        initializeFormula(savedInstanceState);
        if (mPresenter != null) {
            mFormulaList.setKeyboardView(mPresenter.getKeyboardView());
            mFormulaList.setDisplayView(mPresenter.getDisplayView());
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*menu.findItem(R.id.action_open).setVisible(true);
        menu.findItem(R.id.action_save).setVisible(true);*/
    }

    @Override
    public void onPause() {
        saveFile(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        if (invalidateFile) {
            setXmlReadingResult(false);
        }
        if (postActionId != INVALID_ACTION_ID) {
            performAction(postActionId);
            postActionId = INVALID_ACTION_ID;
        }
        super.onResume();
    }


    private void initializeFormula(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getString(FILE_READING_OPERATION) != null) {
            ViewUtils.debug(this, "cannot restore state: state is saved before a reading operation is finished");
            savedInstanceState = null;
        }
        if (isFirstStart()) {
            Uri resource = Uri.parse(getResources().getString(R.string.activity_welcome));
            mFormulaList.readFromResource(resource, XmlLoaderTask.PostAction.CALCULATE);
        } else if (postActionId != INVALID_ACTION_ID) {
            setOpenedFile(null);
        } else if (savedInstanceState != null) {
            try {
                mFormulaList.readFromBundle(savedInstanceState);
            } catch (Exception e) {
                ViewUtils.debug(this, "cannot restore state: " + e.getLocalizedMessage());
                mFormulaList.clearAll();
                invalidateFile = true;
            }
        } else if (externalUri != null) {
            ViewUtils.debug(this, "external uri is passed: " + externalUri.toString());
            if (mFormulaList.readFromFile(externalUri)) {
                setOpenedFile(externalUri);
            }
        } else {
            Uri uri = getOpenedFile();
            if (uri != null) {
                if (mFormulaList.readFromFile(uri)) {
                    setWorksheetName(FileUtils.getFileName(getActivity(), uri));
                } else {
                    setOpenedFile(null);
                }
            }
        }
    }

    private void saveFile(boolean manualSave) {
        if (!manualSave) {
            if (mFormulaList.getXmlLoaderTask() != null) {
                return;
            }
        }
        Uri uri = getOpenedFile();
        if (uri != null) {
            if (FileUtils.isAssetUri(uri)) {
                // no need to save an asset: just ignore this case
            } else {
                mFormulaList.writeToFile(uri);
            }
        } else if (manualSave) {
            saveFileAs(true);
        } else {
            File file = new File(getActivity().getExternalFilesDir(null), AUTOSAVE_FILE_NAME);
            if (file != null) {
                uri = Uri.fromFile(file);
                if (mFormulaList.writeToFile(uri)) {
                    setOpenedFile(uri);
                }
            }
        }
    }

    public void openFile() {
        Commander commander = new Commander(getActivity(), R.string.action_open, Commander.SelectionMode.OPEN, null,
                new Commander.OnFileSelectedListener() {
                    public void onSelectFile(Uri uri, FileType fileType, final AdapterIf adapter) {
                        saveFile(false);
                        uri = FileUtils.ensureScheme(uri);
                        if (mFormulaList.readFromFile(uri)) {
                            setOpenedFile(uri);
                        } else {
                            setOpenedFile(null);
                        }
                    }
                });
        commander.show();
    }

    @Override
    public void performAction(int itemId) {
        switch (itemId) {
            case R.id.action_undo:
                mFormulaList.undo();
                break;
            case R.id.action_new:
                DialogNewFormula d1 = new DialogNewFormula(getActivity(), mFormulaList);
                d1.show();
                break;
            case R.id.action_discard:
                mFormulaList.onDiscardFormula(mFormulaList.getSelectedFormulaId());
                break;
            case R.id.action_new_document:
                if (postActionId != R.id.action_new_document) {
                    // postActionId == R.id.action_new_document means that the fragment is called from an
                    // asset and we do not need to save anything
                    saveFile(false);
                }
                mFormulaList.clearAll();
                setOpenedFile(null);
                break;
            case R.id.action_open:
                openFile();
                break;
            case R.id.action_save:
                saveFile(true);
                break;
            case R.id.action_save_as:
                saveFileAs(true);
                break;
            case R.id.action_export:
                export();
                break;
            case R.id.action_export_image:
                exportImage();
                break;
            case R.id.action_save_to_file:
                createExample();
                break;
            case R.id.action_change_size:
                openZoomControl();
                break;

        }
    }

    private void openZoomControl() {
        ScaleMenuHandler mScaleMenuHandler = new ScaleMenuHandler(getContext());
        mScaleMenuHandler.startActionMode((AppCompatActivity) getActivity(), mFormulaList);
    }

    @VisibleForTesting
    private void createExample() {
        File file = new File(getContext().getFilesDir(), "tmp.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFormulaList.writeToFile(Uri.fromFile(file));
    }


    @Override
    public void setXmlReadingResult(boolean success) {
        if (!success) {
            setOpenedFile(null);
            externalUri = null;
            invalidateFile = false;
        }
    }

}
