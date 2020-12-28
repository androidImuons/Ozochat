package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.model.StatusModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyStatusViewAdapter extends RecyclerView.Adapter<MyStatusViewAdapter.ViewHolder> {

    Context context;
    ArrayList<StatusModel> imgStatusArraylist;

    public MyStatusViewAdapter(Context context, ArrayList<StatusModel> imgStatusArraylist) {
        this.context = context;
        this.imgStatusArraylist = imgStatusArraylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_status_row_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(imgStatusArraylist.get(position).getUploadedFileUrl())
                .placeholder(R.drawable.person_icon)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return imgStatusArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=itemView.findViewById(R.id.thumbnail);
        }
    }
}
