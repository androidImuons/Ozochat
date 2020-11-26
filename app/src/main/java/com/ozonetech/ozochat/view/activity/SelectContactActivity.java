package com.ozonetech.ozochat.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivitySelectContactBinding;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.GroupCreateRecord;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.ContactsAdapter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SelectContactActivity extends BaseActivity implements ContactsAdapter.ContactsAdapterListener, ContactsListener {
    MyPreferenceManager prefManager;
    ActivitySelectContactBinding dataBinding;
    ArrayList<Contacts> selectUsers;
    ContactsAdapter contactsAdapter;
    Cursor phones;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private SearchView searchView;
    Contacts contactsViewModel;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    public boolean is_group_create;
    private Bundle bundle;
    private String tag = "SelectContactActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinding = DataBindingUtil.setContentView(SelectContactActivity.this, R.layout.activity_select_contact);
        contactsViewModel = ViewModelProviders.of(SelectContactActivity.this).get(Contacts.class);
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        dataBinding.etGroupName.setVisibility(View.GONE);
        dataBinding.llNewGroup.setVisibility(View.VISIBLE);
        dataBinding.llNewContact.setVisibility(View.VISIBLE);
        prefManager = new MyPreferenceManager(SelectContactActivity.this);
        actionModeCallback = new ActionModeCallback();

        bundle = getIntent().getExtras();

        init();

    }

    private void init() {
        dataBinding.toolbar.setTitle(R.string.select_contact);
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectUsers = new ArrayList<Contacts>();
        requestContactPermission();

        dataBinding.llNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contactsAdapter != null) {
                    is_group_create = true;
                    contactsAdapter.setGroupFlaf(true);
                    dataBinding.etGroupName.setVisibility(View.VISIBLE);
                    dataBinding.llNewGroup.setVisibility(View.GONE);
                    dataBinding.llNewContact.setVisibility(View.GONE);
                }


            }
        });

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
            LoadContact loadContact = new LoadContact();
            loadContact.execute();
        }
    }

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
                showSnackbar(dataBinding.clSelectContacts, "Until you grant the permission, we canot display the names", Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onContactSelected(Contacts contact) {
        Intent intent = new Intent(SelectContactActivity.this, UserChatActivity.class);
        intent.putExtra("chat_room_id", String.valueOf(contact.getUid()));
        intent.putExtra("name", contact.getName());
        intent.putExtra("mobileNo", contact.getPhone());
        intent.putExtra("status", contact.getStatus());
        intent.putExtra("oneToOne","1");
        intent.putExtra("profilePic", contact.getProfilePicture());
        intent.putExtra("flag", "contact_user");
        intent.putExtra("activityFrom", "SelectContactActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //  Toast.makeText(getApplicationContext(), "Selected: " + contact.getUid() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRowLongClicked(int position) {
// long press is performed, enable action mode
        dataBinding.etGroupName.setVisibility(View.VISIBLE);
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        contactsAdapter.toggleSelection(position);
        int count = contactsAdapter.getSelectedItemCount();

        if (count == 0) {
            dataBinding.etGroupName.setVisibility(View.GONE);
            actionMode.finish();
        } else {
            actionMode.setTitle("Participants : " + String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            // swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_create_group:
                    if (!TextUtils.isEmpty(dataBinding.etGroupName.getText().toString().trim())) {
                        String groupName = dataBinding.etGroupName.getText().toString().trim();
                        createGroup(groupName);
                        mode.finish();
                        return true;
                    } else {
                        showSnackbar(dataBinding.clSelectContacts, "Please enter group name", Snackbar.LENGTH_SHORT);
                        return false;
                    }

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            contactsAdapter.clearSelections();
            // swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            dataBinding.etGroupName.setVisibility(View.GONE);
            dataBinding.llNewGroup.setVisibility(View.VISIBLE);
            dataBinding.llNewContact.setVisibility(View.VISIBLE);
            dataBinding.rvContactsList.post(new Runnable() {
                @Override
                public void run() {
                    contactsAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void createGroup(String groupName) {
        contactsAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions = contactsAdapter.getSelectedItems();
        //[ {"admin":9922803527}, {"members": [ {"mobile": "9922803527"}, {"mobile": "7507828337"}]}, {"group_name":"Android"} ]

        JsonArray memerArray = new JsonArray();
        // for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            String selectedMobileNo = contactsAdapter.getData(selectedItemPositions.get(i));
            JsonObject member = new JsonObject();
            member.addProperty("mobile", selectedMobileNo);
            memerArray.add(member);
        }
        JsonArray jsonArray = new JsonArray();
        JsonObject admin = new JsonObject();
        admin.addProperty("admin", prefManager.getUserDetails().get(MyPreferenceManager.KEY_USER_MOBILE));
        JsonObject memersObjeect = new JsonObject();
        memersObjeect.add("members", memerArray);
        JsonObject groupname = new JsonObject();
        groupname.addProperty("group_name", groupName);
        jsonArray.add(admin);
        jsonArray.add(memersObjeect);
        jsonArray.add(groupname);
        Log.d("SelectContactActiivty", "---jsonarray Create group : " + jsonArray);

        showProgressDialog("Please wait...");
        contactsViewModel.createGroup(SelectContactActivity.this, contactsViewModel.contactsListener = this, jsonArray);

    }

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {
        verifiedContactsResponse.observe(SelectContactActivity.this, new Observer<VerifiedContactsModel>() {
            @Override
            public void onChanged(VerifiedContactsModel verifiedContactsModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (verifiedContactsModel.getSuccess()) {

                        if (verifiedContactsModel.getData() != null && verifiedContactsModel.getData().size() != 0) {
                            ArrayList<Contacts> verifiedUsers = new ArrayList<>();
                            for (int i = 0; i < verifiedContactsModel.getData().size(); i++) {
                                Contacts contacts = new Contacts();
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
                            }


                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            setRecyclerView(inflater, verifiedUsers);
                        }


                        Log.d("SelectContactActivity", "----\n Message : " + verifiedContactsModel.getMessage() +
                                "\n Data : " + verifiedContactsModel.getData());
                        //  Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("SelectContactActivity", "----\n Message : " + verifiedContactsModel.getMessage() +
                                "\n Data : " + verifiedContactsModel.getData());
                    }

                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {
        createGroupResponse.observe(SelectContactActivity.this, new Observer<CreateGRoupREsponse>() {
            @Override
            public void onChanged(CreateGRoupREsponse createGRoupREsponse) {
                //save access token
                hideProgressDialog();
                Log.d("SelectContactActivity", "----\n Message : " + createGRoupREsponse.getMessage() +
                        "\n Data : " + createGRoupREsponse.getData());

                try {
                    if (createGRoupREsponse.getSuccess()) {

                        Log.d("SelectContactActivity", "----\n Message : " + createGRoupREsponse.getMessage() +
                                "\n Data : " + createGRoupREsponse.getData());
                        //  Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();
                        callChatActivity(createGRoupREsponse.getData().get(0));
                    } else {
                        Toast.makeText(SelectContactActivity.this, createGRoupREsponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("SelectContactActivity", "----\n Message : " + createGRoupREsponse.getMessage() +
                                "\n Data : " + createGRoupREsponse.getData());
                    }

                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }

            }
        });
    }

    private void callChatActivity(GroupCreateRecord groupCreateRecord) {
        Intent intent = new Intent(SelectContactActivity.this, UserChatActivity.class);
        intent.putExtra("chat_room_id", groupCreateRecord.getGroupId());
        intent.putExtra("name", dataBinding.etGroupName.getText().toString().trim());
        intent.putExtra("admin_id", groupCreateRecord.getAdmin_user_id());
        intent.putExtra("oneToOne","0");
        intent.putExtra("flag", "group");
        intent.putExtra("activityFrom", "SelectContactActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void setRecyclerView(LayoutInflater inflater, ArrayList<Contacts> verifiedUsers) {
        prefManager.saveArrayListContact(verifiedUsers, prefManager.KEY_CONTACTS);
        dataBinding.toolbar.setSubtitle(String.valueOf(verifiedUsers.size()) + " contacts");
        contactsAdapter = new ContactsAdapter(SelectContactActivity.this, inflater, verifiedUsers, this, is_group_create);
        dataBinding.rvContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataBinding.rvContactsList.setAdapter(contactsAdapter);
        // white background notification bar
        whiteNotificationBar(dataBinding.rvContactsList);
        if (getIntent().hasExtra("action")) {
            if (contactsAdapter != null) {
                is_group_create = true;
                contactsAdapter.setGroupFlaf(true);
                dataBinding.etGroupName.setVisibility(View.VISIBLE);
                dataBinding.llNewGroup.setVisibility(View.GONE);
                dataBinding.llNewContact.setVisibility(View.GONE);
            }
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
        showProgressDialog("Please wait...");
        arrayListAge.setSender_id(prefManager.getUserId());
        for (int i = 0; i < arrayListAge.getMobile().size(); i++) {
            Log.d(tag, "--contact-" + arrayListAge.getMobile().get(i).getMobiles());
        }
        contactsViewModel.sendContacts(SelectContactActivity.this, contactsViewModel.contactsListener = this, arrayListAge);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                contactsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                contactsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}