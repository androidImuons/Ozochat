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
import com.ozonetech.ozochat.model.SelectionImageList;
import com.ozonetech.ozochat.network.AppCommon;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    Activity mActivity;
    ArrayList<String> list;
    ArrayList<SelectionImageList> selectionImageLists;

    public HomeAdapter(Activity statusTest, ArrayList<String> list) {
        this.mActivity = statusTest;
        this.list = list;
    }

    public HomeAdapter(ArrayList<SelectionImageList> selectionImageLists, Activity activity) {
        this.mActivity = activity;
        this.selectionImageLists = selectionImageLists;
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

        if (mActivity instanceof AllImageGrid) {
            File imageFile = new File(selectionImageLists.get(position).getImagePath());
            boolean isValidFile = imageFile.isFile();
            if (isValidFile) {
                holder.movieImage.setController(AppCommon.getInstance(mActivity).getDraweeController(holder.movieImage, String.valueOf(Uri.fromFile(imageFile)), 500));
                // holder.movieImage.setImageURI(Uri.fromFile(imageFile));
            }
            if (selectionImageLists.get(position).isSelected())
                holder.selectionView.setVisibility(View.VISIBLE);
            else
                holder.selectionView.setVisibility(View.GONE);

        } else {
            File imageFile = new File(list.get(position));
            boolean isValidFile = imageFile.isFile();
            if (isValidFile) {
                holder.movieImage.setController(AppCommon.getInstance(mActivity).getDraweeController(holder.movieImage, String.valueOf(Uri.fromFile(imageFile)), 500));
                // holder.movieImage.setImageURI(Uri.fromFile(imageFile));
            }
            holder.selectionView.setVisibility(View.GONE);
        }
        //  holder.movieImage.setImageURI(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (mActivity instanceof AllImageGrid) {
           return selectionImageLists.size();
        }else
        return list.size();
    }

    public void update(ArrayList<SelectionImageList> selectionImageLists) {
        this.selectionImageLists = selectionImageLists;
        notifyDataSetChanged();
    }


    public class HomeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieImage)
        SimpleDraweeView movieImage;
        @BindView(R.id.selectionView)
        View selectionView;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.movieImage)
        void getImage() {
            if (mActivity instanceof AllImageGrid) {
                ((AllImageGrid) mActivity).setImage(getAdapterPosition());

            }
        }

        @OnClick(R.id.selectionView)
        void setSelectionView() {

        }
    }
}
