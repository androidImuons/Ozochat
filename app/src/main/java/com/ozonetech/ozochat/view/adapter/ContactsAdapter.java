package com.ozonetech.ozochat.view.adapter;

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
import com.ozonetech.ozochat.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    private LayoutInflater layoutInflater;
    public List<Contacts> contacts;
    private ArrayList<Contacts> contactsArrayList;
    private List<Contacts> contactListFiltered;
    private ContactsAdapterListener listener;


    public ContactsAdapter(LayoutInflater inflater, List<Contacts> items,ContactsAdapterListener listener) {
        this.layoutInflater = inflater;
        this.contacts = items;
        this.contactsArrayList = new ArrayList<Contacts>();
        this.contactListFiltered=new ArrayList<Contacts>();
        this.contactsArrayList.addAll(contacts);
        this.contactListFiltered.addAll(contacts);
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ContactlistRowLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.contactlist_row_layout,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.setContacts(contactListFiltered.get(position));
        holder.binding.llContactRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onContactSelected(contactListFiltered.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ContactlistRowLayoutBinding binding;
        public MyViewHolder(final ContactlistRowLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding= itemBinding;
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
    }
}
