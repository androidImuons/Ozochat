package com.ozonetech.ozochat.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ozonetech.ozochat.R;


public abstract  class AppBaseDialog extends BaseDialogFragment {
    private Dialog alertDialogProgressBar;
    @Override
    public void initializeComponent() {


    }

    public void displayProgressBar(boolean isCancellable) {
        displayProgressBar(isCancellable, "Please Wait...!");
    }

    public void displayProgressBar(boolean isCancellable, String loaderMsg) {
        dismissProgressBar();
        if (getActivity() == null) return;
        alertDialogProgressBar = new Dialog(getActivity(),
                R.style.DialogWait);
        alertDialogProgressBar.setCancelable(isCancellable);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.dialog_wait);
        TextView tv_loader_msg = alertDialogProgressBar.findViewById(R.id.tv_loader_msg);
        if (loaderMsg != null && !loaderMsg.trim().isEmpty()) {
            tv_loader_msg.setText(loaderMsg);
        } else {
            tv_loader_msg.setVisibility(View.GONE);
        }

        try {
            alertDialogProgressBar.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            alertDialogProgressBar.show();
        } catch (Exception ignore) {

        }
    }
    public void dismissProgressBar() {
        try {
            if (getActivity() != null && alertDialogProgressBar != null && alertDialogProgressBar.isShowing()) {
                alertDialogProgressBar.dismiss();
            }
        } catch (Exception ignore) {

        }
    }


}
