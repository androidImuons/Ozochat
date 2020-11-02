package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.LoginResponse;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.ViewUtils;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.country_category)
    Spinner country_category;
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.ll_login)
    RelativeLayout ll_login;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;;

    List<String> countries;
    HashMap<String, String> countryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);
        ButterKnife.bind(this);
        String[] locales = Locale.getISOCountries();
        countries = new ArrayList<>();
        countries.add("**Select Country**");

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            countries.add(obj.getDisplayCountry());
            countryMap.put(obj.getCountry(), obj.getDisplayCountry());

        }
        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        country_category.setAdapter(adapter);
    }

    public void next(View view) {
        AppCommon.getInstance(this).onHideKeyBoard(this);
        String mobNum = etPhoneNumber.getText().toString().trim();
        if(mobNum.length() == 10){
            callSendMobileNumber(mobNum);
        }else {
            etPhoneNumber.setError("Please enter valid mobile number");
        }
    }

    private void callSendMobileNumber(String mobNum) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            final Dialog dialog = ViewUtils.getProgressBar(EnterPhoneNumberActivity.this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppServices apiService = ServiceGenerator.createService(AppServices.class);
            Map<String, String> entityMap = new HashMap<>();
            entityMap.put("mobile", mobNum);
            Call call = apiService.EnterMobileApi(entityMap);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    AppCommon.getInstance(EnterPhoneNumberActivity.this).clearNonTouchableFlags(EnterPhoneNumberActivity.this);
                    dialog.dismiss();
                    LoginResponse authResponse = (LoginResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getStatus() == true) {
                            showSnackbar(ll_login,authResponse.getMessage(), Snackbar.LENGTH_SHORT);
                            startActivity(new Intent(EnterPhoneNumberActivity.this, OTPActivity.class)
                            .putExtra("mobile" , mobNum));

                        } else {
                            showSnackbar(ll_login,authResponse.getMessage(),Snackbar.LENGTH_SHORT);
                        }
                    } else {
                        AppCommon.getInstance(EnterPhoneNumberActivity.this).showDialog(EnterPhoneNumberActivity.this, authResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    AppCommon.getInstance(EnterPhoneNumberActivity.this).clearNonTouchableFlags(EnterPhoneNumberActivity.this);

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