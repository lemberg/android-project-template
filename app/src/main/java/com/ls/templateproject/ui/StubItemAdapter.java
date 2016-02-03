package com.ls.templateproject.ui;

import com.ls.http.base.ResponseData;
import com.ls.templateproject.R;
import com.ls.templateproject.model.data.vo.StubItemVO;
import com.ls.templateproject.model.plain.Model;
import com.ls.templateproject.model.plain.managers.BaseItemManager;
import com.ls.util.image.LSImageView;

import android.content.Context;
import android.support.annotation.NonNull;
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
public final class StubItemAdapter extends BaseAdapter {

    private final static int PRE_LOADING_PAGE_OFFSET = 4;

    private final List<StubItemVO> mItems = new ArrayList<>();
    private final LayoutInflater mInflater;

    private int mPagesLoaded;
    private boolean mCanLoadMore;
    private boolean mOnLoad;

    private final BaseItemManager.OnDataFetchCompleteListener<List<StubItemVO>, String> listener
            = new BaseItemManager.OnDataFetchCompleteListener<List<StubItemVO>, String>() {

        @Override
        public void onDataFetchComplete(List<StubItemVO> result, ResponseData data,
                String requestTag) {
            mOnLoad = false;
            applyDataUpdate(result);
        }

        @Override
        public void onDataFetchFailed(List<StubItemVO> result, ResponseData data,
                String requestTag) {
            mOnLoad = false;
            applyDataUpdate(result);
        }
    };

    private void applyDataUpdate(List<StubItemVO> result) {
        if (result != null && !result.isEmpty()) {
            mCanLoadMore = true;
            mItems.addAll(result);
            mPagesLoaded++;
            notifyDataSetChanged();
        } else {
            mCanLoadMore = false;
        }
    }

    public StubItemAdapter(@NonNull final Context theContext) {
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
        if (position < mItems.size()) {
            return mItems.get(position);
        } else {
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
            convertView = mInflater.inflate(R.layout.list_item_stub_item, parent, false);
        }

        final StubItemVO item = getItem(position);

        final LSImageView imageView = (LSImageView) convertView.findViewById(R.id.image_view);
        imageView.setImageUri(item.getImageURL());

        final TextView text = (TextView) convertView.findViewById(R.id.textView);
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

    public void enableDataLoad() {
        Model.instance().getStubManager().addDataFetchCompleteListener(listener);
        loadNextPage();
    }

    public void disableDataLoad() {
        Model.instance().getStubManager().removeDataFetchCompleteListener(listener);
    }
}
