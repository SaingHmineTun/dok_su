package it.saimao.doksu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

/* renamed from: it.saimao.doksu.AboutUsActivity */
public class AboutActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    int[] icons = {R.drawable.ic_gmail, R.drawable.ic_facebook, R.drawable.ic_guitar, R.drawable.ic_playstore};
    private ListView simpleListView;
    private String[] stringsAsk = {"E-mail : ", "Facebook : ", "ၵႂၢမ်းတုၵ်းသူး : ", "Rate this app on Play Store"};
    private String[] stringsValue = {"tmk.muse@gmail.com", "ထုင့်မၢဝ်းၶမ်း", "ၵေႃလိၵ်ႈ/ၽိင်ႈတႆး၊\nၸေႊဝဵင်းမူႇၸေႊ။", ""};

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView((int) R.layout.activity_about);
        this.simpleListView = (ListView) findViewById(R.id.simpleListView);
        MaoAdapter maoAdapter = new MaoAdapter(getBaseContext(), this.stringsAsk, this.stringsValue, this.icons);
        this.simpleListView.setOnItemClickListener(this);
        this.simpleListView.setAdapter(maoAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Intent intent;
        if (i == 1) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("fb://page/100377671433172"));
            } catch (Exception unused) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/100377671433172"));
            }
            startActivity(intent);
        } else if (i == 0) {
            Intent intent2 = new Intent("android.intent.action.SEND");
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{"tmk.muse@gmail.com"});
            intent2.setType("message/rfc822");
            startActivity(Intent.createChooser(intent2, "Choose an Email client :"));
        } else if (i == 3) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=it.saimao.doksu")));
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
