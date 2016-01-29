package com.ls.templateproject.model.data.dao;

import com.ls.templateproject.model.data.base.AbstractDAO;
import com.ls.templateproject.model.data.vo.StubItemVO;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

/**
 * Created on 22.05.2015.
 */
public class StubItemDAO extends AbstractDAO<StubItemVO, String> {

    private final static String TABLE_NAME = "stub_items";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_IMAGE_URL = "imageURL";
    private final static String COLUMN_DESCRIPTION = "description";
    private final static String COLUMN_FAVORITE = "isFavorite";
    private final static String COLUMN_PAGE_ID = "page_id";

    public List<StubItemVO> readItemsForPageId(String pageId) {
        String condition = COLUMN_PAGE_ID + "=?";
        return getData(condition, new String[]{pageId});
    }

    public int removeItemsForPageId(String pageId) {
        String condition = COLUMN_PAGE_ID + "=?";
        return deleteData(condition, new String[]{pageId}, false);
    }

    @Override
    protected String getSearchCondition() {
        return COLUMN_ID + " = ?";
    }

    @Override
    protected String[] getSearchConditionArguments(String theId) {
        return new String[]{theId};
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected void putObjectDataForSaving(ContentValues theValues, StubItemVO theObj) {
        theValues.put(COLUMN_ID, theObj.getId());
        putObjectDataForUpdate(theValues, theObj);
    }

    @Override
    protected void putObjectDataForUpdate(ContentValues theValues, StubItemVO theObj) {
        theValues.put(COLUMN_IMAGE_URL, theObj.getImageURL());
        theValues.put(COLUMN_DESCRIPTION, theObj.getDescription());
        theValues.put(COLUMN_FAVORITE, getIntFromBool(theObj.isFavorite()));
        theValues.put(COLUMN_PAGE_ID, theObj.getPageId());
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[]{COLUMN_ID};
    }

    @Override
    protected StubItemVO getObjectFromCursor(Cursor theCursor) {
        int columnId = theCursor.getColumnIndex(COLUMN_ID);
        int columnURL = theCursor.getColumnIndex(COLUMN_IMAGE_URL);
        int columndDescription = theCursor.getColumnIndex(COLUMN_DESCRIPTION);
        int columnFavorite = theCursor.getColumnIndex(COLUMN_FAVORITE);
        int columnPageId = theCursor.getColumnIndex(COLUMN_PAGE_ID);

        StubItemVO item = new StubItemVO();
        item.setId(theCursor.getString(columnId));
        item.setImageURL(theCursor.getString(columnURL));
        item.setDescription(theCursor.getString(columndDescription));
        item.setIsFavorite(getBoolFromInt(theCursor.getInt(columnFavorite)));
        item.setPageId(theCursor.getString(columnPageId));
        return item;
    }
}
