package com.ozonetech.ozochat.view.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.FragmentStatusBinding;
import com.ozonetech.ozochat.utils.MyPreferenceManager;


public class StatusFragment extends Fragment {

    FragmentStatusBinding binding;
    MyPreferenceManager myPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_status,container,false);
       View view = binding.getRoot();
       binding.setLifecycleOwner(this);
        myPreferenceManager = new MyPreferenceManager(getActivity());
        renderView();
       return view;
    }

    private void renderView() {
        if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC) != null) {
            Log.d("image url", "--details-" + (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC)));
            Glide.with(getActivity())
                    .load(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC))
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.person_icon)
                    .into(binding.thumbnail);
        }

        binding.llMyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(getActivity(), Options.init().setRequestCode(100));
            }
        });
    }
}