package com.ozonetech.ozochat.view.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.FragmentCallsBinding;


public class CallsFragment extends Fragment {

    FragmentCallsBinding dataBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_calls,container,false);
        View view=dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        return view;
    }
}