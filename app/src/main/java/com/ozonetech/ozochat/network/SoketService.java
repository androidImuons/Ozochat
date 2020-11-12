package com.ozonetech.ozochat.network;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SoketService extends Service {
    private MyApplication signalApplication;

MyPreferenceManager myPreferenceManager;
    public static SoketService instance = null;
    private String tag="SoketService";

    public static boolean isInstanceCreated() {
        return instance == null ? false : true;
    }
    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SoketService getService() {
            return SoketService.this;
        }
    }

    public void IsBendable() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(isInstanceCreated()){
            return;
        }
        myPreferenceManager=new MyPreferenceManager(getApplicationContext());
        signalApplication=(MyApplication) getApplication();
        if (signalApplication.getSocket() == null)
            signalApplication.iSocket = signalApplication.getSocket();
        signalApplication.iSocket.on(Socket.EVENT_CONNECT, connection);
        signalApplication.iSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        signalApplication.iSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.iSocket.on("getMessages", message);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isInstanceCreated()) {
            return START_STICKY_COMPATIBILITY;
        }
        super.onStartCommand(intent, flags, startId);
        connectConnection();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        signalApplication.getSocket().off(Socket.EVENT_CONNECT, connection);
        signalApplication.getSocket().off(Socket.EVENT_DISCONNECT,onDisconnect );
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //@formatter:off
        signalApplication.getSocket().off("getMessages", message);
        //@formatter:on

        disconnectConnection();
    }

    private void connectConnection() {
        instance = this;
        signalApplication.getSocket().connect();
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
    }

    Emitter.Listener connection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                   // Log.d("socketid","---"+args[0]);
                    Log.d("socketid","---"+signalApplication.getSocket().id());

                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("setStatus","online");
                        jsonObject.put("user_id",myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signalApplication.getSocket().emit("updateStatus", jsonObject);
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("setStatus","offline");
                        jsonObject.put("user_id",myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signalApplication.getSocket().emit("updateStatus", jsonObject);
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("setStatus","offline");
                        jsonObject.put("user_id",myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signalApplication.getSocket().emit("updateStatus", jsonObject);
                }
            });
        }
    };


    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONArray result = (JSONArray) args[0];
            new Handler(getMainLooper())
                    .post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Log.d(tag, "---data--" + result.toString());
                                }
                            }

                    );
        }
    };
    Emitter.Listener chatList=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(getMainLooper())
                    .post(
                            new Runnable() {
                                @Override
                                public void run() {
                                     Log.d(tag, "---chatList--" + args[0]);
                                }
                            }

                    );
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}
