package com.ozonetech.ozochat.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;

import java.util.ArrayList;

public class AllImageGrid extends Activity {

    RecyclerView MainRecyclerView ;
    ArrayList<String> myList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengrid);
        myList = (ArrayList<String>) getIntent().getSerializableExtra("imageList");
        MainRecyclerView = findViewById(R.id.MainRecyclerView);
        MainRecyclerView.setLayoutManager(new GridLayoutManager(AllImageGrid.this, 4));
        MainRecyclerView.setAdapter(new HomeAdapter(AllImageGrid.this , myList));
        MainRecyclerView.scrollToPosition(0);
    }

    public void setImage(int adapterPosition) {
        Intent i = new Intent();
        i.putExtra("pos" , adapterPosition);
        setResult(99 , i);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }
}
