package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityAddMemberBinding;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.AddMemberAdapter;
import com.ozonetech.ozochat.view.adapter.SelectedGrpMemberAdpter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddMemberActivity extends BaseActivity implements AddMemberAdapter.ContactsAdapterListener, SelectedGrpMemberAdpter.SelectedAdpterListener, CreateGroupInterface, ContactsListener {      //

    ActivityAddMemberBinding dataBinding;
    AddMemberAdapter addMemberAdapter;
    SelectedGrpMemberAdpter selectedGrpMemberAdpter;
    ArrayList<Contacts> myContactsArrayList;
    ArrayList<Contacts> groupMembers;
    ArrayList<Contacts> addMemeberContactsArrayList;
    MyPreferenceManager myPreferenceManager;
    private SearchView searchView;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    String group_name, group_id;
    String last_seen, contactProfilePic;
    int groupChat, admin_id;
    UserChatViewModel chatViewModel;
    Contacts contactsViewModel;
    Cursor phones;
    ArrayList<Contacts> selectUsers;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(AddMemberActivity.this, R.layout.activity_add_member);
        chatViewModel = ViewModelProviders.of(AddMemberActivity.this).get(UserChatViewModel.class);
        contactsViewModel = ViewModelProviders.of(AddMemberActivity.this).get(Contacts.class);
        myPreferenceManager = new MyPreferenceManager(AddMemberActivity.this);

        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);

        setSupportActionBar(dataBinding.toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add participants");
        actionModeCallback = new ActionModeCallback();

        init();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void init() {
        requestContactPermission();
        selectUsers = new ArrayList<Contacts>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        groupMembers = (ArrayList<Contacts>) args.getSerializable("ARRAYLIST");
        group_name = intent.getStringExtra("contactName");
        group_id = intent.getStringExtra("group_id");
        contactProfilePic = intent.getStringExtra("contactProfilePic");
        last_seen = intent.getStringExtra("last_seen");
        groupChat = intent.getIntExtra("groupChat", 2);
        admin_id = intent.getIntExtra("admin_id", 0);


        dataBinding.rvSelectedMember.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dataBinding.rvSelectedMember.setVisibility(View.GONE);
        addMemeberContactsArrayList = new ArrayList<>();
        selectedGrpMemberAdpter = new SelectedGrpMemberAdpter(AddMemberActivity.this, addMemeberContactsArrayList, this);



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
                addMemberAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                addMemberAdapter.getFilter().filter(query);
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
        }else{
            finish();
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
    public void onContactSelected(Contacts contact, int adapterPosition) {
        enableActionMode(contact, adapterPosition);
       // Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddMemeber(Contacts contacts) {
        addMemeberContactsArrayList.add(contacts);
        selectedGrpMemberAdpter = new SelectedGrpMemberAdpter(AddMemberActivity.this, addMemeberContactsArrayList, this);
        dataBinding.rvSelectedMember.setAdapter(selectedGrpMemberAdpter);

    }

    @Override
    public void onRemoveMemeber(Contacts contacts) {
        addMemeberContactsArrayList.remove(contacts);
        selectedGrpMemberAdpter = new SelectedGrpMemberAdpter(AddMemberActivity.this, addMemeberContactsArrayList, this);
        dataBinding.rvSelectedMember.setAdapter(selectedGrpMemberAdpter);

    }

    private void enableActionMode(Contacts contact, int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position, contact);
    }

    private void toggleSelection(int position, Contacts contact) {
        addMemberAdapter.toggleSelection(position);
        int count = addMemberAdapter.getSelectedItemCount();
        if (count == 0) {
            dataBinding.rvSelectedMember.setVisibility(View.GONE);
            actionMode.finish();
        } else {
            dataBinding.rvSelectedMember.setVisibility(View.VISIBLE);
            actionMode.setTitle("Participants : " + String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse) {

    }

    @Override
    public void onSuccessLeftGroup(LiveData<LeftResponseModel> leftGroupResponse) {

    }

    @Override
    public void onSuccessGroupDetails(LiveData<GroupDetailModel> groupDetailResponase) {

    }

    @Override
    public void onSuccessAddToGroup(LiveData<AddMemberResponseModel> addMemberResponse) {
        addMemberResponse.observe(AddMemberActivity.this, new Observer<AddMemberResponseModel>() {
            @Override
            public void onChanged(AddMemberResponseModel addMemberResponseModel) {

                Log.d("AddMember", "--addMemberResponseModel : Code" + addMemberResponseModel.toString());
                hideProgressDialog();

                try {
                    if (addMemberResponseModel.getSuccess()) {
                        Intent intent = new Intent(AddMemberActivity.this, DetailViewUpdateActivity.class);
                        intent.putExtra("contactName", group_name);
                        intent.putExtra("last_seen", last_seen);
                        intent.putExtra("contactProfilePic", contactProfilePic);
                        intent.putExtra("groupChat", groupChat);
                        intent.putExtra("group_id", group_id);
                        intent.putExtra("admin_id", admin_id);
                        startActivity(intent);
                        finish();
                    } else {
                        showSnackbar(dataBinding.rlAddMember, addMemberResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }

            }
        });

    }

    @Override
    public void onSuccessRemoveMember(LiveData<LeftResponseModel> removeMemberResponse) {

    }

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {
        verifiedContactsResponse.observe(AddMemberActivity.this, new Observer<VerifiedContactsModel>() {
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

                            gotoAddMembertoGroup(verifiedUsers);
                        }


                        Log.d("SelectContactActivity", "----\n Message : " + verifiedContactsModel.getMessage() +
                                "\n Data : " + verifiedContactsModel.getData());
                        //  Toast.makeText(SelectContactActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        //Toast.makeText(AddMemberActivity.this, verifiedContactsModel.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void gotoAddMembertoGroup(ArrayList<Contacts> verifiedUsers) {
        myContactsArrayList = new ArrayList<>();
        myContactsArrayList =verifiedUsers;
        for(int i=0;i<myContactsArrayList.size();i++){
            Log.d("remain contacts", "--\n--Before myContactsArrayList : " + myContactsArrayList.get(i).getPhone() + "  " +myContactsArrayList.get(i).getName());
        }

        for(int i=0;i<groupMembers.size();i++){
            Log.d("remain contacts", "--\n--groupMembers : " + groupMembers.get(i).getPhone() + "  " +groupMembers.get(i).getName());
        }


        addMemeberContactsArrayList = new ArrayList<>();
        if (myContactsArrayList.size() != 0) {

            for (int i = 0; i < myContactsArrayList.size(); i++) {
                String myContact = myContactsArrayList.get(i).getPhone();

                for (int j = 0; j < groupMembers.size(); j++) {
                    String groupContact = groupMembers.get(j).getPhone();

                    if (myContact.equalsIgnoreCase(groupContact)) {
                        myContactsArrayList.remove(i);
                    }
                }
            }

            for(int i=0;i<myContactsArrayList.size();i++){
                Log.d("remain contacts", "--\n--After myContactsArrayList : " + myContactsArrayList.get(i).getPhone() + "  " +myContactsArrayList.get(i).getName());
            }
            addMemberAdapter = new AddMemberAdapter(AddMemberActivity.this, myContactsArrayList, this);
            // white background notification bar
            whiteNotificationBar(dataBinding.rvContactsList);
            dataBinding.rvContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            dataBinding.rvContactsList.setAdapter(addMemberAdapter);


        }


    }

    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {

    }

    @Override
    public void onGroupImgUploadSuccess(LiveData<UploadResponse> uploadGroupImgResponse) {

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
                    addMemberToGroup();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            addMemberAdapter.clearSelections();

            actionMode = null;
            dataBinding.rvContactsList.post(new Runnable() {
                @Override
                public void run() {
                    addMemberAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void addMemberToGroup() {

        JsonArray jsonArray = new JsonArray();

        JsonObject groupadmin = new JsonObject();
        groupadmin.addProperty("admin", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));


        JsonArray memerArray = new JsonArray();

        for (int i = 0; i < addMemeberContactsArrayList.size(); i++) {
            JsonObject member = new JsonObject();
            member.addProperty("mobile", addMemeberContactsArrayList.get(i).getPhone());
            memerArray.add(member);
        }

        JsonObject memersObjeect = new JsonObject();
        memersObjeect.add("members", memerArray);

        JsonObject groupName = new JsonObject();
        groupName.addProperty("group_name", group_name);

        JsonObject groupId = new JsonObject();
        groupId.addProperty("groupId", group_id);

        jsonArray.add(groupadmin);
        jsonArray.add(memersObjeect);
        jsonArray.add(groupName);
        jsonArray.add(groupId);

        //[ {"admin":7087741183}, {"members": [ {"mobile": "7488713414"} ]}, {"group_name":"ABC"}, {"groupId":"GP1603952201631"} ]"
        Log.d("AddMember JsonArray", "-- add member" + jsonArray);
        addMembersToGroup(jsonArray);

    }

    private void addMembersToGroup(JsonArray jsonArray) {
        showProgressDialog("Please wait...");
        chatViewModel.addMemberToGroup(AddMemberActivity.this, jsonArray, chatViewModel.groupInterface = this);

    }

    // deleting the messages from recycler view
/*
    private void deleteMessages() {
        addMemberAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                addMemberAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
           // addMemberAdapter.removeData(selectedItemPositions.get(i));
        }
        addMemberAdapter.notifyDataSetChanged();
    }
*/

    @Override
    public void onClickedMember(Contacts contact) {
       // Toast.makeText(getApplicationContext(), "Selected Member: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRemoveMember(Contacts contacts) {
        addMemeberContactsArrayList.remove(contacts);
        selectedGrpMemberAdpter = new SelectedGrpMemberAdpter(AddMemberActivity.this, addMemeberContactsArrayList, this);
        dataBinding.rvSelectedMember.setAdapter(selectedGrpMemberAdpter);

        for (int i = 0; i < myContactsArrayList.size(); i++) {
            Contacts myContact = myContactsArrayList.get(i);

            if (contacts.equals(myContact)) {
                int position = i;
                enableActionMode(myContact, position);
            }

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
                showSnackbar(dataBinding.rlAddMember, "Until you grant the permission, we canot display the names", Snackbar.LENGTH_SHORT);
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
                    if (!myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE).equals(number)) {
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
                Log.d("AddMemberActivity", "--not--number--" + number);
            } else {
                Log.d("AddMemberActivity", "--already--number--" + number);
            }


        }
        NumberListObject arrayListAge = new NumberListObject();
        arrayListAge.setMobile(conList);
        gotoFetchValidMembers(arrayListAge);
    }

    private void gotoFetchValidMembers(NumberListObject arrayListAge) {
        showProgressDialog("Please wait...");
        arrayListAge.setSender_id(myPreferenceManager.getUserId());
        for (int i = 0; i < arrayListAge.getMobile().size(); i++) {
            Log.d("AddMemberActivity", "--contact-" + arrayListAge.getMobile().get(i).getMobiles());
        }
        contactsViewModel.sendContacts(AddMemberActivity.this, contactsViewModel.contactsListener = this, arrayListAge);
    }


}