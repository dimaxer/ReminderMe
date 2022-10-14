package com.example.reminderme;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{

    private List<Contact> contactList;
    private ContactClickListener contactClickListener;
    private ContactAdapter adapter;


    /** main activity implements this interface**/
    public interface ContactClickListener
    {
        void OnClick(int position, ContactAdapter adapter);
    }


    public ContactAdapter(Context context, List<Contact> contactList) {
        this.contactList = contactList;
        contactClickListener = (ContactClickListener) context;
        adapter= this;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyViewHolder(view, contactClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact model = contactList.get(position);
        if (model != null){
            if (model.getName() != null){
                holder.name.setText(model.getName());
            }


        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, number;
        private ContactClickListener contactClickListener;
        public MyViewHolder(View itemView, ContactClickListener contactClickListener) {
            super(itemView);
            this.contactClickListener = contactClickListener;
            name = itemView.findViewById(R.id.tvName);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            contactClickListener.OnClick(position, adapter);
        }
    }
}
