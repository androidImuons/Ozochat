package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.ClientWebSocket;
import com.ozonetech.ozochat.network.ContactDBService;
import com.ozonetech.ozochat.network.SoketService;

//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_10;

import java.net.URI;
import java.net.URISyntaxException;

public class SplashActivity extends BaseActivity {
    private static final long SPLASH_TIME_MS = 2000;
    private Handler handler = new Handler();
    private SoketService mBoundService;
    private boolean mIsBound;
    TextView textView;

    //  WebSocketClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler();
        textView = (TextView) findViewById(R.id.txt_ozochat);

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

                    if (checkGoogleService()) {
                        if (AppCommon.getInstance(SplashActivity.this).isUserLogIn()) {

                            Intent intent=new Intent(SplashActivity.this, ContactDBService.class);
                            startService(intent);
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, WelcomeScreen.class));
                        }
                        finish();
                    } else {
                        Log.d("SplashActivity", "---google service not avail..");
                    }
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

    public boolean checkGoogleService() {
        GoogleApiAvailability isavailable = GoogleApiAvailability.getInstance();
        int status = isavailable.isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            if (isavailable.isUserResolvableError(status)) {
                isavailable.getErrorDialog(this, status, 1425).show();
                Log.d("SplashActivity", "---google service not avail.1.");
            }
            return false;
        } else {
            Log.d("SplashActivity", "---google service  avail..2");
            return true;
        }
    }




}