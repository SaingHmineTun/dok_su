package it.saimao.doksu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import it.saimao.doksu.adapters.DokSuAdapter;
import it.saimao.doksu.databinding.ActivityMainBinding;
import it.saimao.doksu.utilities.Utils;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDokSuSongs();
        initListeners();
    }

    private void initListeners() {

        binding.fabPlaying.setOnClickListener(view -> gotoCurrentPlayingSongPage());
        binding.mAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });
        Utils.playing.observeForever(isPlaying -> {
            if (isPlaying) {
                binding.fabPlaying.setVisibility(View.VISIBLE);
            } else {
                binding.fabPlaying.setVisibility(View.GONE);
            }
        });
        Utils.pageNumber.observeForever(pageNumber -> {
            binding.fabPlaying.setText(String.valueOf(pageNumber));
        });
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
        Log.d("Kham", "Main Activity On Restart");
        if (Utils.isReadMode(this)) {
            binding.fabPlaying.hide();
            return;
        }
        updateFabSongNumber(Utils.getPageNumber());
    }

    public void updateFabSongNumber(int i) {
        binding.fabPlaying.setText(String.valueOf(i));
    }
}
