package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityMainBinding;
import com.ozonetech.ozochat.utils.AppUpdateChecker;
import com.ozonetech.ozochat.utils.AppVersionModel;
import com.ozonetech.ozochat.view.dialog.DialogAppUpdater;
import com.ozonetech.ozochat.view.fragment.CallsFragment;
import com.ozonetech.ozochat.view.fragment.CameraFragment;
import com.ozonetech.ozochat.view.fragment.ChatsFragment;
import com.ozonetech.ozochat.view.fragment.StatusFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding dataBinding;
    private int[] tabIcons = {
            R.drawable.camera_icon
    };
    private ImageView iv_menu;
    AppVersionModel appVersionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        setPopUpWindow();
        setActionBar();
        //setupUi();
        checkUpdate();
        init();
    }
    DialogAppUpdater dialogAppUpdater;
    private void checkUpdate() {
        new AppUpdateChecker(this, new AppUpdateChecker.AppUpdateAvailableListener() {
            @Override
            public void appUpdateAvailable(LiveData<AppVersionModel> appUpdatemodal) {

                appUpdatemodal.observe(MainActivity.this, new Observer<AppVersionModel>() {
                    @Override
                    public void onChanged(AppVersionModel appVersionModel) {
                        if (appVersionModel.isSuccess()){
                            MainActivity.this.appVersionModel =appVersionModel;
                            if (isFinishing()) return;
                            try {
                                if (dialogAppUpdater != null && dialogAppUpdater.isShowing()) {
                                    dialogAppUpdater.dismiss();
                                }
                                Log.d(tag,"-----dialog open-"+appUpdatemodal.getValue().isSuccess());
                                dialogAppUpdater = new DialogAppUpdater(MainActivity.this, appVersionModel);
                                dialogAppUpdater.show();
                            } catch (Exception e) {
                                Log.d(tag,"--exception-"+e.getMessage());
                            }
                        }
                    }
                });



            }
        }).checkForUpdate();
    }

    private PopupWindow mypopupWindow;
    private void setActionBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.dash_top_bar_view, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(0, 0, 5, 0);
        actionBar.setCustomView(mCustomView, layout);
        // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        iv_menu = mCustomView.findViewById(R.id.white_topup_iv_menu);

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mypopupWindow.showAsDropDown(v, -100, -150);

            }
        });

    }
    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_item_view, null);

        TextView txt_setting = view.findViewById(R.id.txt_setting);
        TextView txt_new_group = view.findViewById(R.id.txt_new_group);

        txt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ProfileInfoNew.class)
//                                .putExtra("userData" , new Gson().toJson(AppCommon.getInstance(getApplicationContext()).getUserObject())));

                startActivity(new Intent(MainActivity.this,ProfileInfoActivity.class));
            }
        });

        txt_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectContactActivity.class);
                intent.putExtra("action", "group");
                startActivity(intent);
            }
        });

        mypopupWindow = new PopupWindow(view, 500, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }

    private void init() {
        // setSupportActionBar(dataBinding.toolbar);
        setupViewPager(dataBinding.viewpager);
        dataBinding.tabs.setupWithViewPager(dataBinding.viewpager);
        setupTabIcons();
        dataBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSelectContactActivity();
            }
        });

       /* User user = new User(String.valueOf(1),
                "MAYURI",
                "mayuri@123");
*/
        // storing user in shared preferences
        // MyApplication.getInstance().getPrefManager().storeUser(user);


//        try {
//
//
//            IO.Options opts = new IO.Options();
//            opts.forceNew = true;
//            opts.reconnection = false;
//            opts.multiplex = true;
//            iSocket = IO.socket("http://3.0.49.131/", opts);
//            iSocket.on(Socket.EVENT_CONNECT, connection);
//            iSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//            iSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            iSocket.on("getMessages", message);
//            iSocket.connect();
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            Log.d(tag, "----exception--" + e.getMessage());
//        }

    }

    private Socket iSocket;
    private String tag = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();
        if (appVersionModel != null && appVersionModel.isSuccess()) {
            try {
                if (dialogAppUpdater != null && dialogAppUpdater.isShowing()) {
                    dialogAppUpdater.dismiss();
                }
                Log.d(tag,"--reshoww--");
                dialogAppUpdater = new DialogAppUpdater(MainActivity.this, appVersionModel);
                dialogAppUpdater.show();
            } catch (Exception e) {

            }
        }
    }

    private void checkSocketConnection() {
        if (iSocket.connected()) {
            Log.d(tag, "----connected- server-" + iSocket.id());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("message", "Rahul Message..group message karkay...");
                jsonObject.put("user_id", 80);
                jsonObject.put("g_id", 31);
                jsonObject.put("added_id", 82);

                iSocket.emit("sendGroupMessage", jsonObject);

                JSONObject json = new JSONObject();
                json.put("user_id", 80);
                iSocket.emit("getMessages", json).on("getMessages", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONArray data = (JSONArray) args[0];
                        Log.d(tag, "---get message--" + data.toString());
                    }
                });

                iSocket.on("getMessages", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {


                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(tag, "---not-connected--" + iSocket.open().connected());
            Log.d(tag, "---not-connected--" + iSocket.connected());


        }
    }


    private void gotoSelectContactActivity() {
        Intent intent = new Intent(MainActivity.this, SelectContactActivity.class);
        startActivity(intent);
    }

    private void setupTabIcons() {
        dataBinding.tabs.getTabAt(0).setIcon(tabIcons[0]);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(), "");
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


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    private void connectConnection() {

    }
}
