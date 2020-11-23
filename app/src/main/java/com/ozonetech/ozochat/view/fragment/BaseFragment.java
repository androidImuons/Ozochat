package com.ozonetech.ozochat.view.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.network.SoketService;

public class BaseFragment extends Fragment implements SoketService.SocketConnectionListner {

    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        if (getActivity() != null) {
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SoketService.setSocketConnectionListner(this);
    }

    protected void showProgressDialog(String msg) {
        try {
            if (progressDialog == null ) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            if(!progressDialog.isShowing()) {
                progressDialog.setMessage(msg);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void openActivity(Bundle bundle, Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra("data", bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    @Override
    public void onSocketConnect(boolean flag) {

    }
}
