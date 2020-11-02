package com.ozonetech.ozochat;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.engineio.client.transports.Polling;
//import com.github.nkzawa.engineio.client.transports.WebSocket;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketOptions;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private MyPreferenceManager pref;
    public Socket iSocket;
    private String tag = "Myapp";

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
            Log.d(tag, "----exception--" + e.getMessage());
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
}