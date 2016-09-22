package com.ls.http;

import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;
import com.ls.httpclient.test.R;
import com.ls.util.UriFactory;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;

public final class ContentResolverRequestQueueTest extends ProviderTestCase2<FileProvider> {

    private static final String TEST_FILE_NAME = "test";

    public ContentResolverRequestQueueTest() {
        super(FileProvider.class, FileProvider.AUTHORITY);
    }

    @Override
    public MockContentResolver getMockContentResolver() {
        return super.getMockContentResolver();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create test file
        final FileOutputStream os = getContext()
                .openFileOutput(TEST_FILE_NAME, Context.MODE_PRIVATE);
        os.write(TestFiles.TEST_DATA);
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
        final LSClient client = new LSClient.Builder(
                new MockContentResolverContextWrapper(getContext(), getMockContentResolver())).build();

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(FileProvider.getUriForFile(TEST_FILE_NAME).toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TestFiles.TEST_DATA, (byte[]) data.getData()));
    }

    public void testFileUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final URI uri = getContext().getFileStreamPath(TEST_FILE_NAME).toURI();

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TestFiles.TEST_DATA, (byte[]) data.getData()));
    }

    public void testAssetFileUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final Uri uri = UriFactory.makeAssetUri("test");

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TestFiles.TEST_DATA, (byte[]) data.getData()));
    }

    public void testAssetFolderFileUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final Uri uri = UriFactory.makeAssetUri("testFolder/test");

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TestFiles.TEST_DATA, (byte[]) data.getData()));
    }

    public void testDrawableResourceUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final Uri uri = UriFactory.makeDrawableResourceUri(getContext().getResources(), R.drawable.ic_hexagon_outline_black_24dp);

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        assertTrue(Arrays.equals(TestFiles.IC_HEXAGON_OUTLINE_BLACK_24DP, (byte[]) data.getData()));
    }

    public void testXmlResourceUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final Uri uri = UriFactory.makeXmlResourceUri(getContext().getResources(), R.xml.test);

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        android.util.Log.e("data", Arrays.toString((byte[]) data.getData()));
        assertTrue(Arrays.equals(TestFiles.XML_TEST, (byte[]) data.getData()));
    }

    public void testRawResourceUri() throws Exception {
        final LSClient client = new LSClient.Builder(getContext()).build();

        final Uri uri = UriFactory.makeRawResourceUri(getContext().getResources(), R.raw.test);

        final BaseRequest request = new BaseRequestBuilder()
                .setRequestURL(uri.toString())
                .setRequestMethod(BaseRequest.RequestMethod.GET)
                .setRequestFormat(BaseRequest.RequestFormat.TEXT)
                .setResponseFormat(BaseRequest.ResponseFormat.BYTE)
                .create();

        final ResponseData data = client.performRequest(request, true);
        android.util.Log.e("data", Arrays.toString((byte[]) data.getData()));
        assertTrue(Arrays.equals(TestFiles.TEST_DATA, (byte[]) data.getData()));
    }
}
