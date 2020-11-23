package com.ozonetech.ozochat.view.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.databinding.ActivitySelectContactBinding;
import com.ozonetech.ozochat.databinding.FragmentChatsBinding;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.UserRecentChatListener;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.MobileObject;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.network.SoketService;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.SelectContactActivity;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.adapter.ChatRoomsAdapter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.socket.emitter.Emitter;

import static android.os.Looper.getMainLooper;
public class ChatsFragment extends BaseFragment implements UserRecentChatListener, ContactsListener, ChatRoomsAdapter.ClickListener {

    FragmentChatsBinding dataBinding;
    private ChatRoomsAdapter mAdapter;
    private String tag = "ChatsFragment";
    UserChatListModel userChatListModel;
    MyPreferenceManager myPreferenceManager;
    private Contacts contactsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        userChatListModel = ViewModelProviders.of(ChatsFragment.this).get(UserChatListModel.class);
        myPreferenceManager = new MyPreferenceManager(getActivity());
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        prefManager = new MyPreferenceManager(getContext());
        selectUsers = new ArrayList<Contacts>();
        if(prefManager.getArrayListContact(prefManager.KEY_CONTACTS)==null){
            requestContactPermission();
        }else{
            getrecentChat();
        }
        return view;
    }

    private void renderUserChatList() {
        Map<String, String> chatMap = new HashMap<>();
        chatMap.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        chatMap.put("sender_id", "103");
        chatMap.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        showProgressDialog("Please wait...");
        userChatListModel.getUserResentChat(getActivity(), userChatListModel.userRecentChatListener = this, chatMap);
    }

    private void setRecyclerView(ArrayList<ChatRoom> chatRoomList) {

        ArrayList<Contacts> myContactsArrayList = new ArrayList<>();
        myContactsArrayList = myPreferenceManager.getArrayListContact(myPreferenceManager.KEY_CONTACTS);
        Log.d(tag, "---myContactsArrayList : " + myContactsArrayList);

        for (int i = 0; i < chatRoomList.size(); i++) {

            if (chatRoomList.get(i).getOneToOne() == 1) {
                String contactMobileNo = chatRoomList.get(i).getMobile();

                boolean flag = true;
                for (int j = 0; j < myContactsArrayList.size(); j++) {

                    String myContactMobileNo = myContactsArrayList.get(j).getPhone();
                    if (myContactMobileNo.equalsIgnoreCase(contactMobileNo)) {
                        String myContactName = myContactsArrayList.get(j).getName();
                        chatRoomList.get(i).setUsername(myContactName);
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }
                }
                if (!flag) {
                    chatRoomList.get(i).setUsername(contactMobileNo);
                }

                } else if (chatRoomList.get(i).getOneToOne() == 0) {
                    chatRoomList.get(i).setUsername(chatRoomList.get(i).getGroupName());
                }
            }
            mAdapter = new ChatRoomsAdapter(getActivity(), chatRoomList,ChatsFragment.this);
            dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            dataBinding.recyclerView.setAdapter(mAdapter);
/*
            dataBinding.recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), dataBinding.recyclerView, new ChatRoomsAdapter.ClickListener() {
*/
/*
                @Override
                public void onClick(View view, int position) {
                    // when chat is clicked, launch full chat thread activity
                    Log.d(tag, "---chat user click--" + position);
                    ChatRoom chatRoom = chatRoomList.get(position);
                    Intent intent = new Intent(getActivity(), UserChatActivity.class);
                    intent.putExtra("chat_room_id", chatRoom.getGroupId());
                    intent.putExtra("name", chatRoom.getUsername());
                    intent.putExtra("profilePic", chatRoom.getProfilePicture());
                    intent.putExtra("mobileNo", chatRoom.getMobile());
                    intent.putExtra("admin_id", chatRoom.getAdminId());
                    intent.putExtra("status", "Online");
                    intent.putExtra("flag", "user");
                    intent.putExtra("activityFrom", "MainActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                }
*//*


*/
/*
                @Override
                public void onLongClick(View view, int position) {

                }
*//*


            }));
*/


    }


    @Override
    public void onResume() {
        super.onResume();
       /* myPreferenceManager = new MyPreferenceManager(getContext());
        Log.d(tag, "=====token--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        Log.d(tag, "=====id--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        if (MyApplication.getInstance().iSocket.connected()) {
          //  getMessage();
        } else {
            Log.d(tag, "=====not connected--");
        }
        chatViewModel.getchat(getContext(),myPreferenceManager.getUserId());*/
        //  renderUserChatList();




    }

    private void getrecentChat() {
        JSONObject json = new JSONObject();
        try {
            json.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
            MyApplication.getInstance().getSocket().emit("recentChatEvent", json).on("recentChatEvent", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "--recent list -data-array-" + args[0]);
                    JSONObject data = (JSONObject) args[0];
                    // final JSONArray result = (JSONArray) args[0];
                    new Handler(getMainLooper())
                            .post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(tag, "--recent  -data-array-" + data.toString());
                                            try {
                                                setList(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            );

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setList(JSONObject data) throws JSONException {
        if (data.getInt("status") == 200 && data.getInt("success") == 1) {
            ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
            JSONArray jsonArray = data.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //            "uid": 111,
                //            "admin_id": 100,
                //            "group_id": "GP1605155439153",
                //            "oneToOne": 1,
                //            "group_name": "9922803527,7023500608",
                //            "usermobile": "7023500608",
                //            "profile_image": "",
                //            "message": "hello praveen sir",
                //            "title": null,
                //            "mobile": "7023500608"
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setUid(jsonObject.getInt("uid"));
                chatRoom.setAdminId(jsonObject.getInt("admin_id"));
                chatRoom.setGroupId(jsonObject.getString("group_id"));
                chatRoom.setOneToOne(jsonObject.getInt("oneToOne"));
                chatRoom.setGroupName(jsonObject.getString("group_name"));
                chatRoom.setUsername(jsonObject.getString("usermobile"));
                chatRoom.setLastMessage(jsonObject.getString("message"));
                chatRoom.setProfilePicture(jsonObject.getString("profile_image"));
                chatRoom.setTitle(jsonObject.getString("title"));
                chatRoom.setMobile(jsonObject.getString("mobile"));
                chatRoom.setFile(jsonObject.getString("file"));
                chatRoom.setTimestamp(jsonObject.getString("last_seen"));
                chatRoom.setUnreadCount(i + 1);
                chatRoomList.add(chatRoom);
            }
            if (chatRoomList.size() != 0) {
                setRecyclerView(chatRoomList);
                dataBinding.llStartChat.setVisibility(View.GONE);
            } else {
                dataBinding.llStartChat.setVisibility(View.VISIBLE);
            }
        } else {
            dataBinding.llStartChat.setVisibility(View.VISIBLE);
            Log.d(tag, "----no recent--");
        }
    }

    @Override
    public void onUserRecentChatSuccess(LiveData<UserChatListModel> userChatListResponse) {
        userChatListResponse.observe(getViewLifecycleOwner(), new Observer<UserChatListModel>() {
            @Override
            public void onChanged(UserChatListModel userChatListModel) {
                hideProgressDialog();
                try {
                    if (userChatListModel.getSuccess()) {
                        Log.d("ChatFragment", "--Response : Code" + userChatListModel.getChatRoom());

                        if (userChatListModel.getChatRoom() != null) {
                            ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
                            chatRoomList = (ArrayList<ChatRoom>) userChatListModel.getChatRoom();
                            if (chatRoomList.size() != 0) {
                                setRecyclerView(chatRoomList);
                                dataBinding.llStartChat.setVisibility(View.GONE);
                            } else {
                                dataBinding.llStartChat.setVisibility(View.VISIBLE);
                            }
                        } else {
                            dataBinding.llStartChat.setVisibility(View.VISIBLE);
                        }


                    } else {
                        showSnackbar(dataBinding.flChat, userChatListModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    Cursor phones;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    MyPreferenceManager prefManager;
    ArrayList<Contacts> selectUsers;

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    ActivityCompat.requestPermissions(getActivity(),
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
                showSnackbar(dataBinding.llStartChat, "Until you grant the permission, we canot display the names", Snackbar.LENGTH_SHORT);
            }
        }
    }

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

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {
        verifiedContactsResponse.observe(this, new Observer<VerifiedContactsModel>() {
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
                            getrecentChat();

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
                    hideProgressDialog();
                }
            }
        });

    }



    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {

    }

    @Override
    public void onClick(View view, int position, ChatRoom chatRoom) {
        Log.d(tag, "---chat user click--" + position);
       // ChatRoom chatRoom = chatRoomList.get(position);
        Intent intent = new Intent(getActivity(), UserChatActivity.class);
        intent.putExtra("admin_id", chatRoom.getAdminId());
        intent.putExtra("chat_room_id", chatRoom.getGroupId());
        intent.putExtra("group_image",chatRoom.getGroupImage());
        intent.putExtra("group_name",chatRoom.getGroupName());
        intent.putExtra("last_seen",chatRoom.getTimestamp());
        intent.putExtra("mobileNo", chatRoom.getMobile());
        intent.putExtra("oneToOne",chatRoom.getOneToOne());
        intent.putExtra("profilePic", chatRoom.getProfilePicture());
        intent.putExtra("uid",chatRoom.getUid());
        intent.putExtra("usermobile",chatRoom.getUsermobile());
        intent.putExtra("name", chatRoom.getUsername());
        intent.putExtra("status", "Online");
        intent.putExtra("flag", "user_chat");
        intent.putExtra("activityFrom", "MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {

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
        showProgressDialog("Please wait...");
        arrayListAge.setSender_id(prefManager.getUserId());
        for (int i = 0; i < arrayListAge.getMobile().size(); i++) {
            Log.d(tag, "--contact-" + arrayListAge.getMobile().get(i).getMobiles());
        }
        userChatListModel.sendContacts(getContext(), userChatListModel.contactsListener = this, arrayListAge);
    }

    @Override
    public void onSocketConnect(boolean flag) {
        super.onSocketConnect(flag);
        if (flag){
            Log.d(tag,"----- connect socket--");
            if(prefManager.getArrayListContact(prefManager.KEY_CONTACTS)==null){
                requestContactPermission();
            }else{
                getrecentChat();
            }
        }else{
            Log.d(tag,"-----re connect socket--");
           SoketService.instance.connectConnection();
        }
    }
}