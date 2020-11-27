package com.ozonetech.ozochat.view.dialog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.utils.DeviceScreenUtil;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.SelectContactActivity;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.fragment.BaseFragment;
import com.ozonetech.ozochat.view.fragment.ChatsFragment;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static android.app.Activity.RESULT_OK;

public class MoreOptionDialog extends AppBaseFragment implements ContactsListener {
    public UnfollowUser unfollowUser;
    private int PERMISSIONS_REQUEST_WRITE_CONTACTS = 200;
    private int flag;
    private String tag = "MoreOptionDialog";

    public static MoreOptionDialog getInstance(Bundle bundle) {
        MoreOptionDialog messageDialog = new MoreOptionDialog();
        messageDialog.setArguments(bundle);

        return messageDialog;
    }

    private String getname() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("name", "0"));
    }

    private String getgroup() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("group", "0"));
    }

    private String getuId() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("u_id", "0"));
    }

    private String getnumber() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("mobile", "0"));
    }

    private boolean getIsContact() {
        Bundle extras = getArguments();
        return (extras == null ? false : extras.getBoolean("is_contact", false));
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_more_option;
    }

    @Override
    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
        return 0;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = DeviceScreenUtil.getInstance().getWidth(0.90f);
        wlmp.dimAmount = 0.8f;
    }

    TextView txt_add_to_contact;
    TextView txt_add_to_exist_contact;
    TextView txt_message;
    TextView txt_video;
    TextView txt_voice;
    TextView txtUnfollow;

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        txt_add_to_contact = getView().findViewById(R.id.txt_add_to_contact);
        txt_add_to_exist_contact = getView().findViewById(R.id.txt_add_to_exist_contact);
        txt_message = getView().findViewById(R.id.txt_message);
        txt_video = getView().findViewById(R.id.txt_video_call);
        txt_voice = getView().findViewById(R.id.txt_voice_call);
        txt_message.setText("Message  " + getnumber());
        txt_voice.setText("Voice call  " + getnumber());
        txt_video.setText("Video call  " + getnumber());
        selectUsers = new ArrayList<Contacts>();
        prefManager = new MyPreferenceManager(getContext());
        if (getIsContact()) {
            txt_add_to_exist_contact.setVisibility(View.GONE);
            txt_add_to_contact.setVisibility(View.GONE);
        }
        onClick();
    }

    private LiveData<CommonResponse> commonResponseLiveData;

    private void onClick() {
        txt_add_to_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 0;
                requestContactPermission(0);


            }
        });
        txt_add_to_exist_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                requestContactPermission(1);

            }
        });
        txt_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("chat_room_id", getuId());
                intent.putExtra("name", getname());
                intent.putExtra("mobileNo", getnumber());
                intent.putExtra("status", "Online");
                intent.putExtra("oneToOne", "1");
                intent.putExtra("profilePic", "");
                intent.putExtra("flag", "contact_user");
                intent.putExtra("activityFrom", "SelectContactActivity");
                intent.putExtra("userStatus","Active");      //Active

                // intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dismiss();
            }
        });
    }

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {
        verifiedContactsResponse.observe(this, new Observer<VerifiedContactsModel>() {
            @Override
            public void onChanged(VerifiedContactsModel verifiedContactsModel) {
                //save access token
                try {
                    if (verifiedContactsModel.getSuccess()) {

                        if (verifiedContactsModel.getData() != null && verifiedContactsModel.getData().size() != 0) {
                            ArrayList<Contacts> verifiedUsers = new ArrayList<>();
                            for (int i = 0; i < verifiedContactsModel.getData().size(); i++) {
                                Contacts contacts = new Contacts();
                                for (int j = 0; j < selectUsers.size(); j++) {
                                    String mobile = selectUsers.get(j).getPhone().contains("+91") ? selectUsers.get(j).getPhone().replace("+91", "") : selectUsers.get(j).getPhone();
                                    String number = mobile.replaceAll("\\s", "");

                                    if (verifiedContactsModel.getData().get(i).getPhone().equalsIgnoreCase(number)) {
                                        contacts.setName(selectUsers.get(j).getName());
                                    }
                                }
                                contacts.setPhone(verifiedContactsModel.getData().get(i).getPhone());
                                contacts.setProfilePicture(verifiedContactsModel.getData().get(i).getProfilePicture());
                                contacts.setUid(verifiedContactsModel.getData().get(i).getUid());
                                contacts.setStatus("Hii, I am using Ozochat");
                                verifiedUsers.add(contacts);
                            }
                            prefManager.saveArrayListContact(verifiedUsers, prefManager.KEY_CONTACTS);
                            dismiss();
                        }


                        Log.d("SelectContactActivity", "----\n Message : " + verifiedContactsModel.getMessage() +
                                "\n Data : " + verifiedContactsModel.getData());
                        //  Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("SelectContactActivity", "----\n Message : " + verifiedContactsModel.getMessage() +
                                "\n Data : " + verifiedContactsModel.getData());
                    }

                } catch (Exception e) {
                } finally {
                    dismiss();
                }
            }
        });

    }


    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {
        createGroupResponse.observe(MoreOptionDialog.this, gRoupREsponse -> {

        });
    }

    @Override
    public void onGroupImgUploadSuccess(LiveData<UploadResponse> uploadGroupImgResponse) {

    }

    public interface UnfollowUser {
        public void onSuccessUnFollow(LiveData<CommonResponse> data);
    }

    public void requestContactPermission(int flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.WRITE_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Write Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.WRITE_CONTACTS}
                                    , PERMISSIONS_REQUEST_WRITE_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.WRITE_CONTACTS},
                            PERMISSIONS_REQUEST_WRITE_CONTACTS);
                }
            } else {
                if (flag == 1) {
                    Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                    startActivityForResult(intentInsertEdit, 2);
                } else {
                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    contactIntent
                            .putExtra(ContactsContract.Intents.Insert.NAME, getname())
                            .putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                    startActivityForResult(contactIntent, 1);
                }

            }
        } else {
            if (flag == 1) {
                Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                startActivityForResult(intentInsertEdit, 2);
            } else {
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, getname())
                        .putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                startActivityForResult(contactIntent, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                if (flag == 1) {
                    Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    intentInsertEdit.putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                    startActivityForResult(intentInsertEdit, 2);
                } else {
                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    contactIntent
                            .putExtra(ContactsContract.Intents.Insert.NAME, getname())
                            .putExtra(ContactsContract.Intents.Insert.PHONE, getnumber());
                    startActivityForResult(contactIntent, 1);
                }
            } else {
                dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                showContacts();
            }
        }
    }

    Cursor phones;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    MyPreferenceManager prefManager;
    ArrayList<Contacts> selectUsers;

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {

                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String number = phoneNumber.replaceAll("\\s", "");
                    Contacts selectUser = new Contacts();
                    selectUser.setName(name);
                    selectUser.setProfilePicture("https://api.androidhive.info/images/nature/david1.jpg");
                    selectUser.setPhone(number);
                    selectUser.setStatus("I am a Naturalist");
                    if (!prefManager.getUserDetails().get(prefManager.KEY_USER_MOBILE).equals(number)) {
                        selectUsers.add(selectUser);
                    }
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // sortContacts();
            int count = selectUsers.size();
            ArrayList<Contacts> removed = new ArrayList<>();
            ArrayList<Contacts> contacts = new ArrayList<>();
            for (int i = 0; i < selectUsers.size(); i++) {
                Contacts inviteFriendsProjo = selectUsers.get(i);

                if (inviteFriendsProjo.getName().matches("\\d+(?:\\.\\d+)?") || inviteFriendsProjo.getName().trim().length() == 0) {
                    removed.add(inviteFriendsProjo);
                    Log.d("Removed Contact", new Gson().toJson(inviteFriendsProjo));
                } else {
                    contacts.add(inviteFriendsProjo);
                }
            }
            contacts.addAll(removed);
            selectUsers = removeDuplicates(contacts);
            sendContactsList(selectUsers);

        }
    }

    public ArrayList<Contacts> removeDuplicates(ArrayList<Contacts> list) {
        Set<Contacts> set = new TreeSet(new Comparator<Contacts>() {

            @Override
            public int compare(Contacts o1, Contacts o2) {
                if (o1.getPhone().equalsIgnoreCase(o2.getPhone())) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);

        final ArrayList newList = new ArrayList(set);
        return newList;
    }

    private void sendContactsList(ArrayList<Contacts> selectUserslist) {
        ArrayList<MobileObject> conList = new ArrayList();
        for (int i = 0; i < selectUserslist.size(); i++) {
            String mobile = selectUserslist.get(i).getPhone().contains("+91") ? selectUserslist.get(i).getPhone().replace("+91", "") : selectUserslist.get(i).getPhone();
            String number = mobile.replaceAll("\\s", "");

            boolean flag = true;
            for (int j = 0; j < conList.size(); j++) {
                if (conList.get(j).getMobiles().equals(number)) {
                    flag = false;
                    break;
                } else {
                    flag = true;
                }
            }

            if (flag) {
                conList.add(new MobileObject(number));
                Log.d(tag, "--not--number--" + number);
            } else {
                Log.d(tag, "--already--number--" + number);
            }


        }
        NumberListObject arrayListAge = new NumberListObject();
        arrayListAge.setMobile(conList);
        gotoFetchValidMembers(arrayListAge);
    }

    private void gotoFetchValidMembers(NumberListObject arrayListAge) {

        arrayListAge.setSender_id(prefManager.getUserId());
        for (int i = 0; i < arrayListAge.getMobile().size(); i++) {
            Log.d(tag, "--contact-" + arrayListAge.getMobile().get(i).getMobiles());
        }
        UserChatListModel userChatListModel = new UserChatListModel();
        userChatListModel.sendContacts(getContext(), userChatListModel.contactsListener = this, arrayListAge);
    }

}
