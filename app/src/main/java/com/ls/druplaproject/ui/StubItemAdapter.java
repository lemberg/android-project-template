package com.ls.druplaproject.ui;

import com.ls.druplaproject.R;
import com.ls.http.base.ResponseData;
import com.ls.druplaproject.model.Model;
import com.ls.druplaproject.model.data.vo.StubItemVO;
import com.ls.druplaproject.model.managers.BaseItemManager;
import com.ls.util.image.DrupalImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 25.05.2015.
 */
public class StubItemAdapter extends BaseAdapter {
    private final static int PRE_LOADING_PAGE_OFFSET = 4;

    private List<StubItemVO> mItems;
    private int mPagesLoaded;
    private boolean mCanLoadMore;
    private boolean mOnLoad;
    private final LayoutInflater mInflater;

    private BaseItemManager.OnDataFetchCompleteListener listener = new BaseItemManager.OnDataFetchCompleteListener<List<StubItemVO>>()
    {

        @Override
        public void onDataFetchComplete(List<StubItemVO> result, ResponseData data, Object requestTag) {
            mCanLoadMore = true;
        }

        @Override
        public void onDataFetchFailed(List<StubItemVO> result, ResponseData data, Object requestTag) {
            mCanLoadMore = false;
        }
    };

    public StubItemAdapter(Context theContext)
    {
        mItems = new ArrayList<>();
        mCanLoadMore = true;
        mOnLoad = false;
        mPagesLoaded = 0;
        mInflater = LayoutInflater.from(theContext);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public StubItemVO getItem(int position) {
        if(position < mItems.size())
        {
            return mItems.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position > mItems.size() - PRE_LOADING_PAGE_OFFSET) {
            loadNextPage();
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_stub_item, null);
        }

        StubItemVO item = getItem(position);

        DrupalImageView imageView = (DrupalImageView)convertView.findViewById(R.id.image_view);
        imageView.setImageWithURL(item.getImageURL());

        TextView text = (TextView)convertView.findViewById(R.id.textView);
        text.setText(item.getDescription());
        return convertView;
    }

    private void loadNextPage() {
        if (mCanLoadMore && !mOnLoad) {
            mOnLoad = true;
            String pageId = Integer.toString(mPagesLoaded);
            Model.instance().getStubManager().pullPage(pageId);
        }
    }
}
