package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.ozonetech.ozochat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OTPActivity extends AppCompatActivity {

    @BindView(R.id.otp_view)
    OtpTextView otpTextView;
    @BindView(R.id.countMsg)
    TextView countMsg;
    @BindView(R.id.textView)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        ButterKnife.bind(this);
        String a = "Waiting to automatically detect an SMS sent to 9999999999.";
        String b = "<font color='#6553e6'>wrong Number</font>";
        textView.setText(Html.fromHtml(a + b));

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                Toast.makeText(OTPActivity.this, "The OTP is " + otp,  Toast.LENGTH_SHORT).show();
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
}