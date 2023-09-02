/*
import static java.sql.DriverManager.println;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private UsbManager usbManager;  // USB Manager object to manage USB connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the USB manager object
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Register a broadcast receiver for USB accessory events
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);   // Register the broadcast receiver to receive USB events
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {   // Broadcast receiver for USB events
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();   // Get the action of the intent that was received
            if (action == UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {// If an accessory was attached, get it and open a connection to it
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);// Get the accessory from the intent
                if (accessory != null) {      // If an accessory was found, open a connection to it
                    usbManager.openAccessory(accessory);      // Open a connection to the accessory
                    InputStream inputStream = new FileInputStream(accessory.getFileDescriptor());
                    // Create an input stream from the file descriptor of the accessory
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    // Create a buffered reader from the input stream
                    String line;
                    // Read data from the buffered reader until there is no more data available
                    while ((line = reader.readLine()) != null) {
                        System out println("Data Received: " + line);
                    }
                }
            } else if (action == UsbManager.ACTION_USB_ACCESSORY_DETACHED) {
                System out println("Accessory detached");
            }
        }
    };*/
