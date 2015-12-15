package com.ls.templateproject.view.typeface;



import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class FontEditText extends EditText implements TypefaceUtils.TypefaceHolder {
    public FontEditText(Context context) {
        super(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(attrs);
    }

    private void applyAttributes(AttributeSet attrs) {
        TypefaceUtils.applyTypefaceAtts(attrs,this);
    }

    public void setTypeface(final String theName) {
        this.setTypeface(TypefaceContainer.getTypeface(this.getContext(), theName));
    }

    public void setTypeface(final String theName, int theStyle) {
        this.setTypeface(TypefaceContainer.getTypeface(this.getContext(), theName), theStyle);
    }

    public void setTypeface(int theNameResId) {
        this.setTypeface(TypefaceContainer.getTypeface(this.getContext(), theNameResId));
    }

    public void setTypeface(int theNameResId, int theStyle) {
        this.setTypeface(TypefaceContainer.getTypeface(this.getContext(), theNameResId), theStyle);
    }
}
