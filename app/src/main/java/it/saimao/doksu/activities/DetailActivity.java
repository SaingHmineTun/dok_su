package it.saimao.doksu.activities;

import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.saimao.doksu.utilities.DokSuTypefaceSpan;
import it.saimao.doksu.services.PlaybackService;
import it.saimao.doksu.R;
import it.saimao.doksu.utilities.Utils;

@UnstableApi
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ImageButton fabNext;
    private ImageButton fabPlay;
    private ImageButton fabPrev;
    private boolean isEnded;
    private TextView lyric;
    private ScrollView lyricLayout;
    private Handler mHandler;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (mediaController != null) {
                int currentPosition = (int) mediaController.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                tvStart.setText(Utils.formatToMinuteSeconds(mediaController.getCurrentPosition()));
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private LinearLayout mediaLayout;
    private boolean onRestart;
    private int pageNumber;
    private SeekBar seekBar;
    private TextSwitcher title;
    private TextView tvEnd;
    private TextView tvStart;
    private Typeface ajTypeFace;
    private Typeface nkTypeFace;
    private MediaController mediaController;
    private static final String[] menuItems = {"ၽုၺ်ႇသဵင်ၵႂၢမ်း", "ၼႄၶွတ်ႇတိင်ႇ"};

    private void setPlayFabImage(int i) {
        fabPlay.setImageResource(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem playSound = menu.add(menuItems[0]);
        playSound.setCheckable(true);
        playSound.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        playSound.setChecked(!Utils.isReadMode(this));

        MenuItem showChords = menu.add(menuItems[1]);
        showChords.setCheckable(true);
        showChords.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        showChords.setChecked(Utils.isShowChord(this));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getTitle() != null) {
            if (menuItem.getTitle().equals(menuItems[0])) {
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                    mediaLayout.setVisibility(View.GONE);
                    pageNumber = mediaController.getCurrentMediaItemIndex() + 1;
                    mediaController.stop();
                    Utils.setReadMode(this, true);
                } else {
                    menuItem.setChecked(true);
                    mediaLayout.setVisibility(View.VISIBLE);
                    playNewMedia();
                    Utils.setReadMode(this, false);
                }
            } else if (menuItem.getTitle().equals(menuItems[1])) {
                // Enable Guitar Chord
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                    Utils.setShowChord(this, false);
                } else {
                    menuItem.setChecked(true);
                    Utils.setShowChord(this, true);
                }
                updateLyricDisplay(pageNumber);
            }
        } else {
            // Go back menu finish current activity
            finish();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Utils.setDetailsController(this);
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                startPlayingDoksu();
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (isPlaying) {
                            setPlayFabImage(R.drawable.ic_pause_btn);
                        } else {
                            setPlayFabImage(R.drawable.ic_play_btn);
                        }
                    }

                    public void onPlaybackStateChanged(int i) {
                        if (i == PlaybackState.STATE_PLAYING) {
                            // ON & OFF read_mode will come to work here!
                            // Need to update UI
                            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
                            mHandler.post(mRunnable);
                            mediaController.play();
                        }
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());


    }

    private void startPlayingDoksu() {
        // Call when new doksu song is selected
        pageNumber = getIntent().getIntExtra("number", 0);
        lyric.setTypeface(this.nkTypeFace);
        if (Utils.isReadMode(this)) {
            mediaLayout.setVisibility(View.GONE);
            return;
        }
        mediaLayout.setVisibility(View.VISIBLE);
        if (pageNumber == Utils.getPlayingSong()) {
            playCurrentMedia();
        } else if (onRestart) {
            onRestart = false;
            playCurrentMediaWithUpdatedUI();
        } else {
            playNewMedia();
        }
        updateMediaView(pageNumber, mediaController);
    }

    private void updateLyricTitle(int pageNumber) {
        title.setText(Utils.lyricTitle(pageNumber));
    }

    private void playCurrentMediaWithUpdatedUI() {
        pageNumber = mediaController.getCurrentMediaItemIndex() + 1;
        updateMediaView(pageNumber, mediaController);
        playCurrentMedia();
    }

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
            title.setSpan(new DokSuTypefaceSpan(Utils.getAjKunheingFont(this), Utils.dpToPx(this, 34), 0.1f, Color.WHITE), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail);
        setupActionBarStyle();
        ajTypeFace = ResourcesCompat.getFont(this, R.font.aj_kunheing);
        nkTypeFace = ResourcesCompat.getFont(this, R.font.namteng);
        mHandler = new Handler();
        mediaLayout = findViewById(R.id.mediaLayout);
        ScrollView scrollView = findViewById(R.id.lyricLayout);
        lyricLayout = scrollView;
        scrollView.setOnTouchListener(this);
        title = findViewById(R.id.lyricTitle);
        lyric = findViewById(R.id.readerView);
        tvStart = findViewById(R.id.tv_start);
        tvEnd = findViewById(R.id.tv_end);
        seekBar = findViewById(R.id.seek_play);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.post(mRunnable);
                mediaController.seekTo(seekBar.getProgress());
            }
        });
        fabNext = findViewById(R.id.fab_next);
        fabPlay = findViewById(R.id.fab_play);
        fabPrev = findViewById(R.id.fab_prev);
        fabNext.setOnClickListener(this);
        fabPlay.setOnClickListener(this);
        fabPrev.setOnClickListener(this);
        title.setFactory(() -> {
            TextView textView = new TextView(DetailActivity.this);
            textView.setTextSize(28.0f);
            textView.setHeight(Utils.dpToPx(this, 75));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(DetailActivity.this.ajTypeFace);
            title.setInAnimation(DetailActivity.this, android.R.anim.fade_in);
            title.setOutAnimation(DetailActivity.this, android.R.anim.fade_out);
            return textView;
        });
    }

    private float x1, x2;

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN -> x1 = motionEvent.getX();
            case MotionEvent.ACTION_UP -> {
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                if (deltaX > 300) {
                    if (Utils.isReadMode(this))
                        onPagePrev();
                    else
                        onTrackPrev();

                } else if (deltaX < -300) {
                    if (Utils.isReadMode(this))
                        onPageNext();
                    else
                        onTrackNext();
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (mediaController != null && mediaController.isPlaying()) {
            Utils.setPlayingSong(mediaController.getCurrentMediaItemIndex() + 1);
        }
    }

    private void playCurrentMedia() {
        try {
            /*
            Re-enter the playing song will only update UI!
             */
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
            mHandler.post(mRunnable);
            Log.d("Doksu", "RE-ENTER : " + mediaController.isPlaying());
            int playIcon = mediaController.isPlaying() ? R.drawable.ic_pause_btn : R.drawable.ic_play_btn;
            setPlayFabImage(playIcon);
        } catch (Exception ignored) {
        }
    }

    private void playNewMedia() {
        mediaController.prepare();
        mediaController.seekTo(pageNumber - 1, -1);
        mediaController.play();
    }

    public void onClick(View view) {
        var id = view.getId();
        if (id == R.id.fab_next) {
            onTrackNext();
        } else if (id == R.id.fab_play) {
            if (mediaController.isPlaying()) {
                onTrackPause();
            } else {
                onTrackPLay();
            }
        } else if (id == R.id.fab_prev) {
            onTrackPrev();
        }
    }

    private void onPageNext() {
        pageNumber++;
        if (pageNumber > Utils.lyricTitles().length) {
            pageNumber = 1;
        }
        lyric.setAnimation(AnimationUtils.makeInAnimation(this, false));
        updateMediaView(pageNumber, mediaController);
    }

    private void updateLyricDisplay(int pageNumber) {
        SpannableStringBuilder spannableStringBuilder;
        if (Utils.isShowChord(this)) {
            String lyrics = Utils.readLyricsWithChords(this, pageNumber);
            spannableStringBuilder = new SpannableStringBuilder(lyrics);
            Pattern pattern = Pattern.compile("\\b([A-GO](#|b|m|bm|7)?\\s*)\\b");
            Matcher matcher = pattern.matcher(lyrics);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#f7620b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            String lyrics = Utils.readLyricsOnly(this, pageNumber);
            spannableStringBuilder = new SpannableStringBuilder(lyrics);
        }
        String lyrics = spannableStringBuilder.toString();
        String[] lines = lyrics.split("\n");
        for (String line : lines) {
            if (line.contains("-")) {
                int start = lyrics.indexOf(line);
                int end = start + line.length();
                spannableStringBuilder.setSpan(new DokSuTypefaceSpan(ajTypeFace, Utils.dpToPx(this, 30), Color.BLACK), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        lyric.setText(spannableStringBuilder);
    }

    private void onPagePrev() {
        pageNumber--;
        if (pageNumber == 0) {
            pageNumber = Utils.lyricTitles().length;
        }
        lyric.setAnimation(AnimationUtils.makeInAnimation(this, true));
        updateMediaView(pageNumber, mediaController);
    }

    public void onTrackPLay() {
        if (mediaController != null && !mediaController.isPlaying()) {
            if (isEnded) {
                isEnded = false;
                mediaController.seekTo(pageNumber - 1, -1);
            }
            mediaController.play();
            setPlayFabImage(R.drawable.ic_pause_btn);
        }
    }

    public void onTrackPause() {
        if (mediaController != null && mediaController.isPlaying()) {
            mediaController.pause();
            setPlayFabImage(R.drawable.ic_play_btn);
        }
    }

    public void onTrackPrev() {
        if (mediaController.hasPreviousMediaItem()) {
            mediaController.seekToPreviousMediaItem();
            lyric.setAnimation(AnimationUtils.makeInAnimation(this, true));
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
        } else {
            Toast.makeText(this, "No previous songs", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTrackNext() {
        if (mediaController.hasNextMediaItem()) {
            mediaController.seekToNextMediaItem();
            lyric.setAnimation(AnimationUtils.makeInAnimation(this, false));
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
        } else {
            Toast.makeText(this, "No next songs", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: private */
    public void updateMediaView(int pageNumber, Player player) {
        Log.d("Doksu", "Update Media View - Page Number : " + pageNumber);
        if (pageNumber == 0) {
            pageNumber = Utils.lyricTitles().length;
        } else if (pageNumber > Utils.lyricTitles().length) {
            pageNumber = 1;
        }
        this.pageNumber = pageNumber;
        Utils.setPlayingSong(pageNumber);
        updateLyricTitle(pageNumber);
        updateLyricDisplay(pageNumber);
        tvEnd.setText(Utils.formatToMinuteSeconds(player.getDuration()));
        seekBar.setMax((int) player.getDuration());
        lyricLayout.scrollTo(0, 0);
        MainActivity.setCurrentSong(pageNumber);
    }
}