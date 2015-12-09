package com.ls.templateproject.model.plain.requests;

import com.google.gson.reflect.TypeToken;

import com.ls.templateproject.ApplicationConfig;
import com.ls.templateproject.model.data.vo.StubItemVO;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created on 22.05.2015.
 */
public class StubItemRequestBuilder extends BaseRequestBuilder
{
    private String pageId;

    public StubItemRequestBuilder(String pageId)
    {
        this.pageId = pageId;
        setRequestMethod(BaseRequest.RequestMethod.GET);
        setResponseFormat(BaseRequest.ResponseFormat.JSON);
        Type listType = new TypeToken<List<StubItemVO>>(){}.getType();
        setResponseClassSpecifier(listType);
        setRequestURL(getPath());
    }

    protected String getPath() {
        return ApplicationConfig.BASE_URL+"/demos/DrupalTemplate/stub_page_"+pageId+".txt";
    }
}
