package com.example.reminderme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainFragment extends Fragment
{
    private ContactListFragment contactListFragment;
    private ContactAdapter contactAdapter;
    private ContactAdapter savedContactAdapter;
    private FragmentManager manager;
    private List<Contact> contactList;
    private List<Contact> savedContactList;
    private RecyclerView savedRecyclerView;
    private Context context;
    private HashSet<String> SavedContactsSet;
    private SharedPreferences sp;
    private List<String> savedTimeList;


    public MainFragment(Context context, List<Contact> contactList, List<Contact> savedContactList,
                        FragmentManager manager, ContactAdapter contactAdapter, ContactAdapter savedContactAdapter)
    {
        this.contactList = contactList;
        this.manager = manager;
        this.context = context;
        this.contactAdapter = contactAdapter;
        this.savedContactAdapter = savedContactAdapter;
        this.savedContactList = savedContactList;
    }


    /** called from ContactData Fragment **/
    public void addContactToSavedList(Contact contact)
    {
        Contact contactToCheck = checkContactExist(contact);
        if (contactToCheck == null)
            this.savedContactList.add(contact);
        else
            savedContactList.set(savedContactList.indexOf(contactToCheck), contact);
        savedContactAdapter.notifyDataSetChanged();

        removeFragments();
    }


    public Contact checkContactExist(Contact contact)
    {
        for(Contact savedContact : savedContactList)
        {
            String contactName = savedContact.getName();
            if(contactName.equals(contact.getName()))
                return savedContact;
        }

        return null;
    }


    public void removeFragments()
    {
        ContactDataFragment contactDataFragment = (ContactDataFragment) manager.findFragmentByTag("contactDataTag");
        if (contactDataFragment != null)
            manager.beginTransaction().remove(contactDataFragment).commit();
        ContactListFragment contactListFragment = (ContactListFragment) manager.findFragmentByTag("contactListTag");
        if (contactListFragment != null)
            manager.beginTransaction().remove(contactListFragment).commit();
    }

    public List<Contact> getSavedContactList() {
        return savedContactList;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        try
        {

        } catch (ClassCastException e)
        {
            throw new ClassCastException("the class " +
                    getActivity().getClass().getName() +
                    " must implements the interface 'ClickHandler'");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.open_app, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        view.findViewById(R.id.addBtn).setOnClickListener(handleAddContactClick);
        savedRecyclerView = view.findViewById(R.id.savedContact);
        setDataToAdapter();
        savedTimeList = new ArrayList<String>();

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        //**********shared preference block **********
        Boolean val = sp.getBoolean("check_box_preference_1", false);//getting value(boolean) from Shared Preferences xml
        if (val)
        {

            /** loading contact with shared preferance **/
            SavedContactsSet = (HashSet) sp.getStringSet("Contacts", null);
            if (SavedContactsSet == null)
                SavedContactsSet = new HashSet();
            else
            {
                for (String name : SavedContactsSet)
                {

                    for (int i = 0; i < contactList.size(); i++)
                    {
                        Contact contact = contactList.get(i);
                        if (contact.getName().equals(name))
                            savedContactList.add(contact);
                    }
                }
            }

            /**loading date and time into text file**/

            String savedTimeFile = readFromFile(context);

            if (!savedTimeFile.equals(""))
            {
                String newString = savedTimeFile.substring(2, savedTimeFile.length() - 1);
                String[] StringArray = newString.split(", ");

                for (String timeObject : StringArray) {
                    savedTimeList.add(timeObject);
                    String[] time = timeObject.split(";");

                    String name = time[0];

                    Contact contact = getContacFromListByName(name);
                    if (contact != null) {


                        contact.setYear(Integer.parseInt(time[1]));
                        contact.setMonth(Integer.parseInt(time[2]));
                        contact.setDay(Integer.parseInt(time[3]));

                        contact.setHour(Integer.parseInt(time[4]));
                        contact.setMinute(Integer.parseInt(time[5]));
                    }

                }
                for (Contact contact : savedContactList)
                    System.out.println(contact.getName() + " " + contact.getYear());

            }
        }
        else/** if unchecked box than delete all contact data shared prefrense + text file **/
        {

            SharedPreferences.Editor preferencesEditor =  sp.edit();
            preferencesEditor.clear().commit();
            if (SavedContactsSet == null)
                SavedContactsSet = new HashSet();
            writeToFile("",context);

        }
    }

    private Contact getContacFromListByName(String name)
    {
        for (int i = 0; i < savedContactList.size(); i++)
        {
            Contact contact = savedContactList.get(i);
            if (contact.getName().equals(name))
                return contact;
        }
        return null;
    }


    /**pressing add new contact button switches to Contact List Fragment **/

    private View.OnClickListener handleAddContactClick = new View.OnClickListener()
    {
        public void onClick(View arg0)
        {
            //FloatingActionButton addButton = (FloatingActionButton)arg0;

            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            {
                contactListFragment = new ContactListFragment(context,manager, contactList,contactAdapter);

                manager.beginTransaction().
                        add(R.id.mainLayout,  contactListFragment,"contactListTag").
                        addToBackStack("BBB").
                        commit();
                manager.executePendingTransactions();

            }
        }
    };

    /** called from Contact Data Fragment**/
    public void editSharedPrefrence()
    {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("Contacts").apply();
        edit.putStringSet("Contacts", SavedContactsSet).apply();
        savedContactAdapter.notifyDataSetChanged();

    }
    public HashSet<String> getSet()
    {

        return SavedContactsSet;
    }
    private void setDataToAdapter()
    {
        initRecyclerView();
    }

    private void initRecyclerView()
    {
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        savedRecyclerView.setHasFixedSize(true);
        savedRecyclerView.setAdapter(savedContactAdapter);
    }

    /** saving date and time for picked contact functions into text file called fropm Contact Data Fragment**/
    public void saveContactTime(String name,Integer mYear,Integer mMonth,Integer mDay,Integer mHour,Integer mMinute)
    {


        String savedTime = name+";"+mYear.toString()+";"+mMonth.toString()+";"+mDay.toString()+";"+mHour.toString()+";"+mMinute.toString();
        System.out.println(savedTime);

        boolean checkExistingContact = true;
        int i=0;
        for(String contactString: savedTimeList)
        {
                String[] time = contactString.split(";");

                String nameCheck = time[0];
                if(nameCheck.equals(name)) {
                    savedTimeList.set(i, savedTime);
                    checkExistingContact=false;
                    break;
                }

                i++;
            }
        if(checkExistingContact)
            savedTimeList.add(savedTime);
        writeToFile(savedTimeList.toString(),context);

        /**printing check **/
        String checkRead =readFromFile(context);
        System.out.println(checkRead);
    }


    private void writeToFile(String data, Context context)
    {
        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("savedTime.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try
        {
            InputStream inputStream = context.openFileInput("savedTime.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
