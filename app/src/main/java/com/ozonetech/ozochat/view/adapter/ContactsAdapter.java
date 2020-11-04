package com.ozonetech.ozochat.view.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ContactlistRowLayoutBinding;
import com.ozonetech.ozochat.viewmodel.Contacts;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable{

    Context context;
    private LayoutInflater layoutInflater;
    public List<Contacts> contacts;
    private ArrayList<Contacts> contactsArrayList;
    private List<Contacts> contactListFiltered;
    private ContactsAdapterListener listener;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;
private boolean isGroupCreate;

    public ContactsAdapter(Context context, LayoutInflater inflater, List<Contacts> items, ContactsAdapterListener listener, boolean is_group_create) {
        this.context = context;
        this.layoutInflater = inflater;
        this.contacts = items;
        this.contactsArrayList = new ArrayList<Contacts>();
        this.contactListFiltered = new ArrayList<Contacts>();
        this.contactsArrayList.addAll(contacts);
        this.contactListFiltered.addAll(contacts);
        this.listener = listener;
        this.isGroupCreate=is_group_create;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ContactlistRowLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.contactlist_row_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.setContacts(contactListFiltered.get(position));
        // change the row state to activated
        holder.binding.llContactRow.setActivated(selectedItems.get(position, false));
        holder.binding.llContactRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (isGroupCreate){

                    }else{
                        listener.onContactSelected(contactListFiltered.get(position));
                    }

                }
            }
        });
    }
    public void setGroupFlaf(boolean flag){
        isGroupCreate=flag;
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
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final ContactlistRowLayoutBinding binding;

        public MyViewHolder(final ContactlistRowLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            itemBinding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactsArrayList;
                } else {
                    List<Contacts> filteredList = new ArrayList<>();
                    for (Contacts row : contactsArrayList) {

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
        void onContactSelected(Contacts contact);

        void onRowLongClicked(int position);
    }
}
