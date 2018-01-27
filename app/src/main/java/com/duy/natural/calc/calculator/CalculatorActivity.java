package com.duy.natural.calc.calculator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.duy.common.utils.ShareUtil;
import com.duy.common.utils.StoreUtil;
import com.duy.natural.calc.calculator.display.DisplayFragment;
import com.duy.natural.calc.calculator.evaluator.SystemLoaderTask;
import com.duy.natural.calc.calculator.keyboard.KeyboardFragment;
import com.duy.natural.calc.calculator.settings.BaseActivity;
import com.duy.natural.calc.calculator.settings.SettingActivity;
import com.kobakei.ratethisapp.RateThisApp;
import com.mkulesh.micromath.BaseDisplayFragment;
import com.mkulesh.micromath.editstate.clipboard.FormulaClipboardData;
import com.mkulesh.micromath.fman.AdapterDocuments;
import com.mkulesh.micromath.utils.AppLocale;
import com.mkulesh.micromath.utils.CompatUtils;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Duy on 1/13/2018.
 */

public class CalculatorActivity extends BaseActivity {
    /**
     * Constants used to save/restore the instance state.
     */
    private static final String STATE_WORKSHEET_NAME = "worksheet_name";
    private static final String STATE_STORED_FORMULA = "stored_formula";
    private static final int STORAGE_PERMISSION_REQID = 255;
    private static final int SETTINGS_ACTIVITY_REQID = 256;

    private CalculatorContract.IPresenter mPresenter;
    private Dialog mStoragePermissionDialog = null;
    private int mStoragePermissionAction = ViewUtils.INVALID_INDEX;
    private FormulaClipboardData mFormulaClipboardData = null;
    private CharSequence mWorksheetName = null;
    private Toolbar mToolbar = null;
    private ArrayList<ActionMode> mActiveActionModes = null;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_calculator);

        bindView();


        KeyboardFragment keyboard = KeyboardFragment.newInstance();
        replaceFragment(keyboard, R.id.container_keyboard);
        DisplayFragment display = DisplayFragment.newInstance();
        replaceFragment(display, R.id.container_display);

        mPresenter = new CalculatorPresenter(display, keyboard);
        mPresenter.onCreate();

        mActiveActionModes = new ArrayList<>();
        showDialogRate();

        new SystemLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void bindView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle listener = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(listener);
        listener.syncState();
    }

    private void showDialogRate() {
        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void replaceFragment(Fragment fragment, @IdRes int layoutId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutId, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        mStoragePermissionAction = ViewUtils.INVALID_INDEX;

        switch (menuItem.getItemId()) {
            case R.id.action_undo:
            case R.id.action_new:
            case R.id.action_discard:
            case R.id.action_new_document:
            case R.id.action_export_image:
            case R.id.action_change_size: {
                BaseDisplayFragment baseDisplayFragment = getVisibleFragment();
                if (baseDisplayFragment == null) {
                    return true;
                }
                baseDisplayFragment.performAction(menuItem.getItemId());
                return true;
            }
            case R.id.action_open:
            case R.id.action_save:
            case R.id.action_save_as:
            case R.id.action_export:
            case R.id.action_save_to_file: {
                if (checkStoragePermission(menuItem.getItemId())) {
                    BaseDisplayFragment baseDisplayFragment = getVisibleFragment();
                    if (baseDisplayFragment == null) {
                        return true;
                    }
                    baseDisplayFragment.performAction(menuItem.getItemId());
                }
                return true;
            }
            case R.id.action_setting:
                openSetting();
                return true;
            case R.id.action_share:
                ShareUtil.shareThisApp(this);
                return true;
            case R.id.action_rate:
                StoreUtil.gotoPlayStore(this, getPackageName());
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void openSetting() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        final Locale prefLocale = AppLocale.ContextWrapper.getPreferredLocale(newBase);
        super.attachBaseContext(AppLocale.ContextWrapper.wrap(newBase, prefLocale));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mFormulaClipboardData != null) {
            outState.putParcelable(STATE_STORED_FORMULA, mFormulaClipboardData.onSaveInstanceState());
        }
        if (getWorksheetName() != null) {
            outState.putCharSequence(STATE_WORKSHEET_NAME, getWorksheetName());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        try {
            final Parcelable s = inState.getParcelable(STATE_STORED_FORMULA);
            if (s != null) {
                mFormulaClipboardData = new FormulaClipboardData();
                mFormulaClipboardData.onRestoreInstanceState(s);
            }
        } catch (Exception e) {
            ViewUtils.debug(this, e.getLocalizedMessage());
            mFormulaClipboardData = null;
        }
        CharSequence w = inState.getCharSequence(STATE_WORKSHEET_NAME);
        if (w != null) {
            setWorksheetName(w);
        }
        super.onRestoreInstanceState(inState);
    }

    public ActionMode getActionMode() {
        if (!mActiveActionModes.isEmpty()) {
            return mActiveActionModes.get(mActiveActionModes.size() - 1);
        }
        return null;
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        mToolbar.setVisibility(View.INVISIBLE);
        super.onSupportActionModeStarted(mode);
        mActiveActionModes.add(mode);
        final BaseDisplayFragment f = getVisibleFragment();
        if (f != null) {
            f.updateModeTitle();
        }
    }

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        mToolbar.setVisibility(View.VISIBLE);
        mActiveActionModes.remove(mode);
    }

    /**
     * Procedure enforces the currently active action mode to be finished
     */
    public void finishActiveActionMode() {
        for (ActionMode mode : mActiveActionModes) {
            mode.finish();
        }
    }

    /**
     * Procedure return a stored formula from the internal clipboard
     */
    public FormulaClipboardData getStoredFormula() {
        return mFormulaClipboardData;
    }

    /**
     * Procedure stores given formula into the internal clipboard
     */
    public void setStoredFormula(FormulaClipboardData term) {
        this.mFormulaClipboardData = term;
    }


    public CharSequence getWorksheetName() {
        return mWorksheetName;
    }

    public void setWorksheetName(CharSequence name) {
        this.mWorksheetName = name;
    }

    public BaseDisplayFragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && (fragment instanceof BaseDisplayFragment)) {
                return (BaseDisplayFragment) fragment;
            }
        }
        return null;
    }

    /**
     * Storage permission handling
     */
    public boolean checkStoragePermission(int action) {
        if (CompatUtils.isMarshMallowOrLater()) {
            final boolean granted = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                return true;
            }
            ViewUtils.debug(this, "storage permissions are not granted");
            mStoragePermissionAction = action;
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (isFinishing() || (mStoragePermissionDialog != null && mStoragePermissionDialog.isShowing())) {
                    return false;
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setIcon(mStoragePermissionAction == R.id.action_open ? R.drawable.ic_action_content_open
                        : R.drawable.ic_action_content_save);
                alert.setTitle(getString(R.string.allow_storage_access_title));
                alert.setMessage(getString(R.string.allow_storage_access_description));
                alert.setNegativeButton(getString(R.string.dialog_navigation_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // nothing to do
                            }
                        });
                alert.setPositiveButton(getString(R.string.allow_storage_access_grant),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                requestStoragePermission();
                            }
                        });
                mStoragePermissionDialog = alert.show();
            } else {
                requestStoragePermission();
            }
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private void requestStoragePermission() {
        ViewUtils.debug(this, "requesting storage permissions");
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQID: {
                // If request is cancelled, the result arrays are empty.
                if (mStoragePermissionAction != ViewUtils.INVALID_INDEX && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ViewUtils.debug(this, "permission was granted, performing file operation action");
                    final BaseDisplayFragment f = getVisibleFragment();
                    if (f != null) {
                        f.performAction(mStoragePermissionAction);
                    }
                } else {
                    String error = getResources().getString(R.string.allow_storage_access_description);
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
                return;
            }
            default:
                // nothing to do
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AdapterDocuments.REQUEST_OPEN_DOCUMENT_TREE && data != null) {
            Uri uri = data.getData();
            AdapterDocuments.saveURI(this, uri);
        } else if (requestCode == SETTINGS_ACTIVITY_REQID) {
            restartActivity();
        }
    }
}
