package com.ls.templateproject.ui;

import com.ls.templateproject.R;
import com.ls.templateproject.ui.base.BaseActivity;

import android.os.Bundle;
import android.widget.AbsListView;

public final class MainActivity extends BaseActivity {

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
        if (adapter != null) {
            adapter.disableDataLoad();
        }
    }

    private void initViews() {
        adapter = new StubItemAdapter(this);
        ((AbsListView) this.findViewById(R.id.list_view)).setAdapter(adapter);
        adapter.enableDataLoad();
    }

}
