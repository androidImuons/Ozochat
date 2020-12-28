package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.StatusModel;
import com.ozonetech.ozochat.model.StatusResponseModel;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoriesActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    //private static final int PROGRESS_COUNT = 6;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
    ArrayList<StatusModel> imgStatusArraylist;
    private MyPreferenceManager myPreferenceManager;
    CircleImageView thumbnail;
    TextView tvStatusText;

    private int counter = 0;

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };

    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stories);
        myPreferenceManager = new MyPreferenceManager(StoriesActivity.this);
        imgStatusArraylist = (ArrayList<StatusModel>) getIntent().getSerializableExtra("imgStatusArraylist");

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(imgStatusArraylist.size());
        storiesProgressView.setStoryDuration(5000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        //counter = 0;
        storiesProgressView.startStories(counter);

        tvStatusText=(TextView)findViewById(R.id.tvStatusText);
        tvStatusText.setText(imgStatusArraylist.get(counter).getCaption());

        thumbnail=(CircleImageView)findViewById(R.id.thumbnail);
        Glide.with(StoriesActivity.this)
                    .load(myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_PROFILE_PIC))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.person_icon)
                .into(thumbnail);

        image = (ImageView) findViewById(R.id.image);
        Glide.with(this)
                .load(imgStatusArraylist.get(counter).getUploadedFileUrl())
                .placeholder(R.mipmap.ic_launcher_logo)
                .into(image);

        //image.setImageResource(resources[counter]);


        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
        int pos = ++counter;
        Glide.with(this)
                .load(imgStatusArraylist.get(pos).getUploadedFileUrl())
                .placeholder(R.mipmap.ic_launcher_logo)
                .into(image);

        tvStatusText.setText(imgStatusArraylist.get(pos).getCaption());

        // image.setImageResource(resources[++counter]);
    }

    @Override
    public void onPrev() {
        int pos = --counter;
        if ((counter - 1) < 0) return;
        Glide.with(this)
                .load(imgStatusArraylist.get(pos).getUploadedFileUrl())
                .placeholder(R.mipmap.ic_launcher_logo)
                .into(image);

        tvStatusText.setText(imgStatusArraylist.get(pos).getCaption());

        //  image.setImageResource(resources[--counter]);
    }

    @Override
    public void onComplete() {
        finish();
    }


    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}