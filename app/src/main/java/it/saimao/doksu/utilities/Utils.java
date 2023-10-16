package it.saimao.doksu.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import androidx.annotation.OptIn;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.SimpleExoPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import it.saimao.doksu.R;

public class Utils {

    private static int playingSong;
    private static final int[] mediaItemsID = {R.raw.tmk1, R.raw.tmk2, R.raw.tmk3, R.raw.tmk4, R.raw.tmk5, R.raw.tmk6, R.raw.tmk7, R.raw.tmk8, R.raw.tmk9, R.raw.tmk10, R.raw.tmk11, R.raw.tmk12, R.raw.tmk13, R.raw.tmk14, R.raw.tmk15, R.raw.tmk16, R.raw.tmk17, R.raw.tmk18, R.raw.tmk19, R.raw.tmk20, R.raw.tmk21, R.raw.tmk22, R.raw.tmk23, R.raw.tmk24, R.raw.tmk25, R.raw.tmk26, R.raw.tmk27, R.raw.tmk28, R.raw.tmk29, R.raw.tmk30, R.raw.tmk31, R.raw.tmk32, R.raw.tmk33, R.raw.tmk34, R.raw.tmk35, R.raw.tmk36};
    private static final String[] titles = {"ၵႃႈပၼ်ႇၵွင်ႊ", "ၵႂၢမ်းၶွပ်ႈၸႂ်", "ၵႂၢမ်းၸူမ်းပီႈၼွင့် (1)", "ၵႂၢမ်းၸူမ်းပီႈၼွင့် (2)", "ၶိူဝ်းသိူဝ်လၢႆး", "ၶတ်းၸႂ်ႁႂ်ႈမႂ်ႇသုင်", "ၶိုၼ်းဢွမ်ႇၸႂ်ႁပ့်ပီႊမႂ်ႇ", "ၶွပ်ႈၶိုၼ်းပီႊမႂ်ႇ", "သဵင်သၢႆၸႂ်ပီႊမႂ်ႇ", "သဵင်ပၢႆးမွၼ်းတူၵ်းသူး", "တႆးႁူပ့်ထူပ်းၵၼ်", "တူၵ်းသူးမၢႆ (1)", "တူၵ်းသူႈမၢႆ (2)", "တူၵ်းသူးမၢႆ (3)", "တူၵ်းသူးသၢႆၸႂ်ၸိူဝ့်", "တၢင်းႁၢင်ႊလီၽၵ်းတူၸႂ်", "ထုင်းၾိင်ႈယဵၼ်ႇငႄႈသၢႆၸႂ်တႆး", "ပီႊမႂ်ႇတႆး (1)", "ပီႊမႂ်ႇတႆး (2)", "ပီႊမႂ်ႇတႆး (3)", "ပွႆးလိူၼ်ၸဵင်ပီႊမႂ်ႇတႆး", "ပဵၼ်ၸႂ်လဵဝ်ၵၼ်ႁဝ်း", "ၽွင်းၶၢဝ်းၵတ်းပီႊမႂ်ႇ", "မႂ်ႇသုင်", "ယႃႇႁႂ်ႈဢဵၼ်ႁႅင်းႁၢႆ", "ယွၼ်းသူးထိုင်ၸဝ်ႈႁိူၼ်း", "ယွၼ်းသူးပၼ်ၸဝ်ႈႁိူၼ်း", "ယွၼ်းသူးႁႂ်ႈမႂ်ႇသုင်", "ယွၼ်းတူၵ်းသူး", "ႁူႉလႄႈၸၢႆးယိင်းတႆး", "ႁဝ်းၼမ်ႁဝ်းလီ ပႂ်ႉႁပ့်ပီႊမႂ်ႇ", "ႁဝ်းၽွမ့်ၵၼ်", "ႁဝ်းဝႅင်းၵႃႈပၼ်ႇၵွင်ႊ", "ႁႂ်ႈယူႇလီမီးငိုၼ်း", "ႁူမ်ႈႁႅင်းပီႊမႂ်ႇတႆး", "ဢွၼ်ၵၼ်ၶတ်းၸႂ်"};


    public static int getPlayingSong() {
        return playingSong;
    }

    public static void setPlayingSong(int i) {
        playingSong = i;
    }

    public static boolean isReadMode(Context context) {
        return context.getSharedPreferences("mao", 0).getBoolean("read_mode", false);
    }

    public static void setReadMode(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences("mao", 0).edit();
        edit.putBoolean("read_mode", z);
        edit.commit();
    }

    public static String[] lyricTitles() {
        return titles;
    }

    public static int[] getMediaItemsID() {
        return mediaItemsID;
    }

    public static String lyricTitle(int i) {
        return "(" + i + ") " + titles[i - 1];
    }

    public static String readLyricsWithChords(Context context, int pageNumber) {
        AssetManager assets = context.getAssets();
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assets.open("lyrics/tmk" + pageNumber + ".txt")));
            int i2 = 0;
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                if (i2 > 0) {
                    sb.append(readLine + "\n");
                }
                i2++;
            }
        } catch (IOException unused) {
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public static String readLyricsOnly(Context context, int pageNumber) {
        AssetManager assets = context.getAssets();
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assets.open("lyrics/tmk" + pageNumber + ".txt")));
            int i2 = 0;
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) break;
                if (readLine.matches("(\\s*([A-GO](#|b|m|bm|7)?\\s*)\\s*)+")) {
                    i2++;
                    continue;
                }

                if (i2 > 0) {
                    sb.append(readLine).append("\n");
                }
                i2++;
            }
        } catch (IOException unused) {
        }
        sb.append("\n\n");
        return sb.toString();
    }


    public static String formatToMinuteSeconds(long totalSecs) {
        long minutes = (totalSecs / 1000) / 60;
        long seconds = (totalSecs / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static Typeface getAjKunheingFont(Context context) {
        return ResourcesCompat.getFont(context, R.font.aj_kunheing);
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static boolean showChord;

    public static boolean isShowChord(Context context) {
        return context.getSharedPreferences("mao", 0).getBoolean("show_chord", false);
    }

    public static void setShowChord(Context context, boolean showChord) {
        SharedPreferences.Editor edit = context.getSharedPreferences("mao", 0).edit();
        edit.putBoolean("show_chord", showChord);
        edit.commit();
    }
}