package com.ls.templateproject.view.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomFontTextSpan
        extends MetricAffectingSpan {

    final Typeface customFont;

    public CustomFontTextSpan(int theResId, final Context theContext) {
        this.customFont = TypefaceContainer.getTypeface(theContext, theContext.getResources().getString(theResId));
    }

    public CustomFontTextSpan(String family, final Context theContext) {
        this.customFont = TypefaceContainer.getTypeface(theContext, family);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        if (customFont != null) {
            p.setTypeface(customFont);
        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        if (customFont != null) {
            tp.setTypeface(customFont);
        }
    }

    @Override
    public MetricAffectingSpan getUnderlying() {
        return this;
    }
}
