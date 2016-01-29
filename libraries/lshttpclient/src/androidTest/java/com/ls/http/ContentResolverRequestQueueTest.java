package com.ls.http;

import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;
import android.test.ProviderTestCase2;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;

public class ContentResolverRequestQueueTest extends ProviderTestCase2<FileProvider> {

    private static final String TEST_FILE_NAME = "test";

    private static final byte[] TEST_DATA = new byte[]{
            1, 2, 3, 4, 5
    };

    public ContentResolverRequestQueueTest() {
        super(FileProvider.class, FileProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create test file
        final FileOutputStream os = getContext()
                .openFileOutput(TEST_FILE_NAME, Context.MODE_PRIVATE);
        os.write(TEST_DATA);
        os.close();

        // Replace IsolatedContext with the normal one
        try {
            final Field f = ContentProvider.class.getDeclaredField("mContext");
            f.setAccessible(true);
            f.set(getProvider(), getContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // Remove test file
        getContext().deleteFile(TEST_FILE_NAME);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void testContentUri() throws Exception {
        final LSClient client = new LSClient(
                new MockContentResolverContextWrapper(getContext(), getMockContentResolver()));

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(FileProvider.getUriForFile(TEST_FILE_NAME).toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TEST_DATA, (byte[]) data.getData()));
    }

    public void testFileUri() throws Exception {
        final LSClient client = new LSClient(getContext());

        final URI uri = getContext().getFileStreamPath(TEST_FILE_NAME).toURI();

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TEST_DATA, (byte[]) data.getData()));
    }
}
