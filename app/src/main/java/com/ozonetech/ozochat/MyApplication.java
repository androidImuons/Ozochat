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
    private Socket iSocket;
    private String tag = "Myapp";

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mInstance = this;

        try {
//            IO.Options options = new IO.Options();
//            options.transports = new String[] { WebSocket.NAME, Polling.NAME};
            iSocket = IO.socket("http://3.0.49.131:8080/socket.io/");
          //  iSocket = IO.socket("http://3.0.49.131:8080/socket.io/");
           iSocket.connect();
            //iSocket.open();
            checkSocketConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(tag, "----exception--" + e.getMessage());
        }
    }

    private void checkSocketConnection() {
        if (iSocket.connected()) {
          Log.d(tag, "----connected--");
        } else {
         Log.d(tag, "---not-connected--"+iSocket.id());
            Log.d(tag, "---not-connected--"+iSocket.connected());


        }
    }

//    private void checkSocketConnection() {
//        iSocket.on(iSocket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.d(tag, "----connected--"+args[0]);
//            }
//        });
//        if (iSocket.connected()) {
//            Log.d(tag, "----connected--");
//        } else {
//            Log.d(tag, "---not-connected--");
//
//        }
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("message", "Rahul Message");
//            jsonObject.put("user_id", 85);
//            iSocket.emit("sendMessage", jsonObject);
//
//            iSocket.on(iSocket.EVENT_MESSAGE, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    JSONObject data = (JSONObject) args[0];
//                    Log.d(tag, "---data--" + data.toString());
//
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

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