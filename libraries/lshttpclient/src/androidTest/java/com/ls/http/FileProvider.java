package com.ls.http;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * {@link ContentProvider} for sharing files
 */
public final class FileProvider extends ContentProvider {

    public static final String AUTHORITY = "com.ls.test.file";
    private static final String CONTENT_URI = "content://" + AUTHORITY + '/';

    private static final String[] COLUMNS = {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};

    /**
     * The default FileProvider implementation does not need to be initialized. If you want to
     * override this method, you must provide your own subclass of FileProvider.
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    private File getFileForUri(final Uri uri) {
        final Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("No Context attached");
        }
        final String name = uri.getLastPathSegment();
        return new File(context.getFilesDir(), name);
    }

    public static Uri getUriForFile(@NonNull final String fileName) {
        return Uri.parse(CONTENT_URI.concat(fileName));
    }

    /**
     * Use a content URI returned by
     * {@link #getUriForFile(String) getUriForFile()} to get information user a
     * file
     * managed by the FileProvider.
     * FileProvider reports the column names defined in {@link OpenableColumns}:
     * <ul>
     * <li>{@link OpenableColumns#DISPLAY_NAME}</li>
     * <li>{@link OpenableColumns#SIZE}</li>
     * </ul>
     * For more information, see
     * {@link ContentProvider#query(Uri, String[], String, String[], String)
     * ContentProvider.query()}.
     *
     * @param uri           A content URI returned by {@link #getUriForFile}.
     * @param projection    The list of columns to put into the {@link Cursor}. If null all columns
     *                      are
     *                      included.
     * @param selection     Selection criteria to apply. If null then all data that matches the
     *                      content
     *                      URI is returned.
     * @param selectionArgs An array of {@link String}, containing arguments to bind to
     *                      the <i>selection</i> parameter. The <i>query</i> method scans
     *                      <i>selection</i> from left to
     *                      right and iterates through <i>selectionArgs</i>, replacing the current
     *                      "?" character in
     *                      <i>selection</i> with the value at the current position in
     *                      <i>selectionArgs</i>. The
     *                      values are bound to <i>selection</i> as {@link String}
     *                      values.
     * @param sortOrder     A {@link String} containing the column name(s) on which to
     *                      sort
     *                      the resulting {@link Cursor}.
     * @return A {@link Cursor} containing the results of the query.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs,
            String sortOrder) {
        // ContentProvider has already checked granted permissions
        final File file = getFileForUri(uri);

        if (projection == null) {
            projection = COLUMNS;
        }

        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int i = 0;
        for (String col : projection) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                cols[i] = OpenableColumns.DISPLAY_NAME;
                values[i++] = file.getName();
            } else if (OpenableColumns.SIZE.equals(col)) {
                cols[i] = OpenableColumns.SIZE;
                values[i++] = file.length();
            }
        }

        cols = copyOf(cols, i);
        values = copyOf(values, i);

        final MatrixCursor cursor = new MatrixCursor(cols, 1);
        cursor.addRow(values);
        return cursor;
    }

    /**
     * Returns the MIME type of a content URI returned by
     * {@link #getUriForFile(String) getUriForFile()}.
     *
     * @param uri A content URI returned by
     *            {@link #getUriForFile(String) getUriForFile()}.
     * @return If the associated file has an extension, the MIME type associated with that
     * extension; otherwise <code>application/octet-stream</code>.
     */
    @Override
    public String getType(@NonNull final Uri uri) {
        // ContentProvider has already checked granted permissions
        final File file = getFileForUri(uri);

        final int lastDot = file.getName().lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = file.getName().substring(lastDot + 1);
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }

        return "application/octet-stream";
    }

    /**
     * By default, this method throws an {@link UnsupportedOperationException}. You must
     * subclass FileProvider if you want to provide different functionality.
     */
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    /**
     * By default, this method throws an {@link UnsupportedOperationException}. You must
     * subclass FileProvider if you want to provide different functionality.
     */
    @Override
    public int update(@NonNull final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    /**
     * Deletes the file associated with the specified content URI, as
     * returned by {@link #getUriForFile(String) getUriForFile()}. Notice that this
     * method does <b>not</b> throw an {@link java.io.IOException}; you must check its return
     * value.
     *
     * @param uri           A content URI for a file, as returned by
     *                      {@link #getUriForFile(String) getUriForFile()}.
     * @param selection     Ignored. Set to {@code null}.
     * @param selectionArgs Ignored. Set to {@code null}.
     * @return 1 if the delete succeeds; otherwise, 0.
     */
    @Override
    public int delete(@NonNull final Uri uri, final String selection,
            final String[] selectionArgs) {
        // ContentProvider has already checked granted permissions
        final File file = getFileForUri(uri);
        return file.delete() ? 1 : 0;
    }

    /**
     * By default, FileProvider automatically returns the
     * {@link ParcelFileDescriptor} for a file associated with a <code>content://</code>
     * {@link Uri}. To get the {@link ParcelFileDescriptor}, call
     * {@link android.content.ContentResolver#openFileDescriptor(Uri, String)
     * ContentResolver.openFileDescriptor}.
     *
     * To override this method, you must provide your own subclass of FileProvider.
     *
     * @param uri  A content URI associated with a file, as returned by
     *             {@link #getUriForFile(String) getUriForFile()}.
     * @param mode Access mode for the file. May be "r" for read-only access, "rw" for read and
     *             write access, or "rwt" for read and write access that truncates any existing
     *             file.
     * @return A new {@link ParcelFileDescriptor} with which you can access the file.
     */
    @Override
    public ParcelFileDescriptor openFile(@NonNull final Uri uri, @NonNull final String mode)
            throws FileNotFoundException {
        // ContentProvider has already checked granted permissions
        final File file = getFileForUri(uri);
        final int fileMode = modeToMode(mode);
        return ParcelFileDescriptor.open(file, fileMode);
    }

    private static String[] copyOf(String[] original, int newLength) {
        final String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static Object[] copyOf(Object[] original, int newLength) {
        final Object[] result = new Object[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    /**
     * Copied from ContentResolver.java
     */
    private static int modeToMode(String mode) {
        int modeBits;
        //noinspection IfCanBeSwitch
        if ("r".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_ONLY;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return modeBits;
    }
}
