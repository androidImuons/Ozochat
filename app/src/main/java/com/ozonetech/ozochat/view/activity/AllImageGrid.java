package com.ozonetech.ozochat.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.SelectionImageList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllImageGrid extends Activity {

    RecyclerView MainRecyclerView;
    ArrayList<String> myList;
    ArrayList<SelectionImageList> selectionImageLists = new ArrayList<>();
    HomeAdapter homeAdapter;
    @BindView(R.id.save)
    ImageView saveBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengrid);
        ButterKnife.bind(this);
        myList = (ArrayList<String>) getIntent().getSerializableExtra("imageList");
        if (myList.size() != 0) {
            for (int i = 0; i < myList.size(); i++)
                selectionImageLists.add(new SelectionImageList(myList.get(i)));
        }
        MainRecyclerView = findViewById(R.id.MainRecyclerView);
        MainRecyclerView.setLayoutManager(new GridLayoutManager(AllImageGrid.this, 4));
        homeAdapter = new HomeAdapter(selectionImageLists, AllImageGrid.this);
        MainRecyclerView.setAdapter(homeAdapter);
        MainRecyclerView.scrollToPosition(0);
    }

    public void setImage(int adapterPosition) {

        if (selectionImageLists.get(adapterPosition).isSelected())
            selectionImageLists.get(adapterPosition).setSelected(false);
        else
            selectionImageLists.get(adapterPosition).setSelected(true);
        homeAdapter.update(selectionImageLists);
        boolean anySelect = false;
        for (int i = 0; i < selectionImageLists.size(); i++) {
            if (selectionImageLists.get(i).isSelected()) {
                anySelect = true;
                break;
            }
        }
        if (anySelect) {
            saveBtn.setVisibility(View.VISIBLE);
        } else
            saveBtn.setVisibility(View.GONE);

    }

    @OnClick(R.id.save)
    void setSaveBtn() {
        ArrayList<String> selectedList = new ArrayList<>();
        for (int i = 0; i < selectionImageLists.size(); i++) {
            if (selectionImageLists.get(i).isSelected()) {
                selectedList.add(selectionImageLists.get(i).getImagePath());
            }
        }
        Intent i = new Intent();
        i.putExtra("pos", selectedList);
        setResult(99, i);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }
}
