package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityProfileInfoBinding;
import com.ozonetech.ozochat.network.ConnectivityReceiver;

public class ProfileInfoActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {

    ActivityProfileInfoBinding databinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databinding= DataBindingUtil.setContentView(ProfileInfoActivity.this,R.layout.activity_profile_info);
        databinding.executePendingBindings();
        databinding.setLifecycleOwner(this);
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    public void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
            showSnackbar(databinding.clProfileInfo, message, Snackbar.LENGTH_SHORT);

        } else {
            message = "Sorry! Not connected to internet";
            showSnackbar(databinding.clProfileInfo, message, Snackbar.LENGTH_SHORT);

        }

    }

}