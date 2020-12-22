package com.ozonetech.ozochat.view.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import com.ozonetech.ozochat.view.activity.PhotoVideoRedirectActivity;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private ImageView last_ivAudio;
    private Fetch fetch;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView timestamp;
        TextView txt_sender_name;
        ImageView iv_file;
        LinearLayout ll_download;
        TextView txt_file_size;
        ProgressBar progressBar;
        RelativeLayout rl_layer_file;
        LinearLayout ll_audio_layer;
        SeekBar sheekbar;
        ImageView iv_audio_donload;

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
            ll_audio_layer = (LinearLayout) itemView.findViewById(R.id.ll_audio_layer);
            sheekbar = (SeekBar) itemView.findViewById(R.id.sheekbar);
            iv_audio_donload = (ImageView) itemView.findViewById(R.id.iv_audio_donload);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        if (message.getMessage() != null && !message.getMessage().equals("null")) {
            ((ViewHolder) holder).message.setText(message.getMessage());
            ((ViewHolder) holder).message.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).rl_layer_file.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_audio_layer.setVisibility(View.GONE);
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

            viewVideoImage(message, (ViewHolder) holder, position);
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
        seekbarListner(message, (ViewHolder) holder, position);
    }

    private void seekbarListner(Message message, ViewHolder holder, int position) {
        holder.sheekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int x = (int) Math.ceil(progress / 1000f);
                if (x != 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    //clearMediaPlayer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // mediaPlayer.seekTo(holder.sheekbar.getProgress());
                }
            }
        });
    }

    private void viewVideoImage(Message message, ViewHolder holder, int position) {
        holder.rl_layer_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message1 = ChatDatabase.getInstance(mContext).chatMessageDao().getFile(message.getGroupId(), message.getId());

                Intent mIntent = new Intent(mContext, PhotoVideoRedirectActivity.class);
                mIntent.putExtra("PATH", "file://" + message1.getStorageFile());
                mIntent.putExtra("THUMB", "file://" + message1.getStorageFile());
                if (message1.getStorageFile().contains("mp4")) {
                    mIntent.putExtra("WHO", "video");
                } else {
                    mIntent.putExtra("WHO", "Image");
                }
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), message.getStorageFile()));
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (message.getFile().contains("pdf")) {

                } else {
                    mContext.startActivity(mIntent);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showLocalDbFile(ViewHolder holder, Message message, int position) {

        if (message.getFile().contains("pdf")) {
            Log.d(tag, "-----file--" + message.getStorageFile());

            holder.ll_download.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            ((ViewHolder) holder).message.setVisibility(View.GONE);
            ((ViewHolder) holder).rl_layer_file.setVisibility(View.VISIBLE);
            holder.ll_audio_layer.setVisibility(View.GONE);
            try {
                showpdfFile(message, holder, position);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.getFile().contains("jpg")
                || message.getFile().contains("png")
                || message.getFile().contains("mp4")) {
            holder.ll_download.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            ((ViewHolder) holder).message.setVisibility(View.GONE);
            ((ViewHolder) holder).rl_layer_file.setVisibility(View.VISIBLE);
            holder.ll_audio_layer.setVisibility(View.GONE);
            RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
            Glide.with(mContext)
                    .load(message.getStorageFile())
                    .skipMemoryCache(true)
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .into(((ViewHolder) holder).iv_file);

            Log.d(tag, "--storage path 231--" + message.getStorageFile());
        } else if (message.getFile().contains("mp3") ||
                message.getFile().contains("pk")) {
            holder.ll_download.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            ((ViewHolder) holder).message.setVisibility(View.GONE);
            ((ViewHolder) holder).rl_layer_file.setVisibility(View.GONE);
            holder.ll_audio_layer.setVisibility(View.VISIBLE);
            holder.iv_audio_donload.setBackground(mContext.getDrawable(R.drawable.ic_stop));
            holder.iv_audio_donload.setTag(0);


            holder.iv_audio_donload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (last_ivAudio != null) {
                        last_ivAudio.setBackground(mContext.getDrawable(R.drawable.ic_stop));
                        last_ivAudio = holder.iv_audio_donload;
                    } else {
                        last_ivAudio = holder.iv_audio_donload;
                    }

                    if ((Integer) view.getTag() == 0) {
                        holder.iv_audio_donload.setTag(1);
                        holder.iv_audio_donload.setBackground(mContext.getDrawable(R.drawable.ic_play));
                        setAudioFile(message, holder, position);
                    } else {
                        holder.iv_audio_donload.setTag(0);
                        holder.iv_audio_donload.setBackground(mContext.getDrawable(R.drawable.ic_stop));
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                    }
                }
            });

        }
    }

    private void showpdfFile(Message message, ViewHolder holder, int position) throws IOException {
        Bitmap bitmap = null;
        ContentResolver contentResolver = mContext.getContentResolver();
        ParcelFileDescriptor fileDescriptor = contentResolver.openFileDescriptor(Uri.parse("/storage/emulated/0/storage/emulated/0/Download/OZOCHAT/8277.pdf"), "r");
        if (fileDescriptor != null) {
            PdfRenderer pdfRenderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                pdfRenderer = new PdfRenderer(fileDescriptor);
                if (pdfRenderer != null) {
                    if (pdfRenderer.getPageCount() > 0) {
                        PdfRenderer.Page page = pdfRenderer.openPage(0);
                        // Set the width and height based on the desired preview size
                        // and the aspect ratio of the pdf page
                        int bitmapWidth = 250;
                        int bitmapHeight = 250;
                        // Create a white bitmap to make sure that PDFs without
                        // a background color are rendered on a white background
                        int[] colors = new int[]{mContext.getResources().getColor(R.color.red), mContext.getResources().getColor(R.color.colorBlue), mContext.getResources().getColor(R.color.green)};
                        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        page.close();
                        holder.iv_file.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    private void showFile(ViewHolder holder, Message message, int position) throws IOException {

        final int[] file_size = new int[1];
        if (message.getSize() == null) {
            new DownLoadImageTask(holder.txt_file_size, message).execute(message.getFile());
        } else {
            holder.txt_file_size.setText(message.getSize());
        }

        holder.ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadfile(holder, message, position);
            }
        });

        holder.iv_audio_donload.setOnClickListener(new View.OnClickListener() {
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

            holder.rl_layer_file.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_audio_layer.setVisibility(View.GONE);


            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(message.getFile())
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .thumbnail(Glide.with(mContext).load(message.getFile()))
                    .into(((ViewHolder) holder).iv_file);
        } else if (message.getFile().contains("jpg") ||
                message.getFile().contains("png") ||
                message.getFile().contains("mp4")) {
            holder.rl_layer_file.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_audio_layer.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(message.getFile())
                    .apply(options.centerCrop())
                    .placeholder(R.drawable.ic_file)
                    .thumbnail(Glide.with(mContext).load(message.getFile()))
                    .into(((ViewHolder) holder).iv_file);

        } else if (message.getFile().contains("mp3") || message.getFile().contains("pk")) {
            holder.rl_layer_file.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_audio_layer.setVisibility(View.VISIBLE);


        }
    }

    MediaPlayer mediaPlayer = new MediaPlayer();

    public void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    private void setAudioFile(Message message, ViewHolder holder, int position) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                holder.sheekbar.setProgress(0);
            }
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }

            mediaPlayer.setDataSource("file://" + message.getStorageFile());

            mediaPlayer.prepare();
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.setLooping(false);
            holder.sheekbar.setMax(mediaPlayer.getDuration());

            mediaPlayer.start();
            seekBar = holder.sheekbar;
            new Thread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SeekBar seekBar;

    public void run() {

        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();


        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            Log.d(tag, "---run method start-");
            if (seekBar != null) {
                seekBar.setProgress(currentPosition);
            }
        }
    }

    private void downloadfile(ViewHolder holder, Message message, int position) {
        holder.rl_layer_file.setTag(holder);
        String filename = message.getId().toString() + message.getFile().substring(message.getFile().lastIndexOf("."));

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
                        Uri uri;
                        File file_ = new File(Environment.getExternalStorageDirectory(),
                                filename);
                        //    uri = FileProvider.getUriForFile(mContext, mContext.getPackageName(), file_);
//                        uri = Uri.fromFile(file_);
                        Log.d(tag, "---file name download- 387-" + file);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        ChatDatabase.getInstance(mContext).chatMessageDao().updateStorage(file, message.getId());
                        try {
                            if (message.getFile().contains("pdf")) {
                                showpdfFile(ChatDatabase.getInstance(mContext).chatMessageDao().getFile(message.getGroupId(), message.getId()), holder, position);
                            } else {

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private class DownLoadImageTask extends AsyncTask<String, Void, String> {
        TextView txt_size;
        Message message;

        DownLoadImageTask(TextView txt_size, Message message) {
            this.txt_size = txt_size;
            this.message = message;
        }

        private final DecimalFormat format = new DecimalFormat("#.##");
        private static final long MiB = 1024 * 1024;
        private static final long KiB = 1024;

        protected String doInBackground(String... urls) {


            int file_size = 0;
            String size = null;
            try {
                URL myUrl = new URL(urls[0]);
                URLConnection urlConnection = null;
                urlConnection = myUrl.openConnection();
                urlConnection.connect();

                file_size = urlConnection.getContentLength();
                file_size = file_size / 1024;

                final double length = file_size;

                if (length > MiB) {
                    size = format.format(length / MiB) + " MiB";
                }
                if (length > KiB) {
                    size = format.format(length / KiB) + " KiB";
                }
                if (length < KiB) {
                    size = format.format(length) + " B";
                }

                Log.i("sasa", "file_size = " + size);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return size;
        }

        protected void onPostExecute(String result) {

            txt_size.setText(result);
            message.setSize(result);
        }
    }


    private void download(Message message, ViewHolder viewHolder) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(mContext)
                .setDownloadConcurrentLimit(3)
                .build();
         fetch = Fetch.Impl.getInstance(fetchConfiguration);
         // filename = message.getId().toString() + message.getFile().substring(message.getFile().lastIndexOf("."));
       String filename="1608179.jpg";
        String url = "https://ozochatapireplica.ozonetech.biz/uploads/1607670016077.pdf";
        String file = dirPath +"/"+ filename;
        final Request request = new Request(url, file);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

      //  request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
            Log.d(tag,"----fatch--");
            //fetch.addListener(fetchListener);
        }, error -> {
            //An error occurred enqueuing the request.
        });


//Remove listener when done.


    }
    FetchListener fetchListener = new FetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {

        }

        @Override
        public void onQueued(@NotNull Download download, boolean b) {

        }

        @Override
        public void onWaitingNetwork(@NotNull Download download) {

        }

        @Override
        public void onCompleted(@NotNull Download download) {
            download.getFileUri();
            Log.d(tag,"---761--"+download.getFileUri());
            Log.d(tag,"---762--"+download.getFile());
            Uri uri=Uri.fromFile(new File(download.getFile()));
            Log.d(tag,"---764--"+uri.toString());
            Uri uri1=Uri.parse(download.getFileUri().toString());
            Log.d(tag,"---766--"+uri1.toString());
            File file=new File(download.getFile());
            Log.d(tag,"---768--"+file.getAbsolutePath());
            fetch.removeListener(fetchListener);;
        }

        @Override
        public void onError(@NotNull Download download, @NotNull com.tonyodev.fetch2.Error error, @Nullable Throwable throwable) {

        }

        @Override
        public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

        }

        @Override
        public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

        }

        @Override
        public void onProgress(@NotNull Download download, long l, long l1) {

        }

        @Override
        public void onPaused(@NotNull Download download) {

        }

        @Override
        public void onResumed(@NotNull Download download) {

        }

        @Override
        public void onCancelled(@NotNull Download download) {

        }

        @Override
        public void onRemoved(@NotNull Download download) {

        }

        @Override
        public void onDeleted(@NotNull Download download) {

        }
    };

}
