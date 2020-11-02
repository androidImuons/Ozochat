package com.ozonetech.ozochat;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.network.Constants;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.net.URISyntaxException;

import static com.ozonetech.ozochat.network.Constants.CHAT_SERVER_URL;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private MyPreferenceManager pref;
    private Socket mSocket;


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

    {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
            if (mSocket.connected()) {
                Log.d("----------check", "Socket connected " + mSocket.io().timeout());
            } else {
                Log.d("----------check", "Socket not connected " + mSocket.io().timeout());
            }
        } catch (URISyntaxException e) {
            Log.d("----------check", "Socket connection error : " + e.toString());
            throw new RuntimeException(e);
        }
    }


    public Socket getSocket() {
        return mSocket;
    }
}