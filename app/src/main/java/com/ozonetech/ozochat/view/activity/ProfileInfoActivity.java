package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityProfileInfoBinding;
import com.ozonetech.ozochat.network.ConnectivityReceiver;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.dialog.EditDialog;
import com.ozonetech.ozochat.view.dialog.EditDialog.SendCallBack;
import com.ozonetech.ozochat.viewmodel.ProfileInfoViewModel;

public class ProfileInfoActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ActivityProfileInfoBinding databinding;
    MyPreferenceManager myPreferenceManager;
    ProfileInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databinding = DataBindingUtil.setContentView(ProfileInfoActivity.this, R.layout.activity_profile_info);
        databinding.executePendingBindings();
        databinding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(ProfileInfoActivity.this).get(ProfileInfoViewModel.class);
        databinding.setProfile(viewModel);
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());
        //  checkConnection();

        databinding.toolbarView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        databinding.toolbarView.toplbarText.setText("Setting");
        onClick();
    }
    private void onClick() {
        databinding.llProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileInfoActivity.this, ProfileUpdateActivity.class));
            }
        });

    }

    private void setProfile() {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(getResources().getColor(R.color.colorBlack), 1.0f);
        roundingParams.setRoundAsCircle(true);
        databinding.sdvImage.getHierarchy().setRoundingParams(roundingParams);

        if (myPreferenceManager != null) {
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME) != null && !myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME).equals("")) {
                databinding.txtName.setText(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME));
            }
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC) != null) {
                Log.d("image url", "--details-" + (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC)));
                databinding.sdvImage.setImageURI(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC));
            }
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_ABOUT_US) != null) {
                databinding.txtAboutUs.setText(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_ABOUT_US));
            }
        } else {
            Log.d("details", "--details-null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        setProfile();
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