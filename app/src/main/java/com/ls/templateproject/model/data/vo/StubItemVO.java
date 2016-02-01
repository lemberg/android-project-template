package com.ls.templateproject.model.data.vo;

import com.ls.templateproject.model.data.base.AbstractVO;

/**
 * Created on 22.05.2015.
 */
public class StubItemVO extends AbstractVO<String> {

    private String imageURL;
    private String description;
    private Boolean isFavorite;
    private String pageId;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}
