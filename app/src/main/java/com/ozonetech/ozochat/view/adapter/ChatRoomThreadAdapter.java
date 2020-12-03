package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();
    private final MyPreferenceManager prefManager;

    private int userId;
    private int SELF = 100;
    private int OTHER = 200;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView timestamp;
        TextView txt_sender_name;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txt_sender_name = itemView.findViewById(R.id.sender_name);
        }
    }

    UserChatActivity userChatActivity;

    public ChatRoomThreadAdapter(UserChatActivity mContext, ArrayList<Message> messageArrayList, String userId) {
        this.mContext = mContext.getApplicationContext();
        onMessageContactClick = (onMessageContactClick) mContext;
        this.messageArrayList = messageArrayList;
        this.userId = Integer.parseInt(userId);
        userChatActivity = mContext;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); //2020-11-04T10:25:31.000Z
        prefManager = new MyPreferenceManager(mContext.getApplicationContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else if (viewType == OTHER) {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        int check_UserId = message.getUserId();
        if (check_UserId == userId) {
            return SELF;
        }
        if (check_UserId != userId) {
            return OTHER;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder) holder).message.setText(message.getMessage());
        Log.d("group", "--is group yes-" + userChatActivity.groupChat);
        if (userChatActivity.groupChat == 0) {
            if (prefManager.getArrayListContact(prefManager.KEY_CONTACTS) != null) {
                Log.d("contact array list", "--is group-" + prefManager.getArrayListContact(prefManager.KEY_CONTACTS));

                setName(((ViewHolder) holder).txt_sender_name, message);
            } else {
                message.setIs_contact(false);
                ((ViewHolder) holder).txt_sender_name.setText(message.getSender_mobile() + "         " + message.getSender_name());
            }
            ((ViewHolder) holder).txt_sender_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMessageContactClick.onContactClick(message);
                }
            });
        } else {
            ((ViewHolder) holder).txt_sender_name.setVisibility(View.GONE);
        }
        String timestamp = message.getCreated();
        if (timestamp.contains("T")) {
            timestamp = timestamp.replace("T", " ");
        }
        // timestamp=timestamp.replace(".000Z","");
        timestamp = getTimeStamp(timestamp);

        ((ViewHolder) holder).timestamp.setText(timestamp);
    }

    private void setName(TextView holder, Message message) {
        boolean flag = true;
        for (int j = 0; j < prefManager.getArrayListContact(prefManager.KEY_CONTACTS).size(); j++) {

            String myContactMobileNo = prefManager.getArrayListContact(prefManager.KEY_CONTACTS).get(j).getPhone();
            Log.d("chatRoom", "--contact list--" + myContactMobileNo);
            Log.d("chatRoom", "--usernumber-" + message.getSender_mobile());

            if (myContactMobileNo.equalsIgnoreCase(message.getSender_mobile())) {
                String myContactName = prefManager.getArrayListContact(prefManager.KEY_CONTACTS).get(j).getName();
                holder.setText(myContactName);
                message.setIs_contact(true);
                return;
            } else {
                holder.setText(message.getSender_mobile() + "         " + message.getSender_name());
                flag = false;
            }
        }

    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    onMessageContactClick onMessageContactClick;

    public interface onMessageContactClick {


        void onContactClick(Message message);
    }
}
