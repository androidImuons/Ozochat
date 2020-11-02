package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
  //  WebSocketClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler();
//        try {
//            client=new ClientWebSocket(new URI("http://3.0.49.131/socket.io/"),new Draft_10());
//            client.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        startService(new Intent(getApplicationContext(), SoketService.class));

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
}