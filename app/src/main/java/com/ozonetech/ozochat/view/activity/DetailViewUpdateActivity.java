package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityDetailViewUpdateBinding;
import com.ozonetech.ozochat.databinding.ContentMainBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewTopBinding;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.utils.MyDividerItemDecoration;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.GroupMembersAdpater;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import androidx.appcompat.widget.SearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DetailViewUpdateActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, CreateGroupInterface, GroupMembersAdpater.ContactsAdapterListener , ContactsListener {

    private boolean isHideToolbarView = false;
    ActivityDetailViewUpdateBinding dataBinding;
    WidgetHeaderViewTopBinding widgetHeaderViewTopBinding;
    WidgetHeaderViewBinding widgetHeaderViewBinding;
    ContentMainBinding contentMainBinding;
    String contactName, last_seen, contactProfilePic, group_id;
    int groupChat, admin_id;
    MyPreferenceManager myPreferenceManager;
    UserChatViewModel chatViewModel;
    GroupMembersAdpater groupMembersAdpater;
    private SearchView searchView;
    ArrayList<Contacts> groupMembers;
    PopupWindow popupWindow;
    Contacts contactsViewModel;
    Cursor phones;
    ArrayList<Contacts> selectUsers;
    ArrayList<Contacts> verifiedUsers;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(DetailViewUpdateActivity.this, R.layout.activity_detail_view_update);
        chatViewModel = ViewModelProviders.of(DetailViewUpdateActivity.this).get(UserChatViewModel.class);
        contactsViewModel = ViewModelProviders.of(DetailViewUpdateActivity.this).get(Contacts.class);
        chatViewModel.groupInterface = (CreateGroupInterface) this;

        myPreferenceManager = new MyPreferenceManager(DetailViewUpdateActivity.this);
        widgetHeaderViewBinding = dataBinding.floatHeaderView;
        widgetHeaderViewTopBinding = dataBinding.toolbarHeaderView;
        contentMainBinding = dataBinding.updationLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        dataBinding.toolbar.setTitle("");


        contentMainBinding.searchtoolbar.setTitle("");
        setSupportActionBar(contentMainBinding.searchtoolbar);
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        contactName = intent.getStringExtra("contactName");
        group_id = intent.getStringExtra("group_id");
        contactProfilePic = intent.getStringExtra("contactProfilePic");
        last_seen = intent.getStringExtra("last_seen");
        groupChat = intent.getIntExtra("groupChat", 2);
        admin_id = intent.getIntExtra("admin_id", 0);


        initUi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUi() {
        requestContactPermission();
        selectUsers = new ArrayList<Contacts>();

        dataBinding.appbar.addOnOffsetChangedListener(this);

        Glide.with(this)
                .load(contactProfilePic)
                .placeholder(R.drawable.person_icon)
                .into(dataBinding.image);
        widgetHeaderViewTopBinding.name.setText(contactName);
        if (groupChat == 0) {
            widgetHeaderViewTopBinding.lastSeen.setText("");
        } else {
            widgetHeaderViewTopBinding.lastSeen.setText(last_seen);

        }

        widgetHeaderViewBinding.name.setText(contactName);
        if (groupChat == 0) {
            widgetHeaderViewTopBinding.lastSeen.setText("");
        } else {
            widgetHeaderViewBinding.lastSeen.setText(last_seen);
        }

        if (groupChat == 0) {
            contentMainBinding.rlContentMain.setVisibility(View.VISIBLE);
            int user_id = Integer.parseInt(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
            if (admin_id == user_id) {
                contentMainBinding.llAddPeople.setVisibility(View.VISIBLE);
            } else {
                contentMainBinding.llAddPeople.setVisibility(View.GONE);
            }

        } else {
            contentMainBinding.rlContentMain.setVisibility(View.GONE);
        }


        contentMainBinding.llAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DetailViewUpdateActivity.this,AddMemberActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)groupMembers);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("contactName",contactName);
                intent.putExtra("group_id",group_id);
                intent.putExtra("last_seen", last_seen);
                intent.putExtra("contactProfilePic", contactProfilePic);
                intent.putExtra("groupChat", groupChat);
                intent.putExtra("admin_id", admin_id);
                startActivity(intent);
                finish();
            }
        });


        contentMainBinding.cvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //"[ {"member": [ {"mobile": "9509504456"} ]}, {"groupId":"GP1603952201631"} ]"
                JsonArray jsonArray = new JsonArray();
                JsonArray memerArray = new JsonArray();
                JsonObject member = new JsonObject();
                member.addProperty("mobile", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));
                memerArray.add(member);

                JsonObject groupId = new JsonObject();
                groupId.addProperty("groupId", group_id);

                JsonObject memersObjeect = new JsonObject();
                memersObjeect.add("member", memerArray);

                jsonArray.add(memersObjeect);
                jsonArray.add(groupId);

                gotoLeftGroup(jsonArray);

            }
        });


        //gotoGroupDetails();


    }

    private void gotoLeftGroup(JsonArray jsonArray) {
        showProgressDialog("Please wait...");
        chatViewModel.leftGroup(DetailViewUpdateActivity.this, jsonArray, chatViewModel.groupInterface = this);
    }

    private void gotoGroupDetails() {
        showProgressDialog("Please wait...");

        Map<String, String> chatMap = new HashMap<>();
        chatMap.put("group_id", group_id);
        chatViewModel.getGroupDetails(DetailViewUpdateActivity.this, chatMap, chatViewModel.groupInterface = this);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            widgetHeaderViewTopBinding.headerView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            widgetHeaderViewTopBinding.headerView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse) {

    }

    @Override
    public void onSuccessLeftGroup(LiveData<LeftResponseModel> leftGroupResponse) {
        leftGroupResponse.observe(DetailViewUpdateActivity.this, new Observer<LeftResponseModel>() {
            @Override
            public void onChanged(LeftResponseModel leftResponseModel) {
                //{
                //    "success": true,
                //    "message": "7507828337 left the group"
                //}
                Log.d("GroupDetailModel", "--leftGroupResponse : Code" + leftResponseModel.toString());
                hideProgressDialog();

                try {
                    if (leftResponseModel.getSuccess()) {
                        showSnackbar(dataBinding.rlChatDetail, leftResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    } else {
                        showSnackbar(dataBinding.rlChatDetail, leftResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onSuccessGroupDetails(LiveData<GroupDetailModel> groupDetailResponase) {
        groupDetailResponase.observe(DetailViewUpdateActivity.this, new Observer<GroupDetailModel>() {
            @Override
            public void onChanged(GroupDetailModel groupDetailModel) {

                hideProgressDialog();
                try {
                    if (groupDetailModel.getSuccess()) {
                        Log.d("GroupDetailModel", "--GroupDetailModel : Code" + groupDetailModel.getData());
//            "admin_id": 100,
//            "user_id": 101,
//            "isAdmin": false,
//            "mobile": "7087741181",
//            "username": "Azhar",
//            "image": "http://3.0.49.131/api/uploads/null"
                        if (groupDetailModel.getData() != null) {

                            ArrayList<Contacts> myContactsArrayList = new ArrayList<>();
                            myContactsArrayList = verifiedUsers;

                            groupMembers = new ArrayList<>();
                            for (int i = 0; i < groupDetailModel.getData().size(); i++) {
                                Contacts contacts = new Contacts();
                                for (int j = 0; j < myContactsArrayList.size(); j++) {
                                    String mobile = myContactsArrayList.get(j).getPhone();
                                    if (groupDetailModel.getData().get(i).getMobile().equalsIgnoreCase(mobile)) {
                                        contacts.setName(myContactsArrayList.get(j).getName());
                                    } else {
                                        contacts.setName(groupDetailModel.getData().get(i).getUsername());
                                    }
                                }
                                contacts.setPhone(groupDetailModel.getData().get(i).getMobile());
                                contacts.setProfilePicture(groupDetailModel.getData().get(i).getImage());
                                contacts.setUid(groupDetailModel.getData().get(i).getUserId());
                                contacts.setStatus("Hii, I am using Ozochat");
                                contacts.setAdmin(groupDetailModel.getData().get(i).getIsAdmin());

                                groupMembers.add(contacts);

                            }

                            setRecyclerView(groupMembers);

                        }

                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    @Override
    public void onSuccessAddToGroup(LiveData<AddMemberResponseModel> addMemberResponse) {

    }

    @Override
    public void onSuccessRemoveMember(LiveData<LeftResponseModel> removeMemberResponse) {
        removeMemberResponse.observe(DetailViewUpdateActivity.this, new Observer<LeftResponseModel>() {
            @Override
            public void onChanged(LeftResponseModel leftResponseModel) {
                Log.d("GroupDetailModel", "--removeMember : Code" + leftResponseModel.toString());
                hideProgressDialog();

                try {
                    if (leftResponseModel.getSuccess()) {
                        showSnackbar(dataBinding.rlChatDetail, leftResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        popupWindow.dismiss();
                        gotoGroupDetails();
                    } else {
                        showSnackbar(dataBinding.rlChatDetail, leftResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    private void setRecyclerView(ArrayList<Contacts> groupMembers) {

        for(int i=0;i<groupMembers.size();i++){
            String mobileNo = groupMembers.get(i).getPhone();
            if(mobileNo.equalsIgnoreCase(myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_USER_MOBILE))){
                if(groupMembers.get(i).getAdmin()){
                    contentMainBinding.llAddPeople.setVisibility(View.VISIBLE);
                }else{
                    contentMainBinding.llAddPeople.setVisibility(View.GONE);
                }
            }
        }

        contentMainBinding.searchtoolbar.setTitle(String.valueOf(groupMembers.size()) + " participants");
        // white background notification bar
        whiteNotificationBar(contentMainBinding.rvContactsList);

        groupMembersAdpater = new GroupMembersAdpater(DetailViewUpdateActivity.this, groupMembers, this);
        contentMainBinding.rvContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        contentMainBinding.rvContactsList.setItemAnimator(new DefaultItemAnimator());
        contentMainBinding.rvContactsList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        contentMainBinding.rvContactsList.setAdapter(groupMembersAdpater);

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
                groupMembersAdpater.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                groupMembersAdpater.getFilter().filter(query);
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
    public void onContactSelected(Contacts contact) {
        for(int i=0;i<groupMembers.size();i++){
            String mobileNo = groupMembers.get(i).getPhone();
            if(mobileNo.equalsIgnoreCase(myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_USER_MOBILE))){
                if(groupMembers.get(i).getAdmin()){
                    openDialog(contact);
                }
            }
        }

    }

    private void openDialog(Contacts contactPerson) {


        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.group_action_menu, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        TextView tvRemove = popupView.findViewById(R.id.tvRemove);
        tvRemove.setText("Remove " + contactPerson.getName());
        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //[ {"admin":9922803527}, {"member": [ {"mobile": "7087741181"} ]}, {"groupId":"GP1606199463412"} ]

                JsonArray jsonArray = new JsonArray();

                JsonObject group_admin = new JsonObject();
                group_admin.addProperty("admin", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));


                JsonArray memerArray = new JsonArray();
                JsonObject member = new JsonObject();
                member.addProperty("mobile", contactPerson.getPhone());
                memerArray.add(member);
                JsonObject memersObjeect = new JsonObject();
                memersObjeect.add("member", memerArray);

                JsonObject groupId = new JsonObject();
                groupId.addProperty("groupId", group_id);

                jsonArray.add(group_admin);
                jsonArray.add(memersObjeect);
                jsonArray.add(groupId);

                gotoRemoveMember(jsonArray);

            }
        });


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(dataBinding.rlChatDetail, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

    }

    private void gotoRemoveMember(JsonArray jsonArray) {
        showProgressDialog("Please wait...");
        chatViewModel.removeMember(DetailViewUpdateActivity.this, jsonArray, chatViewModel.groupInterface = this);
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
                showSnackbar(dataBinding.rlChatDetail, "Until you grant the permission, we canot display the names", Snackbar.LENGTH_SHORT);
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

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {
        verifiedContactsResponse.observe(DetailViewUpdateActivity.this, new Observer<VerifiedContactsModel>() {
            @Override
            public void onChanged(VerifiedContactsModel verifiedContactsModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (verifiedContactsModel.getSuccess()) {

                        if (verifiedContactsModel.getData() != null && verifiedContactsModel.getData().size() != 0) {
                            verifiedUsers = new ArrayList<>();
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

                          //  gotoAddMembertoGroup(verifiedUsers);
                            gotoGroupDetails();
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

    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {

    }

    @Override
    public void onGroupImgUploadSuccess(LiveData<UploadResponse> uploadGroupImgResponse) {

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
        contactsViewModel.sendContacts(DetailViewUpdateActivity.this, contactsViewModel.contactsListener = this, arrayListAge);
    }

}