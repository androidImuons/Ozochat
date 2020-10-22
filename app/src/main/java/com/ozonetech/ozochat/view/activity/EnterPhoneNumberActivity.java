package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hbb20.CountryCodePicker;
import com.ozonetech.ozochat.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.country_category)
    Spinner country_category;
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
}