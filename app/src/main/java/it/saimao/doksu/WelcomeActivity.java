package it.saimao.doksu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/* renamed from: it.saimao.doksu.WelcomeActivity */
public class WelcomeActivity extends AppCompatActivity {
    Handler handler;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView((int) R.layout.activity_welcome);
        Handler handler2 = new Handler();
        this.handler = handler2;
        handler2.postDelayed(() -> {
            WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            WelcomeActivity.this.finish();
        }, 1000);
        Utils.initAllMediaItems();
    }
}
