package com.ls.templateproject.model.plain.managers;

import com.ls.templateproject.model.data.dao.StubItemDAO;
import com.ls.templateproject.model.data.vo.StubItemVO;
import com.ls.templateproject.model.plain.requests.StubItemRequestBuilder;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22.05.2015.
 */
public class StubItemManager extends SynchrondizedDatabaseManager<List<StubItemVO>,Bundle,String>{

    private final static String PAGE_ID_KEY = "page_ID";
    private final static String TAG_PREFIX = "stub_item_id:";
    private StubItemDAO dao ;

    public StubItemManager(LSClient client) {
        super(client);
        dao = new StubItemDAO();
    }

    /**
     * @param id
     * @return tag, used to identify request
     */
    public Object pullPage(String id)
    {
        return fetchData(getBundleForId(id));
    }

    @Override
    protected BaseRequest getFetchRequest(LSClient client, Bundle requestParams) {
        String requestId = getIdFromBundle(requestParams);

        StubItemRequestBuilder builder =  new StubItemRequestBuilder();
        builder.setPageId(requestId);
        return builder.create();
    }

    @Override
    protected String getEntityRequestTag(Bundle params) {
        return getTagFromBundle(params);
    }

    @Override
    protected List<StubItemVO> readResponseFromRequest(BaseRequest request,ResponseData data, String tag) {
        List<StubItemVO> items = (List<StubItemVO>)data.getData();
        if(request != null && !items.isEmpty()) {
            for(StubItemVO item:items)
            {
                item.setPageId(getReqeustIdFromTag(tag));
            }
            return items;
        }else{
            return null;
        }
    }

    @Override
    protected boolean synchronizedStoreResponse(List<StubItemVO> response, String tag) {
        List<StubItemVO> items = new ArrayList<>(response);
        String requestId = getReqeustIdFromTag(tag);
        dao.removeItemsForPageId(requestId);
        dao.saveData(items);
        return true;
    }

    @Override
    protected List<StubItemVO> synchronizeddRetoreResponse(String tag) {
        String requestId = getReqeustIdFromTag(tag);
        return  dao.readItemsForPageId(requestId);
    }

    private Bundle getBundleForId(String pageId)
    {
        Bundle result = new Bundle();
        result.putString(PAGE_ID_KEY, pageId);
        return result;
    }

    private String getIdFromBundle(Bundle bundle)
    {
        return bundle.getString(PAGE_ID_KEY);
    }

    private String getTagFromBundle(Bundle bundle)
    {
        return TAG_PREFIX + getIdFromBundle(bundle);
    }

    private String getReqeustIdFromTag(String tag)
    {
        return tag.substring(TAG_PREFIX.length());
    }
}
