package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityDetailViewUpdateBinding;
import com.ozonetech.ozochat.databinding.ContentMainBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewTopBinding;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.utils.MyDividerItemDecoration;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.GroupMembersAdpater;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailViewUpdateActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener , CreateGroupInterface ,GroupMembersAdpater.ContactsAdapterListener{

    private boolean isHideToolbarView = false;
    ActivityDetailViewUpdateBinding dataBinding;
    WidgetHeaderViewTopBinding widgetHeaderViewTopBinding;
    WidgetHeaderViewBinding widgetHeaderViewBinding;
    ContentMainBinding contentMainBinding;
    String contactName, last_seen, contactProfilePic,group_id;
    int groupChat, admin_id;
    MyPreferenceManager myPreferenceManager;
    UserChatViewModel chatViewModel;
    GroupMembersAdpater groupMembersAdpater;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(DetailViewUpdateActivity.this, R.layout.activity_detail_view_update);
        chatViewModel = ViewModelProviders.of(DetailViewUpdateActivity.this).get(UserChatViewModel.class);
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
        contactProfilePic = intent.getStringExtra("contactProfilePic");
        last_seen = intent.getStringExtra("last_seen");
        groupChat = intent.getIntExtra("groupChat", 2);
        admin_id = intent.getIntExtra("admin_id", 0);
        group_id=intent.getStringExtra("group_id");
        initUi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUi() {
        dataBinding.appbar.addOnOffsetChangedListener(this);

        Glide.with(this)
                .load(contactProfilePic)
                .placeholder(R.drawable.profile_icon)
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
                //Intent intent= new Intent(DetailViewUpdateActivity.this)
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
                groupId.addProperty("groupId","GP1603952201631" );

                JsonObject memersObjeect = new JsonObject();
                memersObjeect.add("member", memerArray);

                jsonArray.add(memersObjeect);
                jsonArray.add(groupId);

                chatViewModel.leftGroup(getApplicationContext(), jsonArray);

            }
        });


        gotoGroupDetails();


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
    public void onSuccessLeftGroup(LiveData<CommonResponse> leftGroupResponse) {
        leftGroupResponse.observe(DetailViewUpdateActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {

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

                        if (groupDetailModel.getData() != null) {

                            ArrayList<Contacts> myContactsArrayList = new ArrayList<>();
                            myContactsArrayList = myPreferenceManager.getArrayListContact("Contacts");

                            ArrayList<Contacts> groupMembers = new ArrayList<>();
                            for(int i=0;i<groupDetailModel.getData().size();i++){
                                Contacts contacts = new Contacts();
                                for (int j = 0; j < myContactsArrayList.size(); j++) {
                                    String mobile = myContactsArrayList.get(j).getPhone();
                                    if (groupDetailModel.getData().get(i).getMobile().equalsIgnoreCase(mobile)) {
                                        contacts.setName(myContactsArrayList.get(j).getName());
                                    }else{
                                        contacts.setName(groupDetailModel.getData().get(i).getUsername());
                                    }
                                }
                                contacts.setPhone(groupDetailModel.getData().get(i).getMobile());
                                contacts.setProfilePicture(groupDetailModel.getData().get(i).getImage());
                                contacts.setUid(groupDetailModel.getData().get(i).getUserId());
                                contacts.setStatus("Hii, I am using Ozochat");
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

    private void setRecyclerView(ArrayList<Contacts> groupMembers) {
        contentMainBinding.searchtoolbar.setTitle(String.valueOf(groupMembers.size())+" participants");
        // white background notification bar
        whiteNotificationBar(contentMainBinding.rvContactsList);

        groupMembersAdpater= new GroupMembersAdpater(DetailViewUpdateActivity.this, groupMembers,this);
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
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }}