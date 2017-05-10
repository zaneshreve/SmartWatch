package com.example.zane.smartwatch;

import android.content.Intent;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.CheckBox;
import android.bluetooth.BluetoothSocket;
import java.io.OutputStream;
import java.io.IOException;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SendScreen extends AppCompatActivity {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;


    //Dedicate this strings as app identifiers
    //This same list appears in the Arduino code
    final String MESSAGING = "com.google.android.apps.messaging";
    final String EMAIL = "com.google.android.gm";
    final String CALL = "com.android.dialer";
    final String MISSED_CALL = "com.android.server.telecom";
    final String VOICEMAIL = "com.motorola.appdirectedsmsproxy";

    //Dynamic arrayList to keep track of the apps which are allowed to send notifications
    static ArrayList<String> activeApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_screen);

        //Send the time when the watch is first started up


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


        //Configure a repeating alarm (every minute) in conjunction with AlarmReceiver class
        Intent alarmIntent = new Intent(SendScreen.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(SendScreen.this, 0, alarmIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 60000; //60000 milliseconds is one minute
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pendingIntent);
    }

    //Adds or removes the app to or from the list of those that send notifications
    public void appState(View view) {

        CheckBox cb = (CheckBox)view;
        boolean isChecked = cb.isChecked();

        //Looks through each checkbox and checks its state
        //If it is checked, add that app's info to the array of apps to send notifications from
        switch(cb.getId()) {
            case R.id.messaging:
                if (isChecked) {
                    activeApps.add(MESSAGING);
                }
                else {
                    activeApps.remove(MESSAGING);
                }
                break;
            case R.id.email:
                if (isChecked) {
                    activeApps.add(EMAIL);
                }
                else {
                    activeApps.remove(EMAIL);
                }
                break;
            case R.id.call:
                if (isChecked) {
                    activeApps.add(CALL);
                }
                else {
                    activeApps.remove(CALL);
                }
                break;
            case R.id.missed_call:
                if (isChecked) {
                    activeApps.add(MISSED_CALL);
                }
                else {
                    activeApps.remove(MISSED_CALL);
                }
                break;
            case R.id.voicemail:
                if (isChecked) {
                    activeApps.add(VOICEMAIL);
                }
                else {
                    activeApps.remove(VOICEMAIL);
                }
                break;
        }
    }

    //Thread to send a bluetooth message
    //Using a thread allows the message to be sent without interrupting regular
    //execution of the application
    protected class SendThread extends Thread {
        //Get the bluetooth socket
        BluetoothSocket socket = MainActivity.btSocket;

        //Create an output stream object for sending messages over bluetooth
        OutputStream os;

        String message;

        public SendThread(String message) {

            //Accept the argument as what we want to send
            this.message = message;

            //Establish an output stream to send via bluetooth
            try {
                os = socket.getOutputStream();
            }
            catch (IOException e) {
                System.err.println("Can't get output stream");
            }
        }

        //The run() method is what is called to "activate" the thread
        public void run() {
            try {
                //Send the message over bluetooth
                os.write(message.getBytes());
            }
            catch (IOException e) {
                System.err.println("Can't write message");
            }
        }
    }
}
