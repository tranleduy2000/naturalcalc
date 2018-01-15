/*
 * Copyright (c) 2017 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.common.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.duy.common.ads.AdsSupportActivity;
import com.duy.common.utils.DLog;
import com.google.firebase.analytics.FirebaseAnalytics;

import aidl.util.IabBroadcastReceiver;


/**
 * Created by Duy on 9/14/2017.
 */
public abstract class InAppPurchaseActivity extends AdsSupportActivity implements IabBroadcastReceiver.IabBroadcastListener {
    public static final int REQUEST_CODE_UPGRADE = 6032;
    private static final String TAG = "InAppPurchaseActivity";
    private final Handler mHandler = new Handler();
    private FirebaseAnalytics mFirebaseAnalytics;
    private InAppPurchaseHelper mInAppPurchaseHelper;

    @CallSuper
    @Deprecated
    public void updateUiPremium() {
        if (DLog.DEBUG) DLog.d(TAG, "updateUiPremium() called");
        throw new RuntimeException("Deprecated method, use updateUi(boolean premium)");
    }

    public void updateUi(boolean premium) {
        if (DLog.DEBUG) DLog.d(TAG, "updateUi() called with: premium = [" + premium + "]");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
        mInAppPurchaseHelper.onCreate();
    }

    public void clickUpgrade() {
        mFirebaseAnalytics.logEvent("click_upgrade", new Bundle());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInAppPurchaseHelper.showDialogUprade();
            }
        }, 50);
    }

    @Override
    public void receivedBroadcast() {
        mInAppPurchaseHelper.receivedBroadcast();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handled = mInAppPurchaseHelper.onActivityResult(requestCode, resultCode, data);
        if (!handled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        mInAppPurchaseHelper.onDestroy();
        super.onDestroy();
    }

    protected void showDialogUpgrade() {
        mInAppPurchaseHelper.showDialogUprade();
    }
}
