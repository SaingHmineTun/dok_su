package it.saimao.doksu.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import it.saimao.doksu.databinding.ActivityAboutUsBinding;


public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListeners();
    }

    private void initListeners() {

        binding.mAboutUs.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.lySongsBy.setOnClickListener(v -> onItemClick(0));
        binding.lyEmail.setOnClickListener(v -> onItemClick(1));
        binding.lyFacebook.setOnClickListener(v -> onItemClick(2));
        binding.lyPlayStore.setOnClickListener(v -> onItemClick(3));
    }

    public void onItemClick(int index) {
        Intent intent;
        if (index == 2) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("fb://page/529740996878692"));
            } catch (Exception unused) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/529740996878692"));
            }
            startActivity(intent);
        } else if (index == 1) {
            Intent intent2 = new Intent("android.intent.action.SEND");
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{"tmk.muse@gmail.com"});
            intent2.setType("message/rfc822");
            startActivity(Intent.createChooser(intent2, "Choose an Email client :"));
        } else if (index == 0) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("fb://page/103066969300092"));
            } catch (Exception unused) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/103066969300092"));
            }
            startActivity(intent);
        } else if (index == 3) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=it.saimao.doksu")));
        }
    }
}
