package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.database.ChatDatabase;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.utils.FileUtil;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.utils.ThubnailUtils;
import com.ozonetech.ozochat.view.activity.PhotoVideoRedirectActivity;
import com.ozonetech.ozochat.view.activity.UserChatActivity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import iamutkarshtiwari.github.io.ananas.editimage.utils.Utils;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();
    private final MyPreferenceManager prefManager;
    private final String dirPath;

    private int userId;
    private int SELF = 100;
    private int OTHER = 200;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;
    private String tag = "ChatRoomThreadAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView timestamp;
        TextView txt_sender_name;
        ImageView iv_file;
        LinearLayout ll_download;
        TextView txt_file_size;
        ProgressBar progressBar;
        RelativeLayout rl_layer_file;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txt_sender_name = itemView.findViewById(R.id.sender_name);
            iv_file = (ImageView) itemView.findViewById(R.id.iv_file);
            ll_download = (LinearLayout) itemView.findViewById(R.id.ll_download);
            txt_file_size = (TextView) itemView.findViewById(R.id.txt_file_size);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            rl_layer_file = (RelativeLayout) itemView.findViewById(R.id.rl_layer_file);
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
        dirPath = FileUtil.getRootDirPath(mContext);
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
        int check_UserId = Integer.parseInt(message.getSender_id());
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
        if (message.getMessage() != null && !message.getMessage().equals("null")) {
            ((ViewHolder) holder).message.setText(message.getMessage());
            ((ViewHolder) holder).message.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).rl_layer_file.setVisibility(View.GONE);
            Log.d(tag, "----message avail" + message.getMessage());
        } else if (message.getFile() != null) {

            try {
                if (message.getStorageFile() != null) {
                    showLocalDbFile(((ViewHolder) holder), message, position);
                    Log.d(tag, "----file avail" + message.getStorageFile());
                } else {
                    Log.d(tag, "----file avail" + message.getFile());
                    showFile(((ViewHolder) holder), message, position);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            viewVideoImage(message, (ViewHolder) holder,position);
        }


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

    private void viewVideoImage(Message message, ViewHolder holder, int position) {
        holder.rl_layer_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Message message1=ChatDatabase.getInstance(mContext).chatMessageDao().getFile(message.getGroupId(), message.getId());

                Intent mIntent = new Intent(mContext, PhotoVideoRedirectActivity.class);
                mIntent.putExtra("PATH","file://"+message1.getStorageFile());
                mIntent.putExtra("THUMB","file://"+message1.getStorageFile());
                if (message1.getStorageFile().contains("mp4")){
                    mIntent.putExtra("WHO", "video");
                }else{
                    mIntent.putExtra("WHO", "Image");
                }
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(mIntent);
            }
        });
    }

    private void showLocalDbFile(ViewHolder holder, Message message, int position) {
        holder.ll_download.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
        ((ViewHolder) holder).message.setVisibility(View.GONE);
        ((ViewHolder) holder).rl_layer_file.setVisibility(View.VISIBLE);
        if (message.getFile().contains("pdf") || message.getFile().contains("txt")
                || message.getFile().contains("xml")
                || message.getFile().contains("html")
                || message.getFile().contains("rtf")
                || message.getFile().contains("csv")
                || message.getFile().contains("doc")
                || message.getFile().contains("xls")
                || message.getFile().contains("xlsx")
                || message.getFile().contains("zip")) {
            Log.d(tag, "-----file--" + message.getStorageFile());
            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(message.getStorageFile())
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .thumbnail(Glide.with(mContext).load(message.getStorageFile()))
                    .into(((ViewHolder) holder).iv_file);
            File file = new File(message.getStorageFile());
            Log.d(tag, "----getAbsolutePath path--" + file.getAbsolutePath().trim());
            //      holder.iv_file.setImageBitmap(ThubnailUtils.getPictureImage(message.getFile().trim()));

        } else if (message.getStorageFile().contains("jpg")
                || message.getStorageFile().contains("png")
                || message.getStorageFile().contains("mp4")) {
            RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
            Glide.with(mContext)
                    .load("file://" + message.getStorageFile())
                    .skipMemoryCache(true)
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .into(((ViewHolder) holder).iv_file);

            Log.d(tag, "--storage path 231--" + message.getStorageFile());
        }
    }


    private void showFile(ViewHolder holder, Message message, int position) throws IOException {
        holder.rl_layer_file.setVisibility(View.VISIBLE);
        holder.message.setVisibility(View.GONE);
        final int[] file_size = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myUrl = new URL(message.getFile());
                    URLConnection urlConnection = myUrl.openConnection();
                    urlConnection.connect();
                    file_size[0] = urlConnection.getContentLength();

                    file_size[0] = file_size[0] / 1000;
                    Log.i("sasa", "file_size = " + file_size[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        holder.txt_file_size.setText(file_size[0] + " KB");
        holder.ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadfile(holder, message, position);
            }
        });


        if (message.getFile().contains("pdf") || message.getFile().contains("txt")
                || message.getFile().contains("xml")
                || message.getFile().contains("html")
                || message.getFile().contains("rtf")
                || message.getFile().contains("csv")
                || message.getFile().contains("doc")
                || message.getFile().contains("xls")
                || message.getFile().contains("xlsx")
                || message.getFile().contains("zip")) {
            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(message.getFile())
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .thumbnail(Glide.with(mContext).load(message.getFile()))
                    .into(((ViewHolder) holder).iv_file);
        } else if (message.getFile().contains("jpg") || message.getFile().contains("png") || message.getFile().contains("mp4")) {

            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(message.getFile())
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .thumbnail(Glide.with(mContext).load(message.getFile()))
                    .into(((ViewHolder) holder).iv_file);

        } else if (message.getFile().contains("mp4")) {

        }


    }

    private void downloadfile(ViewHolder holder, Message message, int position) {
        holder.rl_layer_file.setTag(holder);
        String filename = message.getId().toString() + message.getFile().substring(message.getFile().lastIndexOf("."));
        ;
        Log.d(tag, "----file generate---" + filename);
        int downloadid = PRDownloader.download(message.getFile(), dirPath, filename).build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
            @Override
            public void onStartOrResume() {
                holder.ll_download.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }
        }).setOnPauseListener(new OnPauseListener() {
            @Override
            public void onPause() {
                holder.ll_download.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }
        })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        holder.ll_download.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        holder.progressBar.setProgress((int) progressPercent);
                        holder.progressBar.setIndeterminate(false);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        String file = dirPath + "/" + filename;
                        Log.d(tag, "---file name download--" + file);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        ChatDatabase.getInstance(mContext).chatMessageDao().updateStorage(file, message.getId());
                    }

                    @Override
                    public void onError(Error error) {
                        holder.ll_download.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
        holder.progressBar.setTag(downloadid);
        holder.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (Integer) view.getTag();
                PRDownloader.cancel(id);
                holder.ll_download.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }
        });
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
