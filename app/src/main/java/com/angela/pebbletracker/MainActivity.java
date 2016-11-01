package com.angela.pebbletracker;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // // Send messages from Android

    // Create a new dictionary
    PebbleDictionary dict = new PebbleDictionary();

    // The key representing a contact name is being transmitted
    final int AppKeyContactName = 0;
    final int AppKeyAge = 1;

    // Get data from the app
    final String contactName = "Contact Name";
    final int age = 20;

    // Add data to the dictionary
    dict.addString(AppKeyContactName, contactName);
    dict.addInt32(AppKeyAge, age);

    final UUID appUuid = UUID.fromString("EC7EE5C6-8DDF-4089-AA84-C3396A11CC95");

    // Send the dictionary
    PebbleKit.sendDataToPebble(getApplicationContext(), appUuid, dict);

    // // Receive messages from Android

//    // Create a new receiver to get AppMessages from the C app
//    PebbleKit.PebbleDataReceiver dataReceiver = new PebbleKit.PebbleDataReceiver(appUuid) {
//        @Override
//        public void receiveData(Context context, int transaction_id,
//                                PebbleDictionary dict) {
//            // A new AppMessage was received, tell Pebble
//            PebbleKit.sendAckToPebble(context, transaction_id);
//        }
//
//    };
}
