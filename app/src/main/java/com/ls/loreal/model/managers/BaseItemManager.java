package com.ls.loreal.model.managers;

import com.ls.drupal.AbstractBaseDrupalEntity;
import com.ls.drupal.DrupalClient;
import com.ls.http.base.ResponseData;
import com.ls.util.ObserverHolder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.net.HttpURLConnection;

/**
 * Created on 22.05.2015.
 */
public abstract class BaseItemManager<ClassToManage extends AbstractBaseDrupalEntity> {

    private DrupalClient client;
    private ObserverHolder<OnDataFetchCompleteListener<ClassToManage>> listeners;

    protected abstract ClassToManage getEntityToFetch(DrupalClient client,Bundle requestParams);
    protected abstract String getEntityRequestTag(Bundle params);
    protected abstract boolean storeResponse(ClassToManage response,Object tag);

    private AbstractBaseDrupalEntity.OnEntityRequestListener updateResponceListener = new AbstractBaseDrupalEntity.OnEntityRequestListener() {
        @Override
        public void onRequestCompleted(AbstractBaseDrupalEntity entity, Object tag, ResponseData data) {
            applyDataUpdateComplete((ClassToManage) entity,tag, data);
        }

        @Override
        public void onRequestFailed(AbstractBaseDrupalEntity entity, Object tag, ResponseData data) {
            applyDataUpdateFailed((ClassToManage)entity,tag,data);
        }

        @Override
        public void onRequestCanceled(AbstractBaseDrupalEntity entity, Object tag) {

        }
    };

    protected BaseItemManager(DrupalClient client)
    {
        this.client = client;
        this.listeners = new ObserverHolder<OnDataFetchCompleteListener<ClassToManage>>();
    }

    public Object fetchData(Bundle requestParams)
    {
        ClassToManage request = getEntityToFetch(this.client,requestParams);
        Object tag = getEntityRequestTag(requestParams);
        request.pullFromServer(false,tag,updateResponceListener);
        return tag;
    }

    public void addDataFetchCompleteListener(OnDataFetchCompleteListener<ClassToManage> listener)
    {
        this.listeners.registerObserver(listener);
    }

    public void removeDataFetchCompleteListener(OnDataFetchCompleteListener<ClassToManage> listener)
    {
        this.listeners.unregisterObserver(listener);
    }


    protected void applyDataUpdateComplete(ClassToManage entity, Object tag,ResponseData data)
    {
        if(data.getStatusCode() == HttpURLConnection.HTTP_OK) {
            if(storeResponse(entity, tag))
            {
                notifyListeners(entity, data,tag,true);
                return;
            }
        }
        notifyListeners(null, data,tag,false);
    }

    protected void applyDataUpdateFailed(ClassToManage entity, Object tag,ResponseData data)
    {
        notifyListeners(null, data, tag, false);
    }

    private void notifyListeners(final ClassToManage result,final ResponseData data,final Object tag, final boolean success)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                listeners.notifyAllObservers(new ObserverHolder.ObserverNotifier<OnDataFetchCompleteListener<ClassToManage>>() {
                    @Override
                    public void onNotify(OnDataFetchCompleteListener observer) {
                        if(success)
                        {
                            observer.onDataFetchComplete(result,data,tag);
                        }else{
                            observer.onDataFetchFailed(result, data, tag);
                        }
                    }
                });
            }
        });
    }

    public static interface OnDataFetchCompleteListener<T>
    {
        void onDataFetchComplete(T result, ResponseData data, Object requestTag);
        void onDataFetchFailed(T result, ResponseData data, Object requestTag);
    }

}
