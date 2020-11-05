package com.ozonetech.ozochat.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.fragment.BaseFragment;


public class EditDialog extends AppBaseDialog {
    public TextView iv_send;
    public SendCallBack sendCallBack;
    private MyPreferenceManager sessionManager;

    public interface SendCallBack {
        public void sendMessage(String msg, String pos);

        public void sendError(String err);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_edit;
    }

    public static EditDialog getInstance(Bundle bundle) {
        EditDialog dialog = new EditDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
        return 0;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.dimAmount = 0.8f;
        sessionManager = new MyPreferenceManager(getContext());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout bottom_sheet;

    CardView cv_data;
    public ImageView iv_profile;
    public EditText et_comment;

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        bottom_sheet = getView().findViewById(R.id.bottom_sheet);
        initializeBottomSheet();
        cv_data = getView().findViewById(R.id.cv_data);
        et_comment = getView().findViewById(R.id.input_name);
        iv_send = getView().findViewById(R.id.txt_save);
        updateBottomMessage();
        et_comment.setText(getName());
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_comment.getText().toString().isEmpty()) {

                    sendCallBack.sendMessage(et_comment.getText().toString(), getStatus());
                    dismiss();
                } else {
                    sendCallBack.sendError("Enter Here...!");
                }
            }
        });


    }

    private String getName() {
        Bundle extras = getArguments();
        return (extras == null ? "" : extras.getString("name", ""));
    }

    private String getStatus() {
        Bundle extras = getArguments();
        return (extras == null ? "" : extras.getString("param", ""));
    }

    private void updateBottomMessage() {

    }

    private void initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
