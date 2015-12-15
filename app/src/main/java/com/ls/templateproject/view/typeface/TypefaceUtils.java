package com.ls.templateproject.view.typeface;



import com.ls.templateproject.R;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 23.10.2014.
 */
public final class TypefaceUtils {

    public interface TypefaceHolder
    {
        public void setTypeface(final String theName);
    }

    public final static <T extends View & TypefaceHolder> void applyTypefaceAtts(AttributeSet attrs,T fontView)
    {
        if (fontView.isInEditMode()) {
            return;
        }
        TypedArray array = fontView.getContext().obtainStyledAttributes(attrs, R.styleable.FontTextView);
        String font = array.getString(R.styleable.FontTextView_customFont);
        if (font != null) {
            fontView.setTypeface(font);
        }
        array.recycle();
    }
}
