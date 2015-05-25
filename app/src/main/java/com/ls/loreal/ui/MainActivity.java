package com.ls.loreal.ui;

import com.ls.http.base.ResponseData;
import com.ls.loreal.R;
import com.ls.loreal.model.Model;
import com.ls.loreal.model.data.vo.StubItemVO;
import com.ls.loreal.model.managers.BaseItemManager;
import com.ls.loreal.model.managers.StubItemManager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class MainActivity extends ActionBarActivity {

    private BaseItemManager.OnDataFetchCompleteListener listener = new BaseItemManager.OnDataFetchCompleteListener<StubItemVO>()
    {

        @Override
        public void onDataFetchComplete(StubItemVO result, ResponseData data, Object requestTag) {
            //TODO: display data
        }

        @Override
        public void onDataFetchFailed(StubItemVO result, ResponseData data, Object requestTag) {
            //TODO: display loading error
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataLoad();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Model.instance().getStubManager().removeDataFetchCompleteListener(listener);
    }

    private void initDataLoad()
    {
        StubItemManager stubManager =  Model.instance().getStubManager();
        stubManager.addDataFetchCompleteListener(listener);
        stubManager.pullPage("some_page");
    }

}
