package it.saimao.doksu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

/* renamed from: it.saimao.doksu.NotificationService */
public class NotificationService extends Service {
    static SimpleExoPlayer exoPlayer;
    static PlayerNotificationManager playerNotificationManager;
    private int notificationId = 1234;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean stopService(Intent intent) {
        stopService();
        return super.stopService(intent);
    }

    public void stopService() {
        stopForeground(true);
        stopSelf();
        Utils.setPlayingSong(0);
        if (exoPlayer.isPlaying()) {
            exoPlayer.stop();
        }
    }

    public void onCreate() {
        super.onCreate();
        exoPlayer = DetailActivity.exoPlayer;
        addPlayerToNotificationService();
    }

    public void onTaskRemoved(Intent intent) {
        stopService();
        super.onTaskRemoved(intent);
    }

    public void onDestroy() {
        PlayerNotificationManager playerNotificationManager2 = playerNotificationManager;
        if (playerNotificationManager2 != null) {
            playerNotificationManager2.setPlayer((Player) null);
            playerNotificationManager = null;
        }
        super.onDestroy();
    }

    private void addPlayerToNotificationService() {
        if (playerNotificationManager == null) {
            final Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.noti_bg);
//            playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel((Context) this, "my_channel_id", (int) R.string.app_name, this.notificationId, (PlayerNotificationManager.MediaDescriptionAdapter) new PlayerNotificationManager.MediaDescriptionAdapter() {
//                public /* synthetic */ CharSequence getCurrentSubText(Player player) {
//                    return PlayerNotificationManager.MediaDescriptionAdapter.CC.$default$getCurrentSubText(this, player);
//                }
//
//                public CharSequence getCurrentContentTitle(Player player) {
//                    int currentWindowIndex = player.getCurrentWindowIndex();
//                    return "(" + (currentWindowIndex + 1) + ")";
//                }
//
//                public PendingIntent createCurrentContentIntent(Player player) {
//                    Intent intent = new Intent(NotificationService.this, MainActivity.class);
//                    TaskStackBuilder create = TaskStackBuilder.create(NotificationService.this.getApplicationContext());
//                    create.addParentStack((Class<?>) MainActivity.class);
//                    create.addNextIntent(intent);
//                    Intent intent2 = new Intent(NotificationService.this, DetailActivity.class);
//                    intent2.putExtra("number", player.getCurrentWindowIndex() + 1);
//                    create.addNextIntent(intent2);
//                    return create.getPendingIntent(0, 134217728);
//                }
//
//                public CharSequence getCurrentContentText(Player player) {
//                    String lyricTitle = Utils.lyricTitle(player.getCurrentWindowIndex() + 1);
//                    return lyricTitle.substring(lyricTitle.indexOf(")") + 2);
//                }
//
//                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback bitmapCallback) {
//                    return decodeResource;
//                }
//            }, (PlayerNotificationManager.NotificationListener) new PlayerNotificationManager.NotificationListener() {
//                public void onNotificationCancelled(int i, boolean z) {
//                    NotificationService.this.stopService();
//                }
//
//                public void onNotificationPosted(int i, Notification notification, boolean z) {
//                    if (z) {
//                        NotificationService.this.startForeground(i, notification);
//                        DetailActivity.setPlayImage(R.drawable.ic_pause_btn);
//                        return;
//                    }
//                    NotificationService.this.stopForeground(false);
//                    DetailActivity.setPlayImage(R.drawable.ic_play_btn);
//                }
//            });
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat((Context) this, "Sai Mao");
            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder().build());
            playerNotificationManager.setMediaSessionToken(mediaSessionCompat.getSessionToken());
//            playerNotificationManager.setUseNavigationActions(true);
            playerNotificationManager.setUseNextAction(true);
            playerNotificationManager.setUseNextActionInCompactView(true);
            playerNotificationManager.setUsePlayPauseActions(true);
            playerNotificationManager.setUsePreviousAction(true);
            playerNotificationManager.setUsePreviousActionInCompactView(true);
//            playerNotificationManager.setRewindIncrementMs(0);
//            playerNotificationManager.setFastForwardIncrementMs(0);
            playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            playerNotificationManager.setPriority(NotificationCompat.PRIORITY_MAX);
            playerNotificationManager.setSmallIcon(R.drawable.noti);
            playerNotificationManager.setPlayer(exoPlayer);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (exoPlayer == null || playerNotificationManager != null) {
            return START_NOT_STICKY;
        }
        addPlayerToNotificationService();
        return START_NOT_STICKY;
    }
}
