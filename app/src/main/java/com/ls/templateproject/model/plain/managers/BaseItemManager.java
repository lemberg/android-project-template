package com.ls.templateproject.model.plain.managers;

import com.ls.http.base.BaseRequest;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;
import com.ls.util.ObserverHolder;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.HttpURLConnection;

/**
 * Created on 22.05.2015.
 */
public abstract class BaseItemManager<ClassToManage, ParametersClass,TagClass> {

    private LSClient client;
    private ObserverHolder<OnDataFetchCompleteListener<ClassToManage,TagClass>> listeners;

    protected abstract BaseRequest getFetchRequest(LSClient client, ParametersClass requestParams);

    protected abstract TagClass getEntityRequestTag(ParametersClass params);

    protected abstract ClassToManage readResponseFromRequest(BaseRequest request,ResponseData data, TagClass tag);

    protected abstract boolean storeResponse(ClassToManage response, TagClass tag);

    protected abstract ClassToManage restoreResponse(TagClass tag);

    private LSClient.OnResponseListener updateResponceListener = new LSClient.OnResponseListener() {
        @Override
        public void onResponseReceived(@NonNull final BaseRequest request,
                @NonNull final ResponseData data,
                @Nullable final Object tag)
        {
            applyDataUpdateComplete(request, (TagClass) tag, data);
        }

        @Override
        public void onError(@NonNull final BaseRequest request,
                @Nullable final ResponseData data,
                @Nullable final Object tag)
        {
            applyDataUpdateFailed(request,(TagClass) tag, data);
        }

        @Override
        public void onCancel(@NonNull final BaseRequest request,
                @Nullable final Object tag)
        {

        }
    };

    protected BaseItemManager(LSClient client) {
        this.client = client;
        this.listeners = new ObserverHolder<>();
    }

    public TagClass fetchData(ParametersClass requestParams) {
        BaseRequest request = getFetchRequest(this.client, requestParams);
        TagClass tag = getEntityRequestTag(requestParams);
        this.client.performRequest(request,tag,updateResponceListener,false);
        return tag;
    }

    public void addDataFetchCompleteListener(OnDataFetchCompleteListener<ClassToManage,TagClass> listener) {
        this.listeners.registerObserver(listener);
    }

    public void removeDataFetchCompleteListener(OnDataFetchCompleteListener<ClassToManage,TagClass> listener) {
        this.listeners.unregisterObserver(listener);
    }


    protected void applyDataUpdateComplete(final BaseRequest entity, final TagClass tag, final ResponseData data) {
        if (data.getStatusCode() == HttpURLConnection.HTTP_OK) {
            final ClassToManage response = readResponseFromRequest(entity,data, tag);
            if (response != null) {
                notifyListeners(response, data, tag, true);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        storeResponse(response, tag);
                        return null;
                    }
                }.execute();
                return;
            }
        }
        restoreData(data, tag, data.getStatusCode() == HttpURLConnection.HTTP_NOT_MODIFIED);
    }

    protected void applyDataUpdateFailed(BaseRequest entity, TagClass tag, ResponseData data) {
        restoreData(data, tag,false);
    }

    private void restoreData(final ResponseData data, final TagClass tag,final boolean successful) {
        new AsyncTask<Void,Void,ClassToManage>()
        {

            @Override
            protected ClassToManage doInBackground(Void... params) {
                return restoreResponse(tag);
            }

            @Override
            protected void onPostExecute(ClassToManage classToManage) {
                notifyListeners(classToManage, data, tag, successful);
            }
        }.execute();
    }

    private void notifyListeners(final ClassToManage result, final ResponseData data, final TagClass tag, final boolean success) {

        listeners.notifyAllObservers(new ObserverHolder.ObserverNotifier<OnDataFetchCompleteListener<ClassToManage,TagClass>>() {
            @Override
            public void onNotify(OnDataFetchCompleteListener observer) {
                if (success) {
                    observer.onDataFetchComplete(result, data, tag);
                } else {
                    observer.onDataFetchFailed(result, data, tag);
                }
            }
        });
    }

    public interface OnDataFetchCompleteListener<ResultClass,TagClass> {

        void onDataFetchComplete(ResultClass result, ResponseData data, TagClass requestTag);

        void onDataFetchFailed(ResultClass result, ResponseData data, TagClass requestTag);
    }

}
