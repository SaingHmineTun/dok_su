package it.saimao.doksu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.PlaybackState;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaNotification;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.MediaStyleNotificationHelper;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PlaybackService extends MediaSessionService {

    private List<MediaItem> allMediaItems;
    private MediaSession mediaSession;
    private ExoPlayer exoPlayer;

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exoPlayer = new ExoPlayer
                .Builder(this)
                .build();
        exoPlayer.setVolume(1.0f);
        exoPlayer.addListener(new Player.Listener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == PlaybackState.STATE_STOPPED) {
                    stopForeground(true);
                    stopSelf();
                }
            }
        });
        initAllMediaItems();
        setupNotificationDesign();
        mediaSession = new MediaSession.Builder(this, exoPlayer).build();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupNotificationDesign() {
        setMediaNotificationProvider(new MediaNotification.Provider() {
            @Override
            public MediaNotification createNotification(MediaSession mediaSession, ImmutableList<CommandButton> customLayout, MediaNotification.ActionFactory actionFactory, Callback onNotificationChangedCallback) {
                createNoti(mediaSession);
                return new MediaNotification(NOTIFICATION_ID, nBuilder.build());
            }

            @Override
            public boolean handleCustomCommand(MediaSession session, String action, Bundle extras) {
                return false;
            }
        });
    }

    private final String notificationId = "sai_mao";
    private final int NOTIFICATION_ID = 2846;
    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;

    @OptIn(markerClass = UnstableApi.class)
    private void createNoti(MediaSession mediaSession) {
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.createNotificationChannel(new NotificationChannel(notificationId, "channel", NotificationManager.IMPORTANCE_LOW));

        final Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.noti_bg);
        nBuilder = new NotificationCompat.Builder(this, notificationId)
                .setSmallIcon(R.drawable.noti)
                .setLargeIcon(decodeResource)
                .setContentIntent(createCurrentContentIntent())
                .setStyle(new MediaStyleNotificationHelper.MediaStyle(mediaSession));
    }
    @OptIn(markerClass = UnstableApi.class)
    public PendingIntent createCurrentContentIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder create = TaskStackBuilder.create(this.getApplicationContext());
        create.addParentStack(MainActivity.class);
        create.addNextIntent(intent);
        Intent intent2 = new Intent(this, DetailActivity.class);
        intent2.putExtra("number", exoPlayer.getCurrentMediaItemIndex() + 1);
        create.addNextIntent(intent2);
        return create.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    @OptIn(markerClass = UnstableApi.class)
    public void onUpdateNotification(MediaSession session, boolean startInForegroundRequired) {
        super.onUpdateNotification(session, startInForegroundRequired);
        if (startInForegroundRequired) {
            int currentIndex = exoPlayer.getCurrentMediaItemIndex();
            nBuilder.setContentTitle(Utils.lyricTitle(currentIndex + 1));
            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    public void initAllMediaItems() {
        if (allMediaItems == null) {
            allMediaItems = new ArrayList<>();
            for (int buildRawResourceUri : Utils.getMediaItemsID()) {
                MediaItem mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(buildRawResourceUri));
                allMediaItems.add(mediaItem);
            }
        }
        exoPlayer.setMediaItems(allMediaItems);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }
}