package com.example.reminderme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class AirplaneModeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean state = intent.getBooleanExtra("state", false);
        if (state)
            Toast.makeText(context, "airplane mode is on , you can't make calls",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "airplane mode is off , you can make calls",Toast.LENGTH_SHORT).show();
    }
}

