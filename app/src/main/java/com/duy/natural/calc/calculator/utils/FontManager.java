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

package com.duy.natural.calc.calculator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.io.IOException;
import java.util.Hashtable;


/**
 * Created by Duy on 18-Mar-17.
 */

public class FontManager {
    private static final String PATH_TO_FONT = "fonts/";
    private static final String TAG = "FontManager";
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();
    private static String[] FREE_FONT_NAMES;

    public synchronized static Typeface get(Context c, String assetPath) throws IOException {
        if (assetPath.equalsIgnoreCase("monospace")) {
            return Typeface.MONOSPACE;
        }
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    throw new IOException("Could not get typeface '" +
                            assetPath + "' because " + e.getMessage());
                }
            }
            return cache.get(assetPath);
        }
    }

    public synchronized static Typeface getFontFromAsset(Context context, String name) {
        if (name == null || name.isEmpty()) {
            return Typeface.MONOSPACE;
        }
        try {
            return get(context, PATH_TO_FONT + name);
        } catch (Exception e) {//can not find font
        }
        return Typeface.MONOSPACE;
    }


    public static void setDefaultFont(Context context, TextView textView) {
        if (textView.getTypeface().getStyle() == Typeface.BOLD) {
            textView.setTypeface(FontManager.getFontFromAsset(context, "Roboto-Regular.ttf"));
        } else {
            textView.setTypeface(FontManager.getFontFromAsset(context, "Roboto-Light.ttf"));
        }
    }
}
