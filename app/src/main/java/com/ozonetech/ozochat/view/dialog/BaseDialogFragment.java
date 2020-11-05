package com.ozonetech.ozochat.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ozonetech.ozochat.view.fragment.BaseFragment;


public abstract class BaseDialogFragment extends DialogFragment {


    private FragmentManager childFm;
    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childFm = getChildFragmentManager();
        if (view == null) {
            setupFragmentViewByResource(inflater, container);
            initializeComponent();
        }

        return view;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        // wlmp.gravity = Gravity.LEFT | Gravity.TOP;
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(this.getActivity(), this.getTheme()) {
            @Override
            public void onBackPressed() {
                if (handleOnBackPress()) return;
                super.onBackPressed();
            }

            @Override
            public void cancel() {
                if (handleOnCancel()) return;
                super.cancel();
            }
        };
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void setupFragmentViewByResource(LayoutInflater inflater, @Nullable ViewGroup container) {

        view = inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }


    public boolean handleOnBackPress() {
        if (childFm.getBackStackEntryCount() > 0) {
            childFm.popBackStackImmediate();
            return true;
        }
        return false;
    }


    public boolean handleOnCancel() {
        return false;
    }


    public abstract int getLayoutResourceId();

    public abstract void initializeComponent();

    public abstract int getFragmentContainerResourceId(BaseFragment baseFragment);

    public FragmentManager getChildFm() {
        return childFm;
    }

    public FragmentTransaction getNewChildFragmentTransaction() {
        return childFm.beginTransaction();
    }
    public void clearFragmentBackStack(int pos) {
        if (childFm.getBackStackEntryCount() > pos) {
            try {
                childFm.popBackStackImmediate(childFm.getBackStackEntryAt(pos).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {
            }
        }
    }
}
