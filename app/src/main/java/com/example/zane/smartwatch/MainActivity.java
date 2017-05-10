package com.example.zane.smartwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;
import android.content.Intent;
import java.util.Locale;
import java.util.Set;
import java.io.*;
import java.util.UUID;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Unique identifier for the HC-06
    final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Keep track of the BT device's name and MAC address
    String name;
    String address;

    BluetoothAdapter bt;

    static BluetoothSocket btSocket;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start BT
        bt = BluetoothAdapter.getDefaultAdapter();

        final int REQUEST_ENABLE_BT = 6;

        if (bt != null) {
            //If BT is not on, request the user to turn it on
            if (!bt.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, REQUEST_ENABLE_BT);
            }

            //Set up the BT device so we can connect to it
            final Set<BluetoothDevice> deviceSet = bt.getBondedDevices();
            final BluetoothDevice actualDevice;

            //Identify the most recently paired BT device by its name and MAC address
            if (deviceSet.size() > 0) {
                for (BluetoothDevice device : deviceSet) {
                    name = device.getName();
                    address = device.getAddress();
                }
            }

            else {
                address = "no device";
                address = "no address";
            }

            //Set up device
            actualDevice = bt.getRemoteDevice(address);

            //Display the device's name & adress
            tv = (TextView)findViewById(R.id.showDevice);
            tv.setText(String.format(Locale.getDefault(), "Paired with %s at %s", name, address));

            //Attach the "connect" button to instigate the BluetoothThread
            Button b = (Button)findViewById(R.id.connectButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BluetoothThread(actualDevice).start();
                }
            });
        }
    }



    //New activity for BT writing
    public void startBluetoothScreen() {
        Intent intent = new Intent(this, SendScreen.class);
        startActivity(intent);
    }


    //Do the connection stuff in this thread
    private class BluetoothThread extends Thread {

        private BluetoothThread(BluetoothDevice device) {
            //Set up a BT socket
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                System.err.println("Can't create socket");
            }
        }

        @Override
        public void run() {
            try {
                //Toast
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
                    }
                });
                //Attempt to connect..
                btSocket.connect();
            }
            catch (IOException e) {
                try {
                    btSocket.close();
                }
                catch (IOException f) {
                    System.err.println("Can't close socket");
                }
                return;
            }

            //Arrive here if we've connected, so advance to the next screen (SendScreen)
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_LONG).show();
                }
            });

            startBluetoothScreen();
        }
    }
}


