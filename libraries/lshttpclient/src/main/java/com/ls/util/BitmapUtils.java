package com.ls.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by lemberg on 19.08.2014.
 */
public final class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    private BitmapUtils() {
    }

    public static Bitmap loadSquareBitmap(Uri selectedImageURI, Context theContext, int width,
            int height) {
        Bitmap srcBitmap = loadBitmap(selectedImageURI, theContext, width, height);
        if (srcBitmap != null) {
            return getSquareBitmap(srcBitmap);
        } else {
            return null;
        }
    }

    @Nullable
    public static Bitmap loadBitmap(Uri selectedImageURI, Context theContext, int maxWidth,
            int maxHeight) {
        InputStream imageStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Don't resize for 0
            if (maxWidth != 0 && maxHeight != 0) {
                imageStream = getStreamFromUri(selectedImageURI, theContext);
                if (imageStream == null) {
                    // Unsupported Uri
                    return null;
                }
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(imageStream, null, options);
                imageStream.close();

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
            }

            imageStream = getStreamFromUri(selectedImageURI, theContext);
            if (imageStream == null) {
                // Unsupported Uri
                return null;
            }
            Bitmap srcBitmap = BitmapFactory.decodeStream(imageStream, null, options);

            float angle = getImageRotation(theContext, selectedImageURI);

            final Matrix mat = new Matrix();
            mat.postRotate(angle);

            return Bitmap
                    .createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), mat,
                            true);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Nullable
    private static InputStream getStreamFromUri(Uri selectedImageURI, Context theContext)
            throws IOException {
        switch (selectedImageURI.getScheme()) {
            case "content":
                return theContext.getContentResolver().openInputStream(selectedImageURI);

            case "file":
                return new FileInputStream(new File(URI.create(selectedImageURI.toString())));

            case "http":
                // Fall through
            case "https":
                final URL url = new URL(selectedImageURI.toString());
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                return connection.getInputStream();

            default:
                Log.w(TAG, "getStreamFromUri(): unsupported Uri scheme: " + selectedImageURI
                        .getScheme());
                return null;
        }
    }

    private static float getImageRotation(Context context, Uri selectedImageURI) {
        try {
            final String scheme = selectedImageURI.getScheme();
            if (scheme.equals("content")) {
                //From the media gallery
                final String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
                final Cursor c = context.getContentResolver()
                        .query(selectedImageURI, projection, null, null, null);
                if (c != null) {
                    try {
                        if (c.moveToFirst()) {
                            return c.getInt(0);
                        }
                    } finally {
                        c.close();
                    }
                }
            } else if (scheme.equals("file")) {
                int rotation = 0;
                //From a file saved by the camera
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    ExifInterface exif = new ExifInterface(selectedImageURI.getPath());
                    rotation = (int) exifOrientationToDegrees(
                            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL));
                }
                return rotation;
            }
            return 0;

        } catch (IOException e) {
            L.e("Error checking exif" + e);
            return 0;
        }
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        float angle;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                angle = 0;
                break;
        }
        return angle;
    }

    private static Bitmap getSquareBitmap(Bitmap srcBitmap) {
        int size = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());
        int paddingX = (srcBitmap.getWidth() - size) / 2;
        int paddingY = (srcBitmap.getHeight() - size) / 2;

        return Bitmap.createBitmap(srcBitmap, paddingX, paddingY, size, size);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap createScaledToFillBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();

        float widthScaleF = (float) width / (float) reqWidth;
        float heightScaleF = (float) height / (float) reqHeight;
        if (widthScaleF < heightScaleF) {
            return Bitmap.createScaledBitmap(bitmap, reqWidth, (int) (height / widthScaleF), true);
        } else {
            return Bitmap.createScaledBitmap(bitmap, (int) (width / heightScaleF), reqHeight, true);
        }
    }

    public static Bitmap createScaledToCenterInsideBitmap(Bitmap bitmap, int reqWidth,
            int reqHeight) {
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();

        float widthScaleF = (float) width / (float) reqWidth;
        float heightScaleF = (float) height / (float) reqHeight;
        if (widthScaleF > heightScaleF) {
            return Bitmap.createScaledBitmap(bitmap, reqWidth, (int) (height / widthScaleF), true);
        } else {
            return Bitmap.createScaledBitmap(bitmap, (int) (width / heightScaleF), reqHeight, true);
        }
    }

    public static Bitmap joinBitmaps(Bitmap first, Bitmap second) {
        Bitmap result = Bitmap
                .createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint bitmapPaint = new Paint();
        if (second.getWidth() != first.getWidth() && second.getHeight() != first.getHeight()) {
            second = createScaledToFillBitmap(second, first.getWidth(), first.getHeight());
        }

        canvas.drawBitmap(first, 0, 0, bitmapPaint);
        canvas.drawBitmap(second, 0, 0, bitmapPaint);

        return result;
    }

    public static Bitmap addLabelToBitmap(Bitmap src, String label) {
        float densityFactor = Resources.getSystem().getDisplayMetrics().density;
        final float textPadding = src.getWidth() * 0.05f;

        Bitmap result = Bitmap
                .createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(12 * densityFactor);
        textPaint.setColor(Color.WHITE);
        textPaint.setStrokeWidth(2 * densityFactor);
        textPaint.setShadowLayer(1 * densityFactor, 0, 0, Color.BLACK);

        float textWidth = textPaint.measureText(label);

        float scaleFactor = (src.getWidth() - textPadding * 2) / textWidth;

        canvas.drawBitmap(src, 0, 0, textPaint);

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        float textPosX = (src.getWidth() / scaleFactor - textWidth) / 2;
        float textPosY = (src.getHeight() - textPadding) / scaleFactor;

        canvas.drawText(label, textPosX, textPosY, textPaint);
        canvas.restore();
        return result;
    }

    public static InputStream getBitmapJpegStream(Bitmap theBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        theBitmap.compress(Bitmap.CompressFormat.JPEG, 95, baos);

        byte[] data = baos.toByteArray();
        return new ByteArrayInputStream(data);
    }

    public static InputStream getBitmapPngStream(Bitmap theBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        theBitmap.compress(Bitmap.CompressFormat.PNG, 95, baos);

        byte[] data = baos.toByteArray();
        return new ByteArrayInputStream(data);
    }
}
