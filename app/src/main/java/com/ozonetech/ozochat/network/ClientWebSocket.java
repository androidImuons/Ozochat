package com.ozonetech.ozochat.network;

import android.provider.Telephony;
import android.util.Log;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_10;
//import org.java_websocket.handshake.ServerHandshake;

import java.net.Socket;
import java.net.URI;

public class ClientWebSocket //extends WebSocketClient//
         {
    private String tag="ClientWeb";

    public ClientWebSocket(URI serverURI) {
       // super(serverURI);

    }

//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//        Log.d(tag,"----onopen--"+handshakedata.getHttpStatusMessage());
//    }
//
//    @Override
//    public void onMessage(String message) {
//        Log.d(tag,"----onMessage--"+message);
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        Log.d(tag,"----onClose--"+reason);
//    }
//
//    @Override
//    public void onError(Exception ex) {
//        Log.d(tag,"----onError--"+ex.getMessage());
//    }
}
