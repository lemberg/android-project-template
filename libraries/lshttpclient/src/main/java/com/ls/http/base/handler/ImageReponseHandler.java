/*
 * The MIT License (MIT)
 *  Copyright (c) 2014 Lemberg Solutions Limited
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.ls.http.base.handler;

import com.android.volley.NetworkResponse;
import com.ls.http.base.BaseByteResponseHandler;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created on 22.04.2015.
 * Returns image Drawable object as a result
 */
public class ImageReponseHandler extends BaseByteResponseHandler {

    @Override
    protected String getAcceptValueType() {
        return "image/*";
    }

    protected Object itemFromResponseWithSpecifier(NetworkResponse response, Object theSpecifier) {
        Object result = null;
        if (response != null && response.data != null && response.data.length > 0) {

            Bitmap imageBitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            Drawable imageDrawable = new BitmapDrawable(Resources.getSystem(), imageBitmap);

            result = imageDrawable;
        }
        return result;
    }
}
