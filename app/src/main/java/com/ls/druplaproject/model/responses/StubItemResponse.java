package com.ls.druplaproject.model.responses;

import com.ls.drupal.AbstractDrupalArrayEntity;
import com.ls.drupal.DrupalClient;
import com.ls.http.base.BaseRequest;
import com.ls.druplaproject.model.data.vo.StubItemVO;

import java.util.Map;

/**
 * Created on 22.05.2015.
 */
public class StubItemResponse extends AbstractDrupalArrayEntity<StubItemVO> {

    private final static int PAGE_SIZE = 5;

    private String pageId;

    public StubItemResponse(DrupalClient client) {
        super(client, PAGE_SIZE);
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
        for(StubItemVO item:this)
        {
            item.setPageId(pageId);
        }
    }

    @Override
    protected String getPath() {
        return "stub/item/path/"+pageId;
    }

    @Override
    protected Map<String, String> getItemRequestPostParameters() {
        return null;
    }

    @Override
    protected Map<String, String> getItemRequestGetParameters(BaseRequest.RequestMethod method) {
        return null;
    }
}
