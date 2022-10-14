package com.example.reminderme;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;


public class ContactDataFragment extends Fragment implements View.OnClickListener{
    private Contact contact;
    private Button saveBtn;
    private TextView name;
    private TextView tel;
    private FloatingActionButton addButton;
    private FragmentManager manager;
    private MainFragment mainFragment;
    private Context context;
    private Button btnDatePicker, btnTimePicker;
    private EditText txtDate, txtTime;
    private int Year, Month, Day, Hour, Minute;

    @SuppressLint("ValidFragment")
    public ContactDataFragment(Contact contact, FragmentManager manager) {
        this.contact = contact;
        this.manager = manager;
    }




    @Override
    public void onAttach(@NonNull Context context) {
        try{
        this.context = context;
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
        return inflater.inflate(R.layout.contact_data_layout, container,false);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        name = view.findViewById(R.id.name);
        tel= view.findViewById(R.id.Tel);

       String Name=contact.getName();
       List<String> Tel=contact.getPhoneNumbers();


       StringBuilder TelToShow = new StringBuilder();
       name.setText(Name);
       int i=0;
       while(i<Tel.size()) {
           if (i+1==Tel.size())
               TelToShow.append(Tel.get(i));
           else
               TelToShow.append(Tel.get(i)+" ,");
           i++;
       }
        tel.setText(TelToShow.toString());


       saveBtn = (Button) view.findViewById(R.id.saveBtn);
       saveBtn.setOnClickListener(new HandleSaveBtnClick());



        /**
         * date and time buttons
         */
        btnDatePicker=(Button)view.findViewById(R.id.btn_date);
        btnTimePicker=(Button)view.findViewById(R.id.btn_time);
        txtDate=(EditText)view.findViewById(R.id.in_date);
        txtTime=(EditText)view.findViewById(R.id.in_time);

        if(contact.getDay()!= -1)
        {
            txtDate.setText(contact.getDay().toString() + "-" + (contact.getMonth() + 1) + "-" + contact.getYear());
            txtTime.setText(contact.getHour() + ":" + contact.getMinute());
            Day=contact.getDay();
            Month=contact.getMonth();
            Year=contact.getYear();
            Hour=contact.getHour();
            Minute=contact.getMinute();
        }

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

    }

    private class HandleSaveBtnClick implements View.OnClickListener {
        public void onClick(View arg0) {

            mainFragment = (MainFragment)  manager.findFragmentByTag("mainFragmentTag");



            mainFragment.addContactToSavedList(contact);
            HashSet<String> SavedContactsSet=mainFragment.getSet();
            if(SavedContactsSet!=null) {
                SavedContactsSet.add(contact.getName());

                //viewModel.saveDeletedCountries();
            }

            contact.setYear( Year);
            contact.setMonth(Month);
            contact.setDay(Day);
            contact.setHour(Hour);
            contact.setMinute(Minute);

            mainFragment.editSharedPrefrence();
            mainFragment.saveContactTime(contact.getName(), Year, Month, Day, Hour, Minute);

            /**listener to to start the service from main activity **/
            ((SaveDataListener) context).OnSave(contact);

        }
    }

    /** implemented by main activity **/
    interface SaveDataListener{
        void OnSave(Contact contact);
    }

    /** date and time click function **/
    @Override
    public void onClick(View v) {
        int mYear, mMonth, mDay, mHour, mMinute;
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            Month=monthOfYear+1;
                            Day=dayOfMonth;
                            Year=year;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                            Hour=hourOfDay;
                            Minute=minute;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }

    }

}

