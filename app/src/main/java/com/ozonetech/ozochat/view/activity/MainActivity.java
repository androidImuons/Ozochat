package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityMainBinding;
import com.ozonetech.ozochat.view.fragment.CallsFragment;
import com.ozonetech.ozochat.view.fragment.CameraFragment;
import com.ozonetech.ozochat.view.fragment.ChatsFragment;
import com.ozonetech.ozochat.view.fragment.StatusFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding dataBinding;
    private int[] tabIcons = {
            R.drawable.camera_icon
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding= DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);

        init();
    }

    private void init() {
        setSupportActionBar(dataBinding.toolbar);
        setupViewPager(dataBinding.viewpager);
        dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
        setupTabIcons();
        dataBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSelectContactActivity();
            }
        });
    }

    private void gotoSelectContactActivity() {
        Intent intent=new Intent(MainActivity.this,SelectContactActivity.class);
        startActivity(intent);
    }

    private void setupTabIcons() {
        dataBinding.tabs.getTabAt(0).setIcon(tabIcons[0]);
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(),"");
        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new StatusFragment(), "STATUS");
        adapter.addFragment(new CallsFragment(), "CALLS");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}


//https://www.wowza.com/blog/build-live-streaming-broadcast-app-android