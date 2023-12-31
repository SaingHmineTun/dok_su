package it.saimao.doksu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.media3.common.util.UnstableApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import it.saimao.doksu.utilities.DokSuTypefaceSpan;
import it.saimao.doksu.adapters.MyListAdapter;
import it.saimao.doksu.R;
import it.saimao.doksu.utilities.Utils;

@UnstableApi /* renamed from: it.saimao.doksu.activities.MainActivity */
public class MainActivity extends AppCompatActivity {
    private static ExtendedFloatingActionButton fabPlaying;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        setupActionBarStyle();
        MyListAdapter myListAdapter = new MyListAdapter(this, Utils.lyricTitles());
        ListView list = findViewById(R.id.list);
        fabPlaying = findViewById(R.id.fab_playing);
        if (Utils.getPageNumber() == 0) {
            fabPlaying.hide();
        } else {
            fabPlaying.show();
            ExtendedFloatingActionButton extendedFloatingActionButton = fabPlaying;
            extendedFloatingActionButton.setText(Utils.getPageNumber() + "");
        }
        fabPlaying.setOnClickListener(view -> MainActivity.this.gotoCurrentActivity());
        list.setAdapter(myListAdapter);

    }

    void setupActionBarStyle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            SpannableString title = new SpannableString("ၽဵင်းၵႂၢမ်းတုၵ်းသူး");
            title.setSpan(new DokSuTypefaceSpan(Utils.getAjKunheingFont(this), Utils.dpToPx(this, 34), 0.1f, Color.WHITE),0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
        }
    }
    /* access modifiers changed from: private */
    public void gotoCurrentActivity() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("number", Utils.getPageNumber());
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        if (Utils.isReadMode(this)) {
            fabPlaying.hide();
            return;
        }
        fabPlaying.show();
        ExtendedFloatingActionButton extendedFloatingActionButton = fabPlaying;
        extendedFloatingActionButton.setText(Utils.getPageNumber() + "");
    }

    public static void setCurrentSong(int i) {
        fabPlaying.setText(String.valueOf(i));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem add = menu.add("About");
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setIcon(R.drawable.ic_about);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        startActivity(new Intent(this, AboutActivity.class));
        return super.onOptionsItemSelected(menuItem);
    }

    public void item_click(View view) {
        LinearLayout layout = (LinearLayout) ((CardView) view).getChildAt(0);
        TextView textView = (TextView) layout.getChildAt(1);
        String charSequence = textView.getText().toString();
        String substring = charSequence.substring(charSequence.indexOf(40) + 1, charSequence.indexOf(41));
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("number", Integer.valueOf(substring));
        startActivity(intent);
    }
}
