package com.ozonetech.ozochat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ozonetech.ozochat.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "ozochat";
    private static final String IS_LOGIN = "IsLoggedIn";

    // All Shared Preferences Keys
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_MOBILE = "user_mobile";
    public static final String KEY_PROFILE_PIC = "user_profile_pic";
    public static final String KEY_OTP_VERIFY = "otp_verify";
    public static final String KEY_DEVICE_ID = "user_device_id";
    public static final String KEY_IS_LOGIN = "user_is_login";
    public static final String KEY_TOKEN = "token";

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(User user) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_USER_ID, String.valueOf(user.getUid()));
        editor.putString(KEY_USER_NAME, user.getUsername());
        editor.putString(KEY_USER_MOBILE, user.getMobile());
        editor.putString(KEY_PROFILE_PIC, user.getProfilePic());
        editor.putString(KEY_OTP_VERIFY, user.getOtpVerify());
        editor.putString(KEY_DEVICE_ID, user.getDeviceId());
        editor.putString(KEY_IS_LOGIN, user.getIsLogin());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.commit();
        Log.e(TAG, "User is stored in shared preferences. " + user.getUsername());
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
        user.put(KEY_USER_MOBILE, pref.getString(KEY_USER_MOBILE, null));
        user.put(KEY_PROFILE_PIC, pref.getString(KEY_PROFILE_PIC, null));
        user.put(KEY_OTP_VERIFY, pref.getString(KEY_OTP_VERIFY, null));
        user.put(KEY_DEVICE_ID, pref.getString(KEY_DEVICE_ID, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }


    //{user_id:85,message:"your message"}
    public String getSendMessageJSON(String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("user_id", getUserId());
            jObj.put("message", message);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public String getMessageJSON() {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("user_id", getUserId());

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    public void clear() {
        editor.clear();
        editor.commit();
    }
}
