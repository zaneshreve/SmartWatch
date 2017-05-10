package com.example.zane.smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.Locale;


//Class to handle the every-minute sending of time information
public class AlarmReceiver extends BroadcastReceiver {

    //When an alarm is received, this method is called
    //The alarm is established in the SendScreen class to be received every minute
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the system time in hours and minutes from the phone
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR);
        int m = c.get(Calendar.MINUTE);

        //Sends the time using the bluetooth SendThread

        //Checks if the minute needs a leading zero or if it is two digits long
        if (Integer.toString(m).length() > 1) {
            new SendScreen().new SendThread(String.format(Locale.getDefault(), "%d:%d#", h, m)).start();
        }
        //If the minute is not greater than one digit, add a leading zero so the format is H:MM
        else {
            new SendScreen().new SendThread(String.format(Locale.getDefault(), "%d:0%d#", h, m)).start();
        }
    }
}