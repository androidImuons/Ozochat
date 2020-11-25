package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityAddMemberBinding;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.AddMemberAdapter;
import com.ozonetech.ozochat.view.adapter.SelectedGrpMemberAdpter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends BaseActivity implements AddMemberAdapter.ContactsAdapterListener, SelectedGrpMemberAdpter.SelectedAdpterListener, CreateGroupInterface {      //

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(AddMemberActivity.this, R.layout.activity_add_member);
        chatViewModel = ViewModelProviders.of(AddMemberActivity.this).get(UserChatViewModel.class);

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


        myPreferenceManager = new MyPreferenceManager(AddMemberActivity.this);
        myContactsArrayList = new ArrayList<>();
        myContactsArrayList = myPreferenceManager.getArrayListContact("Contacts");
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

            addMemberAdapter = new AddMemberAdapter(AddMemberActivity.this, myContactsArrayList, this);
            // white background notification bar
            whiteNotificationBar(dataBinding.rvContactsList);
            dataBinding.rvContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            dataBinding.rvContactsList.setAdapter(addMemberAdapter);


        }


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
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(), "Selected Member: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
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
}