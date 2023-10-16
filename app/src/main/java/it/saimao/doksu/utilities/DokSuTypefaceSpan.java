package it.saimao.doksu.utilities;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Type;

public class DokSuTypefaceSpan extends MetricAffectingSpan {
    private final Typeface typeface;
    private final int textSize;
    private final float letterSpacing;
    private final int color;
    public DokSuTypefaceSpan(Typeface typeface, int textSize, float letterSpacing, int color) {
        this.typeface = typeface;
        this.textSize = textSize;
        this.letterSpacing = letterSpacing;
        this.color = color;
    }

    public DokSuTypefaceSpan(Typeface typeface, int textSize, int color) {
        this.typeface = typeface;
        this.textSize = textSize;
        this.letterSpacing = 0;
        this.color = color;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        applyCustomTypeface(ds, typeface);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        applyCustomTypeface(paint, typeface);
    }

    public void applyCustomTypeface(Paint paint, Typeface typeface) {
        paint.setTypeface(typeface);
        paint.setTextSize(textSize);
        paint.setLetterSpacing(letterSpacing);
        paint.setColor(color);
    }
}
