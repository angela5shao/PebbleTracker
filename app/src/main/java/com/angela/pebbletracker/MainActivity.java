package com.angela.pebbletracker;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // UUID of the C app that receives data
    private static final UUID appUuid = UUID.fromString("8F3C8686-31A1-4F5F-915F-01600C9BDC59");//EC7EE5C6-8DDF-4089-AA84-C3396A11CC95");

    // The key representing a contact name is being transmitted
    final int AppKeyContactName = 0;
    final int AppKeyAge = 1;

    // Get data from the app
    final String contactName = "Contact Name";
    final int age = 20;

    private static final int
            KEY_BUTTON = 0,
            KEY_VIBRATE = 1,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;

    private TextView whichButtonView;
    private Handler handler = new Handler();

    // Create a new receiver to get AppMessages from the C app
    private PebbleKit.PebbleDataReceiver dataReceiver;// = new PebbleKit.PebbleDataReceiver(appUuid) {
//        @Override
//        public void receiveData(Context context, int transaction_id,
//                                PebbleDictionary dict) {
//            Log.d("receiveData1", "called");
//            // If the tuple is present, read the integer value
//            Long ageValue = dict.getInteger(AppKeyAge);
//            if(ageValue != null) {
//                int age = ageValue.intValue();
//            }
//
//            // A new AppMessage was received, tell Pebble
//            PebbleKit.sendAckToPebble(context, transaction_id);
//        }
//
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test: see if Pebble watch is connected
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        if (connected) {
            Log.d("onCreate", "watchIsConnected");
        } else {
            Log.d("onCreate", "watchIsNotConnected");
        }

        // // Send messages from Android

        // Create a new dictionary
        PebbleDictionary dict = new PebbleDictionary();

        // Add data to the dictionary
        dict.addString(AppKeyContactName, contactName);
        dict.addInt32(AppKeyAge, age);

        // Send the dictionary
        PebbleKit.sendDataToPebble(getApplicationContext(), appUuid, dict);

        dataReceiver = null;

        // Customize ActionBar
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle("PebbleKit Example");
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_orange)));

        // Add vibrate Button behavior
        Button vibrateButton = (Button)findViewById(R.id.button_vibrate);
        vibrateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Send KEY_VIBRATE to Pebble
                PebbleDictionary out = new PebbleDictionary();
                out.addInt32(KEY_VIBRATE, 0);
                PebbleKit.sendDataToPebble(getApplicationContext(), appUuid, out);
            }

        });

        // Add output TextView behavior
        whichButtonView = (TextView)findViewById(R.id.which_button);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Define AppMessage behavior
        if(dataReceiver == null) {
            Log.d("onResume", "appMsgReceiver is now != null");
            dataReceiver = new PebbleKit.PebbleDataReceiver(appUuid) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receiveData2", "Got message from Pebble!");

                    // What message was received?
                    if (data.getInteger(KEY_BUTTON) != null) {
                        // KEY_BUTTON was received, determine which button
                        final int button = data.getInteger(KEY_BUTTON).intValue();

                        // Update UI on correct thread
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                switch (button) {
                                    case BUTTON_UP:
                                        whichButtonView.setText("UP");
                                        break;
                                    case BUTTON_SELECT:
                                        whichButtonView.setText("SELECT");
                                        break;
                                    case BUTTON_DOWN:
                                        whichButtonView.setText("DOWN");
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), "Unknown button: " + button, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        });
                    }
                }
            };

//            // Register the receiver
//            PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataReceiver);
        }
        // Register the receiver
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister data receiver
        if(dataReceiver != null) {
            unregisterReceiver(dataReceiver);
            dataReceiver = null;
        }
    }

}
