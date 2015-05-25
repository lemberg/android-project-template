package com.ls.druplaproject.ui;

import com.ls.http.base.ResponseData;
import com.ls.druplaproject.R;
import com.ls.druplaproject.model.Model;
import com.ls.druplaproject.model.data.vo.StubItemVO;
import com.ls.druplaproject.model.managers.BaseItemManager;
import com.ls.druplaproject.model.managers.StubItemManager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private BaseItemManager.OnDataFetchCompleteListener listener = new BaseItemManager.OnDataFetchCompleteListener<List<StubItemVO>>()
    {

        @Override
        public void onDataFetchComplete(List<StubItemVO> result, ResponseData data, Object requestTag) {
            //TODO: display data
        }

        @Override
        public void onDataFetchFailed(List<StubItemVO> result, ResponseData data, Object requestTag) {
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
