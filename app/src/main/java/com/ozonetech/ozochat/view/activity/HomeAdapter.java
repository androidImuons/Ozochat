package com.ozonetech.ozochat.view.activity;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.network.AppCommon;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    Activity mActivity;
    ArrayList<String> list;
    public  HomeAdapter(Activity statusTest, ArrayList<String> list) {
        this.mActivity = statusTest;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        return new HomeAdapter.HomeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
       // holder.movieImage.setController(AppCommon.getInstance(mActivity).getDraweeController(holder.movieImage , String.valueOf("file:/"+list.get(position)), 500));
        File imageFile = new File(list.get(position));
        boolean isValidFile = imageFile.isFile();
        if (isValidFile){
            holder.movieImage.setController(AppCommon.getInstance(mActivity).getDraweeController(holder.movieImage , String.valueOf(Uri.fromFile(imageFile)), 500));
           // holder.movieImage.setImageURI(Uri.fromFile(imageFile));
        }
      //  holder.movieImage.setImageURI(list.get(position));
    }

    @Override
    public int getItemCount() {
        if(list.size()>300){
            return 100;
        }else{
            return list.size();
        }

    }


    public class HomeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieImage)
        SimpleDraweeView movieImage;
        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.movieImage)
        void getImage(){
            if(mActivity instanceof AllImageGrid){
                ((AllImageGrid)mActivity).setImage(getAdapterPosition());
            }
        }
    }
}
