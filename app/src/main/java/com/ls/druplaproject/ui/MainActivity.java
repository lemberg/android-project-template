package com.ls.druplaproject.ui;

import com.ls.druplaproject.R;
import com.ls.druplaproject.model.Model;
import com.ls.druplaproject.model.managers.StubItemManager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    private StubItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter != null)
        {
            adapter.disavleDataLoad();
        }
    }

    private void initViews()
    {
        adapter = new StubItemAdapter(this);
        ((ListView)this.findViewById(R.id.list_view)).setAdapter(adapter);
        adapter.anableDataLoad();
    }

}
