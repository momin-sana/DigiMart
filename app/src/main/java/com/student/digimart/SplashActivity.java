package com.student.digimart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    private static final int SPLASH_SCREEN_TIMEOUT = 5000; // 3 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.enableMergePathsForKitKatAndAbove(true);
        lottieAnimationView.animate().setDuration(2000).setStartDelay(2900);

        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(loginIntent);
            finish();
        },SPLASH_SCREEN_TIMEOUT);

    }
}