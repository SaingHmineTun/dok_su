package it.saimao.doksu;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/* renamed from: it.saimao.doksu.DetailActivity */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static SimpleExoPlayer exoPlayer;
    private static FloatingActionButton fabLeft;
    private static ImageButton fabNext;
    private static ImageButton fabPlay;
    private static ImageButton fabPrev;
    private static FloatingActionButton fabRight;
    /* access modifiers changed from: private */
    public static boolean isEnded;
    private static TextView lyric;
    private static ScrollView lyricLayout;
    /* access modifiers changed from: private */
    public static Handler mHandler;
    /* access modifiers changed from: private */
    public static Runnable mRunnable = new Runnable() {
        public void run() {
            if (DetailActivity.exoPlayer != null) {
                int currentPosition = (int) DetailActivity.exoPlayer.getCurrentPosition();
                DetailActivity.seekPlay.setProgress(currentPosition);
                DetailActivity.tvStart.setText(Utils.formatToMinuteSeconds(exoPlayer.getCurrentPosition()));
                DetailActivity.mHandler.postDelayed(this, 100);
            }
        }
    };
    private static LinearLayout mediaLayout;
    private static boolean onRestart;
    private static int pageNumber;
    /* access modifiers changed from: private */
    public static SeekBar seekPlay;
    /* access modifiers changed from: private */
    public static TextSwitcher title;
    /* access modifiers changed from: private */
    public static TextView tvEnd;
    /* access modifiers changed from: private */
    public static TextView tvStart;
    /* access modifiers changed from: private */
    public Typeface typeface;

    public static void setPlayImage(int i) {
        fabPlay.setImageResource(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem add = menu.add("Play Sound");
        add.setCheckable(true);
        add.setShowAsAction(2);
        if (Utils.isReadMode(this)) {
            add.setIcon(R.drawable.ic_no_sound);
            add.setChecked(true);
        } else {
            add.setIcon(R.drawable.ic_sound);
            add.setChecked(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
            menuItem.setIcon(R.drawable.ic_sound);
            mediaLayout.setVisibility(View.VISIBLE);
            setShowReadMode(false);
            playMedia();
            Utils.setReadMode(this, false);
        } else {
            menuItem.setChecked(true);
            menuItem.setIcon(R.drawable.ic_no_sound);
            mediaLayout.setVisibility(View.GONE);
            setShowReadMode(true);
            pageNumber = exoPlayer.getCurrentWindowIndex() + 1;
            if (exoPlayer.isPlaying()) {
                exoPlayer.stop();
            }
            stopService();
            Utils.setReadMode(this, true);
        }
        return true;
    }

    private void setShowReadMode(boolean z) {
        if (z) {
            fabRight.setVisibility(View.VISIBLE);
            fabLeft.setVisibility(View.VISIBLE);
            return;
        }
        fabRight.setVisibility(View.GONE);
        fabLeft.setVisibility(View.GONE);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        int intExtra = getIntent().getIntExtra("number", 0);
        pageNumber = intExtra;
        title.setText(Utils.lyricTitle(intExtra));
        lyric.setText(Utils.readLyric(this, pageNumber));
        lyric.setTypeface(this.typeface);
        if (Utils.isReadMode(this)) {
            fabRight.setVisibility(View.VISIBLE);
            fabLeft.setVisibility(View.VISIBLE);
            mediaLayout.setVisibility(View.GONE);
            return;
        }
        fabRight.setVisibility(View.GONE);
        fabLeft.setVisibility(View.GONE);
        mediaLayout.setVisibility(View.VISIBLE);
        if (pageNumber == Utils.getPlayingSong()) {
            playCurrentMedia();
        } else if (onRestart) {
            onRestart = false;
            playCurrentMediaWithUpdatedUI();
        } else {
            playMedia();
        }
    }

    private void playCurrentMediaWithUpdatedUI() {
        updateDataForPlayingMediaItem();
        playCurrentMedia();
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        onRestart = true;
    }

    void setupActionBarStyle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            SpannableString title = new SpannableString("ၽဵင်းၵႂၢမ်းတုၵ်းသူး");
            title.setSpan(new DokSuTypefaceSpan(Utils.getAjKunheingFont(this), 90, 0.1f), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        }
    }


            /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_detail);
        setupActionBarStyle();
        typeface = ResourcesCompat.getFont(this, R.font.aj_kunheing);
        mHandler = new Handler();
        mediaLayout = (LinearLayout) findViewById(R.id.mediaLayout);
        ScrollView scrollView = (ScrollView) findViewById(R.id.lyricLayout);
        lyricLayout = scrollView;
        scrollView.setOnTouchListener(this);
        title = (TextSwitcher) findViewById(R.id.lyricTitle);
        lyric = (TextView) findViewById(R.id.readerView);
        tvStart = (TextView) findViewById(R.id.tv_start);
        tvEnd = (TextView) findViewById(R.id.tv_end);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_play);
        seekPlay = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    DetailActivity.mHandler.removeCallbacks(DetailActivity.mRunnable);
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                DetailActivity.mHandler.post(DetailActivity.mRunnable);
                DetailActivity.exoPlayer.seekTo((long) seekBar.getProgress());
            }
        });
        fabNext = (ImageButton) findViewById(R.id.fab_next);
        fabPlay = (ImageButton) findViewById(R.id.fab_play);
        fabPrev = (ImageButton) findViewById(R.id.fab_prev);
        fabLeft = (FloatingActionButton) findViewById(R.id.fab_left);
        fabRight = (FloatingActionButton) findViewById(R.id.fab_right);
        fabNext.setOnClickListener(this);
        fabPlay.setOnClickListener(this);
        fabPrev.setOnClickListener(this);
        fabLeft.setOnClickListener(this);
        fabRight.setOnClickListener(this);
        title.setFactory(() -> {
            TextView textView = new TextView(DetailActivity.this);
            textView.setTextSize(22.0f);
            textView.setGravity(17);
            textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            textView.setTypeface(DetailActivity.this.typeface);
            textView.setPadding(0, 10, 0, 0);
            DetailActivity.title.setInAnimation(DetailActivity.this, android.R.anim.fade_in);
            DetailActivity.title.setOutAnimation(DetailActivity.this, android.R.anim.fade_out);
            return textView;
        });
    }

    /* access modifiers changed from: private */
    public void startShowingNotification() {
        startService();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!Utils.isReadMode(this)) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            fabLeft.hide();
            fabRight.hide();
            return false;
        } else if (motionEvent.getAction() != 1) {
            return false;
        } else {
            fabLeft.show();
            fabRight.show();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        SimpleExoPlayer simpleExoPlayer = exoPlayer;
        if (simpleExoPlayer != null && simpleExoPlayer.isPlaying()) {
            Utils.setPlayingSong(exoPlayer.getCurrentWindowIndex() + 1);
        }
    }

    public void startService() {
        startService(new Intent(this, NotificationService.class));
    }

    public void stopService() {
        stopService(new Intent(this, NotificationService.class));
    }

    private void playCurrentMedia() {
        try {
            seekPlay.setMax((int) exoPlayer.getDuration());
            tvEnd.setText(Utils.formatToMinuteSeconds(exoPlayer.getDuration()));
            mHandler.post(mRunnable);
            Utils.setPlayingSong(exoPlayer.getCurrentWindowIndex() + 1);
            if (exoPlayer.isPlaying()) {
                setPlayImage(R.drawable.ic_pause_btn);
            } else {
                setPlayImage(R.drawable.ic_play_btn);
            }
        } catch (Exception unused) {
        }
    }

    private void playMedia() {
        try {
            if (exoPlayer == null) {
                Utils.initExoPlayer(this);
                SimpleExoPlayer exoPlayer2 = Utils.getExoPlayer();
                exoPlayer = exoPlayer2;
                exoPlayer2.setRepeatMode(Player.REPEAT_MODE_ALL);
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onEvents(Player player, Player.Events events) {
                        Player.Listener.super.onEvents(player, events);
                    }
                });
                exoPlayer.addListener((Player.Listener) new Player.Listener() {

                    public void onMediaItemTransition(MediaItem mediaItem, int i) {
                        DetailActivity.this.updateDataForPlayingMediaItem();
                    }

                    public void onPlaybackStateChanged(int i) {
                        if (i == 3) {
                            DetailActivity.seekPlay.setMax((int) DetailActivity.exoPlayer.getDuration());
//                            DetailActivity.tvEnd.setText(LrcHelper.formatTime(DetailActivity.exoPlayer.getDuration()));
                            DetailActivity.mHandler.post(DetailActivity.mRunnable);
                            Utils.setPlayingSong(DetailActivity.exoPlayer.getCurrentWindowIndex() + 1);
                            if (DetailActivity.this.isMyServiceDead()) {
                                DetailActivity.this.startShowingNotification();
                            }
                            DetailActivity.exoPlayer.play();
                        } else if (i == 4) {
                            boolean unused = DetailActivity.isEnded = true;
                        }
                    }
                });
            }
            exoPlayer.prepare();
            exoPlayer.seekTo(pageNumber - 1, -1);
        } catch (Exception ignored) {
        }
    }

    public void onClick(View view) {
        var id = view.getId();
        if (id == R.id.fab_left) {
            int i = pageNumber - 1;
            pageNumber = i;
            if (i == 0) {
                pageNumber = 37;
            }
            lyric.setAnimation(AnimationUtils.makeInAnimation(this, true));
            title.setText(Utils.lyricTitle(pageNumber));
            lyric.setText(Utils.readLyric(this, pageNumber));
            lyricLayout.scrollTo(0, 0);
        } else if (id == R.id.fab_next){
            onTrackNext();
        } else if (id == R.id.fab_play) {
            if (exoPlayer.isPlaying()) {
                onTrackPause();
            } else {
                onTrackPLay();
            }
        } else if (id == R.id.fab_prev){
            onTrackPrev();
        } else if (id == R.id.fab_right) {
            int i2 = pageNumber + 1;
            pageNumber = i2;
            if (i2 > 37) {
                pageNumber = 1;
            }
            lyric.setAnimation(AnimationUtils.makeInAnimation(this, false));
            title.setText(Utils.lyricTitle(pageNumber));
            lyric.setText(Utils.readLyric(this, pageNumber));
            lyricLayout.scrollTo(0, 0);
        }
    }

    public void onTrackPLay() {
        SimpleExoPlayer simpleExoPlayer = exoPlayer;
        if (simpleExoPlayer != null && !simpleExoPlayer.isPlaying()) {
            if (isEnded) {
                isEnded = false;
                exoPlayer.seekTo(pageNumber - 1, -1);
            }
            exoPlayer.play();
            fabPlay.setImageResource(R.drawable.ic_pause_btn);
            if (isMyServiceDead()) {
                startShowingNotification();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isMyServiceDead() {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (runningServiceInfo.service.getClassName().equals(NotificationService.class.getName())) {
                return false;
            }
        }
        return true;
    }

    public void onTrackPause() {
        SimpleExoPlayer simpleExoPlayer = exoPlayer;
        if (simpleExoPlayer != null && simpleExoPlayer.isPlaying()) {
            exoPlayer.pause();
            fabPlay.setImageResource(R.drawable.ic_play_btn);
        }
    }

    public void onTrackPrev() {
        if (exoPlayer.hasPrevious()) {
            exoPlayer.previous();
        } else {
            Toast.makeText(this, "No previous songs", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTrackNext() {
        if (exoPlayer.hasNext()) {
            exoPlayer.next();
        } else {
            Toast.makeText(this, "No next songs", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: private */
    public void updateDataForPlayingMediaItem() {
        pageNumber = exoPlayer.getCurrentWindowIndex() + 1;
        Utils.setPlayingSong(exoPlayer.getCurrentWindowIndex() + 1);
        title.setText(Utils.lyricTitle(pageNumber));
        lyric.setText(Utils.readLyric(this, pageNumber));
        lyricLayout.scrollTo(0, 0);
    }
}
