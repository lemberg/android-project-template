package com.ls.templateproject.view.typeface;


import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class TypefaceContainer {

    private final static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

    public final static String getFontPathFromName(final String theFontName) {
        return "fonts/" + theFontName;
    }

    public final static Typeface getTypeface(Context theContext, String theTypeface) {
        Typeface typeface;
        typeface = fonts.get(theTypeface);
        if (typeface == null) {
            typeface = Typeface
                    .createFromAsset(theContext.getAssets(), getFontPathFromName(theTypeface));
            fonts.put(theTypeface, typeface);
        }

        return typeface;
    }

    public final static Typeface getTypeface(Context theContext, int theTypefaceNameId) {
        return getTypeface(theContext, theContext.getResources().getString(theTypefaceNameId));
    }
}
