package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.OTPResponse;
import com.ozonetech.ozochat.model.User;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.ContactDBService;
import com.ozonetech.ozochat.network.ViewUtils;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    MyPreferenceManager prefManager;

    @BindView(R.id.otp_view)
    OtpTextView otpTextView;
    @BindView(R.id.countMsg)
    TextView countMsg;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.verify_num)
    RelativeLayout ll_login;

    String mob = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        ButterKnife.bind(this);
        prefManager=new MyPreferenceManager(OTPActivity.this);


        if(getIntent()!= null){
            mob = getIntent().getStringExtra("mobile");
            String a = "Waiting to automatically detect an SMS sent to "+mob;
            String b = "<font color='#6553e6'> Wrong Number</font>";
            textView.setText(Html.fromHtml(a + b));
        }

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                Toast.makeText(OTPActivity.this, "The OTP is " + otp,  Toast.LENGTH_SHORT).show();
                callOtpApi(otp);
            }
        });
        new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countMsg.setText("00 : "+String.valueOf(millisUntilFinished/1000));

            }
            @Override
            public void onFinish() {
                countMsg.setText("RESEND");
            }
        }.start();
    }

    private void callOtpApi(String otp) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            final Dialog dialog = ViewUtils.getProgressBar(OTPActivity.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppServices apiService = ServiceGenerator.createService(AppServices.class);
            Map<String, String> entityMap = new HashMap<>();
            entityMap.put("mobile", mob);
            entityMap.put("otp", otp);
            Call call = apiService.verifyOtpCall(entityMap);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(OTPActivity.this).clearNonTouchableFlags(OTPActivity.this);
                    dialog.dismiss();
                    OTPResponse authResponse = (OTPResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", "---OTP RESPONSE --"+new Gson().toJson(authResponse));
                        if (authResponse.getSuccess() == true) {

                            showSnackbar(ll_login,authResponse.getMessage(), Snackbar.LENGTH_SHORT);
                            if(authResponse.getUser()!= null){

                                User user= new User(String.valueOf(authResponse.getUser().getUid()),authResponse.getUser().getUsername(),authResponse.getUser().getMobile(),
                                        authResponse.getUser().getProfilePic(),authResponse.getUser().getOtpVerify(),authResponse.getUser().getDeviceId(),
                                        authResponse.getUser().getIsLogin(),authResponse.getUser().getToken());
                                prefManager.storeUser(user);
//                                startActivity(new Intent(OTPActivity.this, MainActivity.class)
//                                        .putExtra("userData" , new Gson().toJson(authResponse.getUser())));
                                Intent intent=new Intent(OTPActivity.this, ContactDBService.class);
                                startService(intent);
                                startActivity(new Intent(OTPActivity.this, ProfileInfoNew.class)
                                        .putExtra("userData" , new Gson().toJson(authResponse.getUser())));

                            }else {

                            startActivity(new Intent(OTPActivity.this, ProfileInfoNew.class)
                                    .putExtra("userData" , new Gson().toJson(authResponse.getUser())));
//                                startActivity(new Intent(OTPActivity.this, MainActivity.class)
//                                        .putExtra("userData" , new Gson().toJson(authResponse.getUser())));
                            }
                            finishAffinity();
                            AppCommon.getInstance(OTPActivity.this).setUserObject(new Gson().toJson(authResponse.getUser()));
                            AppCommon.getInstance(OTPActivity.this).setUserLogin(String.valueOf(authResponse.getUser().getUid()),true);


                        } else {
                            showSnackbar(ll_login,authResponse.getMessage(),Snackbar.LENGTH_SHORT);
                        }
                    } else {
                        AppCommon.getInstance(OTPActivity.this).showDialog(OTPActivity.this, authResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(OTPActivity.this).clearNonTouchableFlags(OTPActivity.this);

                    showSnackbar(ll_login,getResources().getString(R.string.ServerError),Snackbar.LENGTH_SHORT);
                }
            });


        } else {
            showSnackbar(ll_login,getResources().getString(R.string.NoInternet),Snackbar.LENGTH_SHORT);

        }
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }
}