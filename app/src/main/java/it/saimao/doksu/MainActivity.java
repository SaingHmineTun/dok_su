package it.saimao.doksu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/* renamed from: it.saimao.doksu.MainActivity */
public class MainActivity extends AppCompatActivity {
    private static ExtendedFloatingActionButton fabPlaying;
    private ListView list;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        setupActionBarStyle();
        MyListAdapter myListAdapter = new MyListAdapter(this, Utils.lyricTitles());
        this.list = findViewById(R.id.list);
        fabPlaying = findViewById(R.id.fab_playing);
        if (Utils.getPlayingSong() == 0) {
            fabPlaying.hide();
        } else {
            fabPlaying.show();
            ExtendedFloatingActionButton extendedFloatingActionButton = fabPlaying;
            extendedFloatingActionButton.setText(Utils.getPlayingSong() + "");
        }
        fabPlaying.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.gotoCurrentActivity();
            }
        });
        this.list.setAdapter(myListAdapter);

    }

    void setupActionBarStyle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            SpannableString title = new SpannableString("ၽဵင်းၵႂၢမ်းတုၵ်းသူး");
            title.setSpan(new DokSuTypefaceSpan(Utils.getAjKunheingFont(this), 90, 0.1f),0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void actionBarIdForAll()
    {
        int titleId = 0;

        titleId = getResources().getIdentifier("action_bar_title", "id", "android");

        if(titleId>0)
        {
            // Do whatever you want ? It will work for all the versions.

            // 1. Customize your fonts
            // 2. Infact, customize your whole title TextView

            TextView titleView = (TextView) findViewById(titleId);
            titleView.setText("RedoApp");
            titleView.setTextColor(Color.CYAN);
        }
    }

    /* access modifiers changed from: private */
    public void gotoCurrentActivity() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("number", Utils.getPlayingSong());
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        if (Utils.getPlayingSong() == 0) {
            fabPlaying.hide();
            return;
        }
        fabPlaying.show();
        ExtendedFloatingActionButton extendedFloatingActionButton = fabPlaying;
        extendedFloatingActionButton.setText(Utils.getPlayingSong() + "");
    }

    public static void setCurrentSong(int i) {
        fabPlaying.setText(String.valueOf(i));
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem add = menu.add("About");
        add.setCheckable(true);
        add.setShowAsAction(2);
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