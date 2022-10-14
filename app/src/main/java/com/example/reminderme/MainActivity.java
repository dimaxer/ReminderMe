package com.example.reminderme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.ContactClickListener, ContactDataFragment.SaveDataListener {
    // list variable
    private List<Contact> newContactList = new ArrayList<>();
    private List<Contact> savedContactList = new ArrayList<>();

    //RecyclerView variables
    private RecyclerView recyclerView;
    private RecyclerView savedRecyclerView;
    //adapters
    ContactAdapter newContactAdapter;
    ContactAdapter savedContactAdapter;

    //class variable
    private static String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONTACT = 1;
    private FragmentManager manager;
    private ContactDataFragment contactDataFragment;
    private MainFragment mainfragmanet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();

        newContactAdapter = new ContactAdapter(this, newContactList);
        savedContactAdapter = new ContactAdapter(this,savedContactList);

        /** start Main Fragment**/
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            mainfragmanet = new MainFragment(this, newContactList,savedContactList, manager, newContactAdapter,savedContactAdapter);

            manager.beginTransaction().
                    add(R.id.mainLayout,  mainfragmanet,"mainFragmentTag").
                    commit();
            manager.executePendingTransactions();
        }

        /**ask for contact permisions + load contacts to contactList **/
        requestContactsPermissions();

        /** register Broadcast reciever for airplane mode **/
        AirplaneModeChangedReceiver receiver = new AirplaneModeChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(receiver,intentFilter);


        /** to check if app was activated by notification click **/
        if (getIntent().getExtras()!=null && getIntent().getExtras().getInt("notify")==1)
        {
            Intent stop = new Intent(this, com.example.reminderme.MyService.class);

            stopService(stop);

            Intent intent =getIntent();
            String userPhoneNumber = intent.getExtras().getString("Number");


            /** show dialog with contact information **/
            new AlertDialog.Builder(this)
                    .setTitle("Call "+ intent.getExtras().getString("Name"))
                    .setMessage("Are you sure you want to call :" + userPhoneNumber)
                    .setPositiveButton("Call", new DialogInterface.OnClickListener()
                    {

                        public void onClick(DialogInterface dialog, int which)
                        {
                            callUser(userPhoneNumber);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing.
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /** function to allow call to cntact **/
    public void callUser(String phoneNum) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNum));
        startActivity(callIntent);
    }



    private void ServiceCaller(Contact contact){

        Intent intent = new Intent(this, com.example.reminderme.MyService.class);

        stopService(intent);

        intent.putExtra("Hour", contact.getHour());
        intent.putExtra("Minute", contact.getMinute());

        intent.putExtra("Year", contact.getYear());
        intent.putExtra("Month", contact.getMonth());
        intent.putExtra("Day", contact.getDay());
        intent.putExtra("Name",contact.getName());
        intent.putExtra("Number",contact.getPhoneNumbers().get(0));


        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu ) {
        getMenuInflater().inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.settings:
            {

                MySettingFragment frag =(MySettingFragment) manager.findFragmentByTag("settingTag");

                if(frag==null) {
                            manager.
                            beginTransaction().
                            add(R.id.mainLayout,new MySettingFragment(),"settingsTag").
                            addToBackStack(null).
                            commit();
                    manager.executePendingTransactions();

                }

            }
            case R.id.back:
            {
                ContactDataFragment contactDataFragment = (ContactDataFragment) manager.findFragmentByTag("contactDataTag");
                if (contactDataFragment != null)
                    manager.beginTransaction().remove(contactDataFragment).commit();
                ContactListFragment contactListFragment = (ContactListFragment) manager.findFragmentByTag("contactListTag");
                if (contactListFragment != null)
                    manager.beginTransaction().remove(contactListFragment).commit();

            }


        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getContactInfo(){
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String CONTACT_ID = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
                Contact contact = new Contact();
                if (hasPhoneNumber > 0){
                    contact.setName(name);

                    Cursor phoneCursor = contentResolver.query(PHONE_URI, new String[]{NUMBER},PHONE_ID+" = ?",new String[]{CONTACT_ID},null);
                    List<String> contactPhoneNumberList = new ArrayList<>();
                    phoneCursor.moveToFirst();
                    while (!phoneCursor.isAfterLast()){
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ","");
                        contactPhoneNumberList.add(phoneNumber);
                        phoneCursor.moveToNext();
                    }
                    contact.setPhoneNumbers(contactPhoneNumberList);
                    this.newContactList.add(contact);
                    phoneCursor.close();
                }
            }
            newContactAdapter.notifyDataSetChanged();
        }
    }

    public void requestContactsPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)){

                Snackbar.make(recyclerView, "permission Contact", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
            }
        } else {
            getContactInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_GRANTED) {
                getContactInfo();
            }
        }
    }

    /** called from both contactAdapter and savedContactadapter **/
    @Override
    public void OnClick(int position, ContactAdapter adapter) {
        System.out.println("position:  " + position + "  name: " + newContactList.get(position).getName() + "  tel: " + newContactList.get(position).getPhoneNumbers());


        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            List<Contact> contactListToDisplay = null;
            if(adapter == newContactAdapter)
                contactListToDisplay= newContactList;
            else if(adapter == savedContactAdapter)
                contactListToDisplay=savedContactList;
            contactDataFragment = new ContactDataFragment(contactListToDisplay.get(position),manager);
            
            manager.beginTransaction().
                    add(R.id.mainLayout,  contactDataFragment,"contactDataTag").
                    addToBackStack("BBB").
                    commit();
            manager.executePendingTransactions();

        }
    }

    /** called from ContactDataFragment **/
    @Override
    public void OnSave(Contact contact) {
       ServiceCaller(contact);

    }
}
