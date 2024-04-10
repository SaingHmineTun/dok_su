package it.saimao.doksu.activities;

import android.content.ComponentName;
import android.graphics.Typeface;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.saimao.doksu.R;
import it.saimao.doksu.databinding.ActivityDetailBinding;
import it.saimao.doksu.services.PlaybackService;
import it.saimao.doksu.utilities.DokSuTypefaceSpan;
import it.saimao.doksu.utilities.Utils;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private boolean isEnded;
    private Handler mHandler;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (mediaController != null) {
                int currentPosition = (int) mediaController.getCurrentPosition();
                binding.seekPlay.setProgress(currentPosition);
                binding.tvStart.setText(Utils.formatToMinuteSeconds(mediaController.getCurrentPosition()));
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private boolean onRestart;
    private Typeface ajTypeFace;
    private MediaController mediaController;

    private ActivityDetailBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
        initTypeface();
        initListeners();
        mHandler = new Handler();
    }

    private void initUi() {
        binding.cbPlayDokSuSong.setChecked(!Utils.isReadMode(this));
        binding.cbShowGuitarChord.setChecked(Utils.isShowChord(this));
    }

    private void initListeners() {
        binding.mGoBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.lyricLayout.setOnTouchListener(this);
        binding.seekPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        binding.fabNext.setOnClickListener(this);
        binding.fabPlay.setOnClickListener(this);
        binding.fabPrev.setOnClickListener(this);
        binding.fabStop.setOnClickListener(this);
        binding.cbShowGuitarChord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Utils.setShowChord(this, isChecked);
            updateLyricDisplay(Utils.getPageNumber());
        });

        binding.cbPlayDokSuSong.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Utils.setReadMode(this, !isChecked);
            if (!isChecked) {
                showMediaInput(false);
                mediaController.stop();
            } else {
                showMediaInput(true);
                initMediaPlayer();
            }
        });

    }

    private void showMediaInput(boolean isShowing) {
        if (isShowing) {
            binding.llSeekBar.setVisibility(View.VISIBLE);
            binding.llPlaySong.setVisibility(View.VISIBLE);
            binding.llStopSong.setVisibility(View.VISIBLE);
        } else {
            binding.llSeekBar.setVisibility(View.GONE);
            binding.llPlaySong.setVisibility(View.GONE);
            binding.llStopSong.setVisibility(View.GONE);
        }
    }

    private void initTypeface() {
        ajTypeFace = ResourcesCompat.getFont(this, R.font.aj_kunheing);
    }

    private void setPlayFabImage(int i) {
        binding.fabPlay.setImageResource(i);
    }


    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Utils.setPageNumber(getIntent().getIntExtra("number", 0));
        if (Utils.isReadMode(this)) {
            initLyricReader();
        } else {
            initMediaPlayer();
        }
    }

    private void initLyricReader() {
        showMediaInput(false);
        updateMediaView(Utils.getPageNumber(), null);
    }

    private void initMediaPlayer() {
        if (mediaController == null) {
            SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));
            ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
            controllerFuture.addListener(() -> {
                try {
                    mediaController = controllerFuture.get();
                    startPlayingDokSuSong();
                    mediaController.addListener(new Player.Listener() {
                        @Override
                        public void onIsPlayingChanged(boolean isPlaying) {
                            Log.d("Kham", "Media Controller Is Playing : " + isPlaying);
                            if (isPlaying) {
                                setPlayFabImage(R.drawable.ic_pause);
                                Utils.setPlaying(true);
                            } else {
                                setPlayFabImage(R.drawable.ic_play);
                                Utils.setPlaying(false);
                            }
                        }

                        @Override
                        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                            Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                            if (reason == 1) {
                                updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
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
        } else {
            startPlayingDokSuSong();
        }
    }

    // Call by Media Player only!
    private void startPlayingDokSuSong() {
        showMediaInput(true);
        if (Utils.getPageNumber() == mediaController.getCurrentMediaItemIndex() + 1) {
            playCurrentMedia();
        } else if (onRestart) {
            onRestart = false;
            playCurrentMediaWithUpdatedUI();
        } else {
            playNewMedia();
        }
        updateMediaView(Utils.getPageNumber(), mediaController);
    }

    private void updateLyricTitle(int pageNumber) {
        binding.lyricTitle.setText(Utils.lyricTitle(pageNumber));
    }

    private void playCurrentMediaWithUpdatedUI() {
        updateMediaView(Utils.getPageNumber(), mediaController);
        playCurrentMedia();
    }

    public void onRestart() {
        super.onRestart();
        onRestart = true;
    }

    private float x1, x2;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN -> x1 = motionEvent.getX();
            case MotionEvent.ACTION_UP -> {
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                if (deltaX > 300) {
                    if (Utils.isReadMode(this)) onPagePrev();
                    else onTrackPrev();

                } else if (deltaX < -300) {
                    if (Utils.isReadMode(this)) onPageNext();
                    else onTrackNext();
                }
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaController != null && mediaController.isPlaying()) {
            Utils.setPageNumber(mediaController.getCurrentMediaItemIndex() + 1);
        }
    }

    /*
    Re-enter the playing song will only update UI!
     */
    private void playCurrentMedia() {
        try {
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
            mHandler.post(mRunnable);
            int playIcon = mediaController.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;
            setPlayFabImage(playIcon);
            if (!mediaController.isPlaying()) {
                mediaController.seekTo(0);
                mediaController.play();
            }
        } catch (Exception ignored) {
        }
    }

    private void playNewMedia() {
        mediaController.prepare();
        mediaController.seekTo(Utils.getPageNumber() - 1, -1);
        mediaController.play();
    }

    @Override
    public void onClick(View view) {
        var id = view.getId();
        if (id == R.id.fab_next) {
            if (Utils.isReadMode(this)) onPageNext();
            else onTrackNext();
        } else if (id == R.id.fab_play) {
            if (mediaController.isPlaying()) {
                onTrackPause();
            } else {
                onTrackPLay();
            }
        } else if (id == R.id.fab_prev) {
            if (Utils.isReadMode(this)) onPagePrev();
            else onTrackPrev();
        } else if (id == R.id.fab_stop) {
            Utils.setPlaying(false);
            onTrackStop();
            finish();
        }
    }

    /*
    Page Next & Page Prev works with lyric view
     */
    private void onPageNext() {
        Utils.setPageNumber(Utils.getPageNumber() + 1);
        if (Utils.getPageNumber() > Utils.lyricTitles().length) {
            Utils.setPageNumber(1);
        }
        binding.lyricTitle.setAnimation(AnimationUtils.makeInAnimation(this, false));
        updateMediaView(Utils.getPageNumber(), mediaController);
    }

    private void onPagePrev() {
        Utils.setPageNumber(Utils.getPageNumber() - 1);
        if (Utils.getPageNumber() == 0) {
            Utils.setPageNumber(Utils.lyricTitles().length);
        }
        binding.lyricTitle.setAnimation(AnimationUtils.makeInAnimation(this, true));
        updateMediaView(Utils.getPageNumber(), mediaController);
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
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Utils.getColorFromTheme(this, Utils.COLORS.get(4))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                spannableStringBuilder.setSpan(new DokSuTypefaceSpan(ajTypeFace, Utils.dpToPx(this, 30), Utils.getColorFromTheme(this, Utils.COLORS.get(4))), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        binding.readerView.setText(spannableStringBuilder);
    }

    /*
    onTrackPlay, onTrackPause, onTrackNext, onTrackPrev work with Media Player
     */
    public void onTrackPLay() {
        if (mediaController != null && !mediaController.isPlaying()) {
            if (isEnded) {
                isEnded = false;
                mediaController.seekTo(Utils.getPageNumber() - 1, -1);
            }
            mediaController.play();
            setPlayFabImage(R.drawable.ic_pause);
        }
    }

    public void onTrackPause() {
        if (mediaController != null && mediaController.isPlaying()) {
            mediaController.pause();
            setPlayFabImage(R.drawable.ic_play);
        }
    }

    public void onTrackPrev() {
        if (mediaController.hasPreviousMediaItem()) {
            mediaController.seekToPreviousMediaItem();
            binding.lyricTitle.setAnimation(AnimationUtils.makeInAnimation(this, true));
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
        } else {
            Toast.makeText(this, "No previous songs", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTrackStop() {
        if (mediaController != null) mediaController.stop();
    }

    public void onTrackNext() {
        if (mediaController.hasNextMediaItem()) {
            mediaController.seekToNextMediaItem();
            binding.lyricTitle.setAnimation(AnimationUtils.makeInAnimation(this, false));
            updateMediaView(mediaController.getCurrentMediaItemIndex() + 1, mediaController);
        } else {
            Toast.makeText(this, "No next songs", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateMediaView(int pageNumber, Player player) {
        if (pageNumber == 0) {
            pageNumber = Utils.lyricTitles().length;
        } else if (pageNumber > Utils.lyricTitles().length) {
            pageNumber = 1;
        }
        Utils.setPageNumber(pageNumber);
        updateLyricTitle(pageNumber);
        updateLyricDisplay(pageNumber);
        if (player != null) {
            if (player.getDuration() > 0)
                binding.tvEnd.setText(Utils.formatToMinuteSeconds(player.getDuration()));
            binding.seekPlay.setMax((int) player.getDuration());
            binding.lyricLayout.scrollTo(0, 0);
        }
        Utils.setPageNumber(pageNumber);
    }
}