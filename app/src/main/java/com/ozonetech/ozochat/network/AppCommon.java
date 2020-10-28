package com.ozonetech.ozochat.network;

/*
 * Created by Tabish on 06-05-2020.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.ozonetech.ozochat.R;

import static android.content.Context.MODE_PRIVATE;

public class AppCommon {

    public static AppCommon mInstance = null;

    static Context mContext;
    MediaPlayer mediaPlayer = null;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final int ENABLE_LOCATION_SERVICES_REQUEST_CODE = 205;

    public static AppCommon getInstance(Context _Context) {
        if (mInstance == null) {
            mInstance = new AppCommon();
        }
        mContext = _Context;
        return mInstance;
    }

    public void setNonTouchableFlags(Activity activity) {
        if (activity != null) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void clearNonTouchableFlags(Activity mActivity) {
        if (mActivity != null) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void setUserLogin(String userId, boolean b) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putBoolean(MyPreference.login, b);
        editor.putString(MyPreference.userId, userId);
        editor.apply();
    }

    public boolean isUserLogIn() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getBoolean(MyPreference.login, false);
    }

    public String getPassword() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.password, "");
    }
    public void setPassword(String password) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.password, password);
        editor.apply();
    }
    public int getId() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getInt(MyPreference.IsDemo, 0);
    }
    public void setId(int id) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putInt(MyPreference.IsDemo , id);
        editor.apply();
    }
    public boolean isSocial() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getBoolean(MyPreference.sound, true);
    }
    public String getSocialType() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.SocialType, "");
    }
    public void setSocial(boolean demo , String SocialType) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putBoolean(MyPreference.sound , demo);
        editor.putString(MyPreference.SocialType , SocialType);
        editor.apply();
    }

    public void onHideKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getUserId() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.userId, null);
    }

    public void setUserObject(String json) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.userObject, json);
        editor.apply();
    }
    public void setDeviceId(String json) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mDeviceId, MODE_PRIVATE).edit();
        editor.putString(MyPreference.userObject, json);
        editor.apply();
    }
    public String getDeviceId() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mDeviceId, MODE_PRIVATE);
        return prefs.getString(MyPreference.userObject, null);
    }

    public String getUserObject() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.userObject, null);
    }

    public void clearPreference() {
        SharedPreferences.Editor editorLogin = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editorLogin.clear();
        editorLogin.apply();

    }

    public void showDialog(Activity mactivity, String title) {
        if (!mactivity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mactivity);
            builder.setCancelable(false);
            builder.setTitle(mactivity.getResources().getString(R.string.app_name));
            builder.setMessage(title);
            builder.setCancelable(false);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }

    public int getAccount() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getInt(MyPreference.bidCount, 0);
    }

    public void setAccount(int count) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putInt(MyPreference.bidCount, count);
        editor.apply();
    }
    public int getSesstionId() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getInt(MyPreference.SeetionId, 0);
    }

    public void setSesstionId(int count) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putInt(MyPreference.SeetionId, count);
        editor.apply();
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.TokenValue, token);
        editor.apply();
    }


    public void setUID(String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.TokenValue, token);
        editor.apply();
    }

    public void setAddress(String token, String prizm) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.BTCAddress, token);
        editor.putString(MyPreference.PrizmAddress, prizm);
        editor.apply();

    }

    public String getAddress() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.BTCAddress, "");
    }

    public String getPrizmAddress() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.PrizmAddress, "");
    }

    public String getToken() {
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.TokenValue, "");
    }

    public DraweeController getDraweeController(SimpleDraweeView mImageView, String vendorImg, int i) {
        Uri uri = Uri.parse(vendorImg);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(i, i))
                .setProgressiveRenderingEnabled(false)
                .build();

        return Fresco.newDraweeControllerBuilder()
                .setOldController(mImageView.getController())
                .setImageRequest(request)
                .build();
    }



    public void IslangSelected(boolean flag) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putBoolean(MyPreference.PREFS_LANGUAGE_IS_SELECTED, flag);
        editor.apply();
    }
    public boolean  GetIsLangSelected(){
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getBoolean(MyPreference.PREFS_LANGUAGE_IS_SELECTED, false);
    }


    public void setSeleectedLan(String flag) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.language, MODE_PRIVATE).edit();
        editor.putString(MyPreference.SELECTEDlANG, flag);
        editor.apply();
    }
    public String GetLangSelected(){
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.language, MODE_PRIVATE);
        return prefs.getString(MyPreference.SELECTEDlANG, "");
    }

    public void setCurrentTime(String flag) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.CurentTime, flag);
        editor.apply();
    }
    public String getCurrentTime(){
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.CurentTime, "000");
    }
    public void storeNotificationObject(boolean flag) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putBoolean(MyPreference.notificationObj, flag);
        editor.apply();
    }
    public boolean  getNotificationObj(){
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getBoolean(MyPreference.notificationObj, true);
    }


    public void setGameToken(String gameToken) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE).edit();
        editor.putString(MyPreference.GameToken, gameToken);
        editor.apply();
    }
    public String getGameToken(){
        SharedPreferences prefs = mContext.getSharedPreferences(MyPreference.mUserLogin, MODE_PRIVATE);
        return prefs.getString(MyPreference.GameToken, null);
    }
}
