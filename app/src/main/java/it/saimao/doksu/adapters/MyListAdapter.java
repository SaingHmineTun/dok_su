package it.saimao.doksu.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import it.saimao.doksu.R;

/* renamed from: it.saimao.doksu.adapters.MyListAdapter */
public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] titles;
    private Typeface typeface;

    public boolean isEnabled(int i) {
        return false;
    }

    public MyListAdapter(Activity activity, String[] strArr) {
        super(activity, R.layout.mylist, strArr);
        this.context = activity;
        this.titles = strArr;
//        this.typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/ajkunheing.ttf");
        this.typeface = ResourcesCompat.getFont(getContext(), R.font.aj_kunheing);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout linearLayout = (LinearLayout) this.context.getLayoutInflater().inflate(R.layout.mylist, (ViewGroup) null, true);
        TextView textView = (TextView) linearLayout.findViewById(R.id.txtTitle);
        textView.setTypeface(this.typeface);
        textView.setText("(" + (i + 1) + ") " + this.titles[i]);
        return linearLayout;
    }
}
