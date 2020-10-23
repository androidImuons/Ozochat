package com.ozonetech.ozochat.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ozonetech.ozochat.R;


/**
 * Created by sotsys016-2 on 13/8/16 in com.cnc3camera.
 */
public class PhotoVideoRedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideo_redirect);


        init();

    }
    VideoView videoView;
    private void init() {

        SimpleDraweeView imgShow =  findViewById(R.id.imgShow);
        videoView = (VideoView) findViewById(R.id.vidShow);

        if(getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")){

            imgShow.setVisibility(View.VISIBLE);

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
        }else {

            videoView.setVisibility(View.VISIBLE);
            try {
                videoView.setMediaController(null);
                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")));
            } catch (Exception e){
                e.printStackTrace();
            }
            videoView.requestFocus();
            //videoView.setZOrderOnTop(true);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
        }











    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        super.onBackPressed();
    }
}
