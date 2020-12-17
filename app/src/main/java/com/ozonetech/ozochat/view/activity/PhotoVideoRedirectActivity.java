package com.ozonetech.ozochat.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozonetech.ozochat.R;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


/**
 * Created by sotsys016-2 on 13/8/16 in com.cnc3camera.
 */
public class PhotoVideoRedirectActivity extends AppCompatActivity {

    private SimpleDraweeView imgShow;
    private String url;
    private String tag="PhotoVideoRedirect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideo_redirect);


        init();

    }

    VideoView videoView;
    VideoPlayerView videoPlayerView;

    private void init() {

        imgShow = findViewById(R.id.imgShow);
        videoView = (VideoView) findViewById(R.id.vidShow);
        videoPlayerView = findViewById(R.id.video_player);
        if (getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")) {
            imgShow.setVisibility(View.GONE);
            videoPlayerView.setVisibility(View.GONE);
            imgShow.setVisibility(View.VISIBLE);
            url = getIntent().getStringExtra("PATH");
            Log.d(tag,"----url--"+url);
            imgShow.setImageURI(getIntent().getStringExtra("PATH"));
            /*Glide.with(PhotoVideoRedirectActivity.this)
                    .load(getIntent().getStringExtra("PATH"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).placeholder(R.drawable.ic_photo_cont)
                    .into(imgShow);*/
        } else {
            url = getIntent().getStringExtra("PATH");
            Log.d(tag,"----url--"+url);
            imgShow.setVisibility(View.VISIBLE);
            videoPlayerView.setVisibility(View.VISIBLE);
            showVideo();
//            videoView.setVisibility(View.VISIBLE);
//            try {
//                videoView.setMediaController(null);
//                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            videoView.requestFocus();
//            //videoView.setZOrderOnTop(true);
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                public void onPrepared(MediaPlayer mp) {
//
//                    videoView.start();
//                }
//            });
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    videoView.start();
//                }
//            });


        }

    }

    private void showVideo() {
        videoPlayerView.onVideoStoppedMainThread();

        videoPlayerView.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
                imgShow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
                imgShow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
                imgShow.setVisibility(View.VISIBLE);
            }
        });

        if (videoPlayerManager != null) {
            videoPlayerManager.stopAnyPlayback();
        }
        videoPlayerManager.playNewVideo(null, videoPlayerView, url);

    }

    VideoPlayerManager videoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData currentItemMetaData) {

        }
    });

    @Override
    public void onBackPressed() {
        videoPlayerView.onVideoStoppedMainThread();
        super.onBackPressed();
    }
}
