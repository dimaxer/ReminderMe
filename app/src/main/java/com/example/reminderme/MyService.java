package com.example.reminderme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private Integer Hour;
    private Integer Minute;
    private Integer Year, Month ,Day ;
    private Ringtone ringtone;
    private String Name;
    private String Number;
    private Timer t = new Timer();

    private static final String CHANNEL_ID = "MyNotificationChannelID";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Hour = intent.getIntExtra("Hour", 0);
        Minute = intent.getIntExtra("Minute", 0);
        Year = intent.getIntExtra("Year", 0);
        Month = intent.getIntExtra("Month", 0);
        Day = intent.getIntExtra("Day", 0);
        Name= intent.getStringExtra("Name");
        Number= intent.getStringExtra("Number");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        try {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra("notify",1);
            notificationIntent.putExtra("Hour", Hour);
            notificationIntent.putExtra("Minute",Minute);

            notificationIntent.putExtra("Year", Year);
            notificationIntent.putExtra("Month", Month);
            notificationIntent.putExtra("Day", Day);
            notificationIntent.putExtra("Name", Name);
            notificationIntent.putExtra("Number", Number);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID )
                    .setContentTitle("Call "+Name)
                    .setContentText("Your chosen time is now:  " + Hour.toString() + ":" + Minute.toString())

.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Reminder Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        catch (Exception e){
            e.printStackTrace();
        }


        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                LocalDateTime now = LocalDateTime.now();
                if (c.getTime().getHours() == Hour && c.getTime().getMinutes() == Minute&& now.getYear()== Year && now.getMonthValue() == Month && now.getDayOfMonth() == Day)
                {

                    ringtone.play();
                }
                else {
                    ringtone.stop();
                }

            }
        }, 0, 2000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
        t.cancel();
        super.onDestroy();
    }
}