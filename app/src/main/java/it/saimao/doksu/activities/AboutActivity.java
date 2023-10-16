package it.saimao.doksu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import it.saimao.doksu.adapters.AboutAdapter;
import it.saimao.doksu.utilities.DokSuTypefaceSpan;
import it.saimao.doksu.R;
import it.saimao.doksu.utilities.Utils;

/* renamed from: it.saimao.doksu.AboutUsActivity */
public class AboutActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    int[] icons = {R.drawable.ic_music, R.drawable.ic_gmail, R.drawable.ic_facebook, R.drawable.ic_playstore};
    private final String[] stringsAsk = { "သဵင်ၵႂၢမ်း : ", "ဢီးမေးလ် : ", "ၾဵတ်ႉပုၵ်ႉ : ", "ပၼ်ဢႅပ်းတုၵ်းသူးၼႆႉ လၢဝ်ႁႃႈလုၵ်ႈ!"};
    private final String[] stringsValue = { "ၵေႃလိၵ်ႈလၢႆးလႄႈ ၽိင်ႈငႄႈတႆး၊ ၸေႊဝဵင်းမူႇၸေႊ။", "tmk.muse@gmail.com", "ထုင့်မၢဝ်းၶမ်း", ""};

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_about);
        ListView simpleListView = (ListView) findViewById(R.id.simpleListView);
        AboutAdapter aboutAdapter = new AboutAdapter(getBaseContext(), this.stringsAsk, this.stringsValue, this.icons);
        simpleListView.setOnItemClickListener(this);
        simpleListView.setAdapter(aboutAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupActionBarStyle();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Intent intent;
        if (i == 2) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("fb://page/100377671433172"));
            } catch (Exception unused) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/100377671433172"));
            }
            startActivity(intent);
        } else if (i == 1) {
            Intent intent2 = new Intent("android.intent.action.SEND");
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{"tmk.muse@gmail.com"});
            intent2.setType("message/rfc822");
            startActivity(Intent.createChooser(intent2, "Choose an Email client :"));
        } else if (i == 0) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("fb://page/103066969300092"));
            } catch (Exception unused) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/103066969300092"));
            }
            startActivity(intent);
        }

        else if (i == 3) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=it.saimao.doksu")));
        }
    }

    void setupActionBarStyle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            SpannableString title = new SpannableString("လွင်ႈၽူႈၶူင်သၢင်ႈ");
            title.setSpan(new DokSuTypefaceSpan(Utils.getAjKunheingFont(this), Utils.dpToPx(this, 34), 0.1f, Color.WHITE),0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return true;
        }
        finish();
        return true;
    }
}
