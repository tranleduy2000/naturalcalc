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

package com.duy.common.ads;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.duy.common.R;
import com.duy.common.purchase.Premium;

/**
 * Created by Duy on 29-Dec-17.
 *
 * @since 1.0.7
 */
public class AdsSupportActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    protected Toolbar mToolbar;
    /**
     * Activity on top of screen
     */
    private boolean isVisible = true;
    private final Runnable mShowAds = new Runnable() {
        @Override
        public void run() {
            AdsManager.showFullScreenAdsIfRequired(AdsSupportActivity.this);
        }
    };

    protected final void postShowFullScreenAdsIfNeeded() {
        mHandler.removeCallbacks(mShowAds);
        if (!Premium.isPremiumUser(this)) {
            mHandler.postDelayed(mShowAds, AdsManager.DELAY_TIME);
        }
    }

    protected final void postShowFullScreenAdsAfter(long millisTime) {
        mHandler.removeCallbacks(mShowAds);
        if (!Premium.isPremiumUser(this)) {
            mHandler.postDelayed(mShowAds, millisTime);
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mShowAds);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    public boolean isActivityVisible() {
        return isVisible;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
