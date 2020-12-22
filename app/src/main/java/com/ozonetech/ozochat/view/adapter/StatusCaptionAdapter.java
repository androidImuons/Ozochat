package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;

import java.util.ArrayList;

public class StatusCaptionAdapter extends RecyclerView.Adapter<StatusCaptionAdapter.ViewHolder> {

    Context context;
    ArrayList<String> captionArraylist;
    StatusCaptionInterface listener;

    public StatusCaptionAdapter(Context context,  ArrayList<String> captionArraylist,StatusCaptionInterface listener) {
        this.context = context;
        this.captionArraylist = captionArraylist;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_caption_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(captionArraylist.get(position).equalsIgnoreCase("")){
            holder.caption.setText("Add a caption....");
        }else {
            holder.caption.setText(captionArraylist.get(position));
        }

        holder.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editCaption(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return captionArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView caption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            caption = (TextView) itemView.findViewById(R.id.caption);
        }
    }

    public interface StatusCaptionInterface{

        public void editCaption(int postion);
    }
}
