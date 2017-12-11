/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.service.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by yoxjames on 10/8/17.
 */

public class ImageUtil
{
    public static Bitmap scaleBitmap(String path, int destWidth, int destHeight)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;

        if (srcHeight > destHeight || srcWidth > destWidth)
            if (srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / destHeight);
            else
                inSampleSize = Math.round(srcWidth / destWidth);

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap scaleBitmap(String path, Activity activity)
    {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return scaleBitmap(path, size.x, size.y);
    }
}
