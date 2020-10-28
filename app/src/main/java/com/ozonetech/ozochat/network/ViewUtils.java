package com.ozonetech.ozochat.network;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ozonetech.ozochat.R;


public class ViewUtils extends AppCompatActivity {
    private static Dialog alertDialogProgressBar;
    public static Dialog getProgressBar(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        dialog.setContentView(R.layout.progress_bar);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.dark_graw), PorterDuff.Mode.MULTIPLY);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
   /* public static Dialog popUp(Activity activity , boolean isCancel) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        dialog.setContentView(R.layout.pop_up);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }*/
    public static Dialog getBottomProgress(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        dialog.setContentView(R.layout.progress_bar);
        ProgressBar progressBar = dialog.findViewById(R.id.bottom_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

  /*  public static void displayProgressBar(Activity activity) {
        //dismissProgressBar();
        if (activity.isFinishing()) return;
        alertDialogProgressBar = new Dialog(activity,
                R.style.DialogWait);
        alertDialogProgressBar.setCancelable(false);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.progress_bar);


        try {
            alertDialogProgressBar.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            alertDialogProgressBar.show();

        } catch (Exception ignore) {

        }
    }*/

    // Dialog dialog = ViewUtils.getProgressBar(MainActivity.this);
    // dialog.dismiss();

    public void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
