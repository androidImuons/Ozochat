package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityAddMemberBinding;
import com.ozonetech.ozochat.utils.MyDividerItemDecoration;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.AddMemberAdapter;
import com.ozonetech.ozochat.view.adapter.SelectedGrpMemberAdpter;
import com.ozonetech.ozochat.viewmodel.Contacts;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity implements AddMemberAdapter.ContactsAdapterListener,SelectedGrpMemberAdpter.SelectedAdpterListener {

    ActivityAddMemberBinding dataBinding;
    AddMemberAdapter addMemberAdapter;
    SelectedGrpMemberAdpter selectedGrpMemberAdpter;
    ArrayList<Contacts> myContactsArrayList;
    ArrayList<Contacts> groupMembers;
    ArrayList<Contacts> addMemeberContactsArrayList;
    MyPreferenceManager myPreferenceManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(AddMemberActivity.this, R.layout.activity_add_member);
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);

        setSupportActionBar(dataBinding.toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add participants");


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

        dataBinding.rvSelectedMember.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

        addMemeberContactsArrayList=new ArrayList<>();
        selectedGrpMemberAdpter = new SelectedGrpMemberAdpter(addMemeberContactsArrayList, this);
        dataBinding.rvSelectedMember.setAdapter(selectedGrpMemberAdpter);
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
    public void onContactSelected(Contacts contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickedMember(Contacts contact) {
        Toast.makeText(getApplicationContext(), "Selected Member: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }
}