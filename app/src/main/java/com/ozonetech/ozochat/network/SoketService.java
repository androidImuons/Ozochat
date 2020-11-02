package com.ozonetech.ozochat.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
//
//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class SoketService extends Service {
    //private Socket iSocket;
    private String tag="SoketService";

    @Override
    public void onCreate() {
        super.onCreate();
//        IO.Options opts = new IO.Options();
////            opts.query = "auth_token=" + authToken;
//        try {
//            iSocket = IO.socket("http://3.0.49.131/socket.io/");
//           // iSocket.connect();
//            //checkSocketConnection();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

//    private void checkSocketConnection() {
//        if(iSocket.connected()){
//            Log.d(tag,"----connected--");
//        }else{
//            Log.d(tag,"---not-connected--");
//
//        }
//
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
