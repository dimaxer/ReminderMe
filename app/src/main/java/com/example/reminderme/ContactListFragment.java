package com.example.reminderme;


import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactListFragment extends Fragment {
    private FragmentManager manager;
    private RecyclerView recyclerView;
    private Context context;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    public ContactListFragment(Context context,FragmentManager manager, List<Contact> contactList, ContactAdapter contactAdapter)
    {
        this.context = context;
     this.manager= manager;
     this.contactList = contactList;
     this.contactAdapter=contactAdapter;
    }


    private void setDataToAdapter(){

        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactAdapter);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        try{

        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    getActivity().getClass().getName() +
                    " must implements the interface 'ClickHandler'");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.contact_list_layout, container,false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.rv);
        setDataToAdapter();

    }


}
