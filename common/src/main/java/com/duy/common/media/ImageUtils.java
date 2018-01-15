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

package com.duy.common.media;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;

import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.MediaColumns;

/**
 * Created by Duy on 28-Dec-17.
 */

public class ImageUtils {
    /**
     * store image file on media store provider
     */
    public static boolean addImageToGallery(final String filePath, final Context context) {
        try {
            ContentValues values = new ContentValues();

            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaColumns.DATA, filePath);

            context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * store image file on media store provider
     */
    public static boolean addImageToGallery(File file, Context context) {
        return addImageToGallery(file.getPath(), context);
    }
}
