package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.viewmodel.Contacts;

import java.util.ArrayList;
import java.util.List;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MyViewHolder> implements Filterable {

    private Context context;

    private List<Contacts> contactList;
    private List<Contacts> contactListFiltered;
    private ContactsAdapterListener listener;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;


    public AddMemberAdapter(Context context, List<Contacts> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_member_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contacts contact = contactListFiltered.get(position);
        holder.tvContactName.setText(contact.getName());
        holder.tvContactStatus.setText(contact.getStatus());
        // change the row state to activated
        holder.ll_contact_row.setActivated(selectedItems.get(position, false));

        Glide.with(context)
                .load(contact.getProfilePicture())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.person_icon)
                .into(holder.thumbnail);

        // handle icon animation
        applyIconAnimation(holder, position);

    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iv_selected.setVisibility(View.VISIBLE);
            listener.onAddMemeber(contactListFiltered.get(position));

        } else {
            holder.iv_selected.setVisibility(View.GONE);
            listener.onRemoveMemeber(contactListFiltered.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public String getData(int position) {
        String mobile = contactListFiltered.get(position).getPhone();
        return mobile;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);

        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);

        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvContactName, tvContactStatus;
        public ImageView thumbnail, iv_selected;
        LinearLayout ll_contact_row;

        public MyViewHolder(View view) {
            super(view);
            tvContactName = view.findViewById(R.id.tvContactName);
            tvContactStatus = view.findViewById(R.id.tvContactStatus);
            thumbnail = view.findViewById(R.id.thumbnail);
            iv_selected = view.findViewById(R.id.iv_selected);
            ll_contact_row = view.findViewById(R.id.ll_contact_row);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Contacts> filteredList = new ArrayList<>();
                    for (Contacts row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Contacts contact,int adapterPosition);

        void onAddMemeber(Contacts contacts);

        void onRemoveMemeber(Contacts contacts);
    }


}