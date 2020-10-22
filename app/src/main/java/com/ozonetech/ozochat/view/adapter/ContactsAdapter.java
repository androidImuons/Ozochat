package com.ozonetech.ozochat.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ContactlistRowLayoutBinding;
import com.ozonetech.ozochat.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private LayoutInflater layoutInflater;
    public List<Contacts> contacts;
    Contacts contactslist;
    private ArrayList<Contacts> contactsArrayList;
    boolean checked = false;
    View view;

    public ContactsAdapter(LayoutInflater inflater, List<Contacts> items) {
        this.layoutInflater = inflater;
        this.contacts = items;
        this.contactsArrayList = new ArrayList<Contacts>();
        this.contactsArrayList.addAll(contacts);
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

        holder.binding.setContacts(contactsArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ContactlistRowLayoutBinding binding;
        public MyViewHolder(final ContactlistRowLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding= itemBinding;
        }
    }
}
