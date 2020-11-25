package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.SelectedListItemBinding;
import com.ozonetech.ozochat.viewmodel.Contacts;

import java.util.List;

public class SelectedGrpMemberAdpter extends RecyclerView.Adapter<SelectedGrpMemberAdpter.MyViewHolder> {

    Context context;
    private List<Contacts> selectedMembers;
    private LayoutInflater layoutInflater;
    private SelectedAdpterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final SelectedListItemBinding binding;

        public MyViewHolder(final SelectedListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

    public SelectedGrpMemberAdpter(Context context, List<Contacts> selectedMembers, SelectedAdpterListener listener) {
        this.context=context;
        this.selectedMembers = selectedMembers;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SelectedListItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.selected_list_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(context)
                .load(selectedMembers.get(position).getProfilePicture())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.person_icon)
                .into(holder.binding.thumbnail);
        holder.binding.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickedMember(selectedMembers.get(position));
                }
            }
        });

       holder.binding.ivCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listener.onRemoveMember(selectedMembers.get(position));
           }
       });
    }

    @Override
    public int getItemCount() {
        return selectedMembers.size();
    }

    public interface SelectedAdpterListener {
        void onClickedMember(Contacts contacts);

        void onRemoveMember(Contacts contacts);
    }
}
