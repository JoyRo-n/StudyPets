package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LinearLayout llLogo   = findViewById(R.id.ll_logo);
        LinearLayout llLoading = findViewById(R.id.ll_loading);

        // Logo: fade + scale in dari tengah
        llLogo.setAlpha(0f);
        llLogo.setScaleX(0.7f);
        llLogo.setScaleY(0.7f);
        llLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                .start();

        // Loading bar: slide up setelah logo muncul
        llLoading.setAlpha(0f);
        llLoading.setTranslationY(40f);
        llLoading.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(600)
                .start();

        // Selalu arahkan ke LoginActivity — user harus login ulang setiap buka app
        new Handler().postDelayed(() -> {
            // Reset status login agar tidak skip ke MainActivity
            getSharedPreferences("studypets_user", MODE_PRIVATE)
                    .edit()
                    .putBoolean("is_logged_in", false)
                    .apply();

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

            // Transisi fade keluar
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2800);
    }
}
