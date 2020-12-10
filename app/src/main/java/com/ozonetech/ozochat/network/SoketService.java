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
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.ozonetech.ozochat.MyApplication.chatDatabase;


public class SoketService extends Service {
    private MyApplication signalApplication;

    MyPreferenceManager myPreferenceManager;
    public static SoketService instance = null;
    private String tag = "SoketService";

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
        if (isInstanceCreated()) {
            return;
        }
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());
        signalApplication = (MyApplication) getApplication();
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
        socketConnectionListner = (SocketConnectionListner) instance;
        timer.scheduleAtFixedRate(new ConnectionTimer(), 0, 5000);
        connectConnection();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        signalApplication.getSocket().off(Socket.EVENT_CONNECT, connection);
        signalApplication.getSocket().off(Socket.EVENT_DISCONNECT, onDisconnect);
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.getSocket().off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //@formatter:off
        signalApplication.getSocket().off("getMessages", message);
        //@formatter:on

        disconnectConnection();
    }

    public void connectConnection() {
        instance = this;
        signalApplication.getSocket().connect();
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
        timer.cancel();
    }

    Emitter.Listener connection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // Log.d("socketid","---"+args[0]);
                    Log.d(tag, "- connection create --" + signalApplication.getSocket().id());
                    if (socketConnectionListner != null) {
                        socketConnectionListner.onSocketConnect(true);
                    }

                    JSONObject jsonObject = new JSONObject();
                    //checkNonSendMessage();
                    try {
                        jsonObject.put("setStatus", "online");
                        jsonObject.put("user_id", myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), DbService.class);
                    startService(intent);
                    signalApplication.getSocket().emit("updateStatus", jsonObject).
                            on("updateStatus", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(tag, "----update status on connection--");
                                }
                            });
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
                    Log.d(tag, "----error socket---" + args[0]);
                    if (socketConnectionListner != null) {
                        socketConnectionListner.onSocketConnect(false);
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("setStatus", "offline");
                        jsonObject.put("user_id", myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signalApplication.getSocket().emit("updateStatus", jsonObject).
                            on("updateStatus", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(tag, "----update status on connection error--");
                                }
                            });
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
                    if (socketConnectionListner != null) {
                        socketConnectionListner.onSocketConnect(false);
                    }
                    Log.d(tag, "----disconnect socket---");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("setStatus", "offline");
                        jsonObject.put("user_id", myPreferenceManager.getUserId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signalApplication.getSocket().emit("updateStatus", jsonObject).
                            on("updateStatus", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(tag, "----update status on diconnect--");
                                }
                            });
                }
            });
        }
    };


    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //  final JSONArray result = (JSONArray) args[0];
            new Handler(getMainLooper())
                    .post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(tag, "--message -data--" + args[0]);
                                }
                            }

                    );
        }
    };
    Emitter.Listener chatList = new Emitter.Listener() {
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

    static SocketConnectionListner socketConnectionListner;

    public static void setSocketConnectionListner(SocketConnectionListner connectionListner) {
        socketConnectionListner = connectionListner;
    }

    public interface SocketConnectionListner {
        public void onSocketConnect(boolean flag);
    }

    private static Timer timer = new Timer();

    public class ConnectionTimer extends TimerTask {

        @Override
        public void run() {
            if (signalApplication.iSocket.connected()) {
                Log.d(tag, "----connect");

            } else {
                Log.d(tag, "---reconetccet");
                connectConnection();
            }
        }
    }
    private void checkNonSendMessage() {

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT * FROM message");
        ArrayList<Message> unsendMsgLsit = new ArrayList<>();
        if (chatDatabase.getInstance(this).chatMessageDao().getGroupList(query).size() != 0) {
            unsendMsgLsit =
                    new ArrayList<Message>(chatDatabase.getInstance(this).chatMessageDao().getGroupList(query));
            sendUnSendMessage(unsendMsgLsit);
        }
    }

    private void sendUnSendMessage(ArrayList<Message> unsendMsgLsit) {
        for (int i = 0; i < unsendMsgLsit.size(); i++) {
            Message message = unsendMsgLsit.get(i);
            JSONObject jsonObject = new JSONObject();
            Log.d(tag, "-unsend message  :" + message.isStatus());
            try {
                jsonObject.put("sender_id", MyApplication.getInstance().getPrefManager().getUserId());
                jsonObject.put("user_id", message.getUserId());
                jsonObject.put("group_id", message.getGroupId());
                jsonObject.put("message", message.getMessage());
                Log.d(tag, "---send un message parameter-- user_id :" + jsonObject);
                if (!message.isStatus()) {
                    MyApplication.getInstance().getSocket().emit("sendMessage", jsonObject).on("sendMessage", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(tag, "--un-send message to get meesgae --" + args[0]);
                            updateunsendMessage(message);
                        }
                    });
                    updateunsendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateunsendMessage(Message message) {

        message.setStatus(true);
        chatDatabase.getInstance(this).chatMessageDao().update(message);
    }


}
