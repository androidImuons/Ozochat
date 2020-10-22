package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.ozonetech.ozochat.R;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_MS = 2000;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}