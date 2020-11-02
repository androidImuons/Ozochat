package com.ozonetech.ozochat;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;

import java.net.URISyntaxException;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getName();

    private static MyApplication mInstance;
    private MyPreferenceManager pref;
    Socket iSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mInstance = this;

        try {

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;
            opts.multiplex = true;
            iSocket = IO.socket("http://3.0.49.131/", opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(TAG, "----exception--" + e.getMessage());
        }
    }

    public Socket getSocket() {
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