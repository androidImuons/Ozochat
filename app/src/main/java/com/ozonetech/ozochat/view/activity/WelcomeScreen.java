package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.ozonetech.ozochat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeScreen extends AppCompatActivity {

    @BindView(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom_screen);
        ButterKnife.bind(this);
        String a = "Read";
        String a2 = "Privacy";
        String b = "<font color='#6553e6'>wrong Number</font>";
//String s =new String();
//s ="" +text.getText();
text.setText(getString(R.string.policy), TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable)text.getText();
        //Spannable spannable = new SpannableString("Read Privacy Policy Tap Agree and Continue to accept the Terms of service");
        //Spannable spannable = (Spannable) s;
       spannable.setSpan(new ForegroundColorSpan(Color.GREEN) ,5,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       spannable.setSpan(new ForegroundColorSpan(Color.GREEN) ,57,73,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //text.setText();

        //text.setText(Html.fromHtml(a + b))
    }
}