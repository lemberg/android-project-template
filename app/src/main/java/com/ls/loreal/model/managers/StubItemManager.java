package com.ls.loreal.model.managers;

import com.ls.drupal.DrupalClient;
import com.ls.loreal.model.data.dao.StubItemDAO;
import com.ls.loreal.model.responses.StubItemResponse;

import android.os.Bundle;

/**
 * Created on 22.05.2015.
 */
public class StubItemManager extends BaseItemManager<StubItemResponse>{

    private final static String PAGE_ID_KEY = "page_ID";

    protected StubItemManager(DrupalClient client) {
        super(client);
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
    protected StubItemResponse getEntityToFetch(DrupalClient client, Bundle requestParams) {

        StubItemResponse response =  new StubItemResponse(client);
        response.setPageId(getIdFromBundle(requestParams));
        return response;
    }

    @Override
    protected String getEntityRequestTag(Bundle params) {
        return "stub_item_id"+ getIdFromBundle(params);
    }

    @Override
    protected boolean storeResponse(StubItemResponse response, Object tag) {
        StubItemDAO dao = new StubItemDAO();
        //TODO: store data with DAO
        return true;
    }

    private Bundle getBundleForId(String pageId)
    {
        Bundle result = new Bundle();
        result.putString(PAGE_ID_KEY,pageId);
        return result;
    }

    private String getIdFromBundle(Bundle bundle)
    {
        return bundle.getString(PAGE_ID_KEY);
    }
}
