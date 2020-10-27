package com.ozonetech.ozochat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private MyPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mInstance = this;
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
}