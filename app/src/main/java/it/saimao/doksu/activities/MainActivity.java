package it.saimao.doksu.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import it.saimao.doksu.R;
import it.saimao.doksu.adapters.DokSuAdapter;
import it.saimao.doksu.databinding.ActivityMainBinding;
import it.saimao.doksu.utilities.DokSuTypefaceSpan;
import it.saimao.doksu.utilities.Utils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDokSuSongs();
        initPlayingFab();
        initListeners();
    }

    private void initListeners() {

        binding.fabPlaying.setOnClickListener(view -> gotoCurrentPlayingSongPage());
        binding.mAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });
    }

    private void initPlayingFab() {

        if (Utils.getPageNumber() == 0) {
            binding.fabPlaying.hide();
        } else {
            binding.fabPlaying.show();
            binding.fabPlaying.setText(String.valueOf(Utils.getPageNumber()));
        }
    }

    private void initDokSuSongs() {
        DokSuAdapter dokSuAdapter = new DokSuAdapter(Utils.lyricTitles(), songNumber -> {

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("number", songNumber);
            startActivity(intent);
        });
        binding.rvDokSuSongs.setAdapter(dokSuAdapter);

    }

    /* access modifiers changed from: private */
    public void gotoCurrentPlayingSongPage() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("number", Utils.getPageNumber());
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Utils.isReadMode(this)) {
            binding.fabPlaying.hide();
            return;
        }
        binding.fabPlaying.show();
        setCurrentSong(Utils.getPageNumber());
    }

    public void setCurrentSong(int i) {
        binding.fabPlaying.setText(String.valueOf(i));
    }
}
