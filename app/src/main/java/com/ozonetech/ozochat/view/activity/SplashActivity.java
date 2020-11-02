package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.ClientWebSocket;
import com.ozonetech.ozochat.network.SoketService;

//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_10;

import java.net.URI;
import java.net.URISyntaxException;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_MS = 2000;
    private Handler handler = new Handler();
    private SoketService mBoundService;
    private boolean mIsBound;

    //  WebSocketClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler();

        if (MyApplication.getInstance().getSocket() != null) {
            Log.e("Socket", " is null");
            startService(new Intent(getBaseContext(), SoketService.class));
            doBindService();
        }

    }
    private void handler() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    if (AppCommon.getInstance(SplashActivity.this).isUserLogIn()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, WelcomeScreen.class));
                    }
                    finish();
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    protected ServiceConnection socketConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SoketService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    private void doBindService() {
        if (mBoundService != null) {
            bindService(new Intent(SplashActivity.this, SoketService.class), socketConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
            mBoundService.IsBendable();
        }
    }


}