package com.ls.templateproject.model.drupal.requests;

import com.ls.drupal.AbstractDrupalArrayEntity;
import com.ls.drupal.DrupalClient;
import com.ls.http.base.BaseRequest;
import com.ls.templateproject.model.data.vo.StubItemVO;

import java.util.Map;

/**
 * Created on 22.05.2015.
 */
public class StubItemRequest extends AbstractDrupalArrayEntity<StubItemVO> {

    private final static int PAGE_SIZE = 5;

    private String pageId;

    public StubItemRequest(DrupalClient client) {
        super(client, PAGE_SIZE);
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    @Override
    protected String getPath() {
        return "demos/DrupalTemplate/stub_page_" + pageId + ".txt";
    }

    @Override
    protected Map<String, String> getItemRequestPostParameters() {
        return null;
    }

    @Override
    protected Map<String, Object> getItemRequestGetParameters(BaseRequest.RequestMethod method) {
        return null;
    }
}
