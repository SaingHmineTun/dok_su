package it.saimao.doksu;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

/* renamed from: it.saimao.doksu.MaoAdapter */
public class AboutAdapter extends BaseAdapter {
    Context context;
    int[] icons;
    LayoutInflater inflater;
    String[] stringsAsk;
    String[] stringsValues;
    private final Typeface uniTypeface;

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public AboutAdapter(Context context, String[] strArr, String[] strArr2, int[] iArr) {
        this.context = context;
        this.stringsAsk = strArr;
        this.stringsValues = strArr2;
        this.icons = iArr;
        this.inflater = LayoutInflater.from(context);
//        this.uniTypeface = Typeface.createFromAsset(context2.getAssets(), "fonts/aj_kunheing.ttf");
        this.uniTypeface = ResourcesCompat.getFont(this.context, R.font.aj_kunheing);
    }

    public int getCount() {
        return this.stringsValues.length;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.inflater.inflate(R.layout.about_list_item, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.textAsk);
        TextView textView2 = (TextView) inflate.findViewById(R.id.textValue);
        textView.setTypeface(this.uniTypeface);
        textView2.setTypeface(this.uniTypeface);
        textView.setText(this.stringsAsk[i]);
        textView2.setText(this.stringsValues[i]);
        ((ImageView) inflate.findViewById(R.id.iconView)).setImageResource(this.icons[i]);
        return inflate;
    }
}
