package com.ls.druplaproject.model.requests;

import com.google.gson.reflect.TypeToken;

import com.ls.druplaproject.ApplicationConfig;
import com.ls.druplaproject.model.data.vo.StubItemVO;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created on 22.05.2015.
 */
public class StubItemRequestBuilder extends BaseRequestBuilder
{

    public StubItemRequestBuilder()
    {
        setRequestMethod(BaseRequest.RequestMethod.GET);
        setResponseFormat(BaseRequest.ResponseFormat.JSON);
        Type listType = new TypeToken<List<StubItemVO>>(){}.getType();
        setResponseClassSpecifier(listType);
    }

    @Override
    public BaseRequest create()
    {
        setRequestURL(getPath());
        return super.create();
    }

    private String pageId;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }


    protected String getPath() {
        return ApplicationConfig.BASE_URL+"/demos/DrupalTemplate/stub_page_"+pageId+".txt";
    }
}
