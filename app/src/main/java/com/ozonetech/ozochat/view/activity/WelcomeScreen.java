package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeScreen extends BaseActivity {

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
        Spannable spannable = (Spannable) text.getText();
        //Spannable spannable = new SpannableString("Read Privacy Policy Tap Agree and Continue to accept the Terms of service");
        //Spannable spannable = (Spannable) s;
        spannable.setSpan(new ForegroundColorSpan(Color.GREEN), 5, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.GREEN), 57, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //text.setText();

        //text.setText(Html.fromHtml(a + b))
        requestContactPermission();
    }

    public void next(View view) {
        startActivity(new Intent(this, EnterPhoneNumberActivity.class));
    }


    Cursor phones;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                showContacts();
            }
        } else {
            showContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                showSnackbar(text, "Until you grant the permission, we canot display the names", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//            SelectContactActivity.LoadContact loadContact = new SelectContactActivity.LoadContact();
//            loadContact.execute();
        }
    }
}