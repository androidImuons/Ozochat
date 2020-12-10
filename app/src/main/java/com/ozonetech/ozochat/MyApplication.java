package com.ozonetech.ozochat;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.amitshekhar.DebugDB;
import com.downloader.PRDownloader;
import com.facebook.drawee.backends.pipeline.Fresco;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.ozonetech.ozochat.database.ChatDatabase;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;

import java.lang.reflect.Field;
import java.net.URISyntaxException;

import static com.ozonetech.ozochat.network.webservices.ServiceGenerator.SOCKETURL;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getName();

    private static MyApplication mInstance;
    private MyPreferenceManager pref;
    public Socket iSocket;
    public static ChatDatabase chatDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mInstance = this;
        chatDatabase = ChatDatabase.getInstance(this);
        try {

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;
            opts.multiplex = true;
            iSocket = IO.socket(SOCKETURL, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(TAG, "----exception--" + e.getMessage());
        }
        PRDownloader.initialize(getApplicationContext());

        Log.d("address","--address-"+DebugDB.getAddressLog());
    }


    public Socket getSocket() {
        if (iSocket.connected()) {
            Log.d(TAG, "----Socket connected--" + iSocket.connected());
        } else {
            Log.d(TAG, "----Socket not connected--" + iSocket.connected());

        }
        return iSocket;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }

    public boolean isDebugBuild() {

        try {
            String packageName = getPackageName();

            Bundle bundle = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA).metaData;
            String manifest_pkg = null;
            if (bundle != null) {
                manifest_pkg = bundle.getString("manifest_pkg", null);
            }
            if (manifest_pkg != null) {
                packageName = manifest_pkg;
            }
            final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
            final Field DEBUG = buildConfig.getField("DEBUG");
            DEBUG.setAccessible(true);
            return DEBUG.getBoolean(null);
        } catch (final Throwable t) {
            final String message = t.getMessage();
            if (message != null && message.contains("BuildConfig")) {
                return false;
            } else {
                return BuildConfig.DEBUG;
            }
        }
    }
/*
    {
        try {
            iSocket = IO.socket(CHAT_SERVER_URL);
            if (iSocket.connected()) {
                Log.d("----------check", "Socket connected " + mSocket.io().timeout());
            } else {
                Log.d("----------check", "Socket not connected " + mSocket.io().timeout());
            }
        } catch (URISyntaxException e) {
            Log.d("----------check", "Socket connection error : " + e.toString());
            throw new RuntimeException(e);
        }
    }
*/

}