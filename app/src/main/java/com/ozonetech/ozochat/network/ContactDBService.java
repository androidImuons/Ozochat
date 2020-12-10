package com.ozonetech.ozochat.network;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.ozonetech.ozochat.database.ChatDatabase;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.model.ContactModel;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.SelectContactActivity;
import com.ozonetech.ozochat.view.lifecycle.ContactDBLifeCycle;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ContactDBService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.ozonetech.ozochat.network.action.FOO";
    private static final String ACTION_BAZ = "com.ozonetech.ozochat.network.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.ozonetech.ozochat.network.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ozonetech.ozochat.network.extra.PARAM2";
    private MyPreferenceManager prefManager;
    ArrayList<Contacts> selectUsers;
    private String tag = "ContactDBService";

    public ContactDBService() {
        super("ContactDBService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ContactDBService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ContactDBService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    ContactDBLifeCycle contactDBLifeCycle;
    private ChatDatabase chatDatabase;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            selectUsers = new ArrayList<Contacts>();
            prefManager = new MyPreferenceManager(getApplicationContext());
            contactDBLifeCycle = new ContactDBLifeCycle(this);
            showContacts();
        }
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Cursor phones;


    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e(tag, "---phone size--" + phones.getCount());
                if (phones.getCount() == 0) {

                }

                while (phones.moveToNext()) {
                    if (phones.getColumnCount()!=0){
                        Bitmap bit_thumb = null;
                     //   String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
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
        Log.d(tag, "--service call- size--" + arrayListAge.getMobile().size());

        AppServices apiService = ServiceGenerator.createService(AppServices.class);
        apiService.getValidContacts(arrayListAge).enqueue(new Callback<VerifiedContactsModel>() {
            @Override
            public void onResponse(Call<VerifiedContactsModel> call, Response<VerifiedContactsModel> response) {
                VerifiedContactsModel verifiedContactsModel;
                if (response.isSuccessful()) {
                    verifiedContactsModel = response.body();

                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));
                    ChatDatabase.getInstance(getApplicationContext()).ValidContact().deleteAll();
                    loadData(verifiedContactsModel);
                } else {
                    verifiedContactsModel = response.body();
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<VerifiedContactsModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });

    }

    private void loadData(VerifiedContactsModel verifiedContactsModel) {
        try {
            if (verifiedContactsModel.getSuccess()) {

                if (verifiedContactsModel.getData() != null && verifiedContactsModel.getData().size() != 0) {
                    ArrayList<ContactModel> verifiedUsers = new ArrayList<>();
                    for (int i = 0; i < verifiedContactsModel.getData().size(); i++) {
                        ContactModel contacts = new ContactModel();
                        for (int j = 0; j < selectUsers.size(); j++) {
                            String mobile = selectUsers.get(j).getPhone().contains("+91") ? selectUsers.get(j).getPhone().replace("+91", "") : selectUsers.get(j).getPhone();
                            if (verifiedContactsModel.getData().get(i).getPhone().equalsIgnoreCase(mobile)) {
                                contacts.setName(selectUsers.get(j).getName());
                            }
                        }
                        contacts.setPhone(verifiedContactsModel.getData().get(i).getPhone());
                        contacts.setProfilePicture(verifiedContactsModel.getData().get(i).getProfilePicture());
                        contacts.setUid(verifiedContactsModel.getData().get(i).getUid());
                        contacts.setStatus("Hii, I am using Ozochat");
                        verifiedUsers.add(contacts);
                        Log.d(tag, "-insert item--" + contacts.getName());
                        ChatDatabase.getInstance(getApplicationContext()).ValidContact().insert(contacts);
                    }

                }

                Log.d(tag, "----\n Message : " + verifiedContactsModel.getMessage() +
                        "\n Data : " + verifiedContactsModel.getData());
                //  Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(tag, "----\n Message : " + verifiedContactsModel.getMessage() +
                        "\n Data : " + verifiedContactsModel.getData());
            }

        } catch (Exception e) {
            Log.d(tag, "----excepton--" + e.getMessage());
        } finally {

        }
    }

}
