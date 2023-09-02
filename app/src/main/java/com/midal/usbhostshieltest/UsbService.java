/*
package com.midal.usbhostshieltest;

import static java.lang.Thread.sleep;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class UsbService extends Service {

    //Member attributes
    // USB
    private static final String ACTION_USB_PERMISSION = "com.examples.accessory.controller.action.USB_PERMISSION";

    private Context mMainContext;
    LocalBroadcastManager mBroadcastManager = null;

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private UsbAccessory mAccessory;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    private boolean mPermissionRequestPendding = false;

    private Thread mAccessMonitor;
    private Thread mUsbListenner;

    private ConcurrentLinkedQueue<String> usbDataBuffer;

    */
/* ************************************* *//*

    */
/*         BROADCAST RECEIVERS           *//*

    */
/* ************************************* *//*


    private final BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            usbPermissionMonitor(intent);
        }
    };

    */
/* ************************************* *//*

    */
/*              RUNNABLES                *//*

    */
/* ************************************* *//*


    // Running thread listenning to USB port for received messages
    private Runnable UsbListenner = new Runnable() {
        public void run() {
            while(1)
                readUsbAccessory();
        }
    };

    // Running thread listenning to USB accessories
    private Runnable AccessoryMonitor = new Runnable() {
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            while(mIsServiceRunning){
                try {
                    getAccessory();
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    */
/* ************************************* *//*

    */
/*           SERVICE FUNCTIONS           *//*

    */
/* ************************************* *//*

    public UsbService(){}

    @Override
    public void onCreate() {
        mMainContext = getApplicationContext();
        mBroadcastManager = LocalBroadcastManager.getInstance(mMainContext);
        mBroadcastManager.registerReceiver(usbServiceReceiver, usbServiceFilter);
        initUsb();
    }

    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        return true;
    }

    // Destroy
    @Override
    public void onDestroy() {

        closeAccessory();
        mBroadcastManager.unregisterReceiver(mUsbPermissionReceiver);
        mBroadcastManager.unregisterReceiver(usbServiceReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return START_NOT_STICKY;
    }

    */
/* ************************************* *//*

    */
/*         USB CONTROL FUNCTIONS         *//*

    */
/* ************************************* *//*


    private void initUsb()
    {
        usbDataBuffer = new ConcurrentLinkedQueue<>();
        mAccessMonitor= new Thread(AccessoryMonitor);
        mUsbListenner = new Thread(UsbListenner);

        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction(UsbManager.EXTRA_ACCESSORY);
        mBroadcastManager.registerReceiver(mUsbPermissionReceiver, filter);
        mAccessMonitor.start();
    }

    private void getAccessory()
    {
        if (mFileDescriptor != null) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null)
        {
            if (mUsbManager.hasPermission(accessory))
            {
                //"has permission";
                if(!mPermissionRequestPendding) {
                    openAccessory(accessory);
                }
            }
            else
            {
                //"no permission";
                synchronized (mUsbPermissionReceiver)
                {
                    if(!mPermissionRequestPendding) {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPendding = true;
                    }
                }
            }
        }
    }

    private void openAccessory(UsbAccessory accessory)
    {
        if(accessory != null) {
            mFileDescriptor = mUsbManager.openAccessory(accessory);
            if (mFileDescriptor != null) {
                mAccessory = accessory;
                FileDescriptor fd = mFileDescriptor.getFileDescriptor();
                mInputStream = new FileInputStream(fd);
                mOutputStream = new FileOutputStream(fd);
                mUsbListenner.start();
                mPermissionRequestPendding = false;
            }
        }
    }

    private void closeAccessory() {
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        }
        catch (IOException e) {

        }
        finally {
            mAccessory = null;
            mFileDescriptor = null;
        }
    }

    public void sendDataUsb(String msg)
    {
        if (mFileDescriptor != null) {
            byte[] bytes = msg.getBytes();
            try {
                mOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    */
/*
     * Runnable block that will poll the accessory data stream
     * for regular updates, adds each message to a buffer.
     *//*

    private void readUsbAccessory() {
        if (mFileDescriptor != null) {
            int ret = 0;
            byte[] buffer = new byte[16384];

            while (mFileDescriptor != null) {
                try {
                    ret = mInputStream.read(buffer);
                    if (ret > 0) {
                        usbDataBuffer.add(new String(buffer, 0, ret));
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    */
/*
     * This receiver monitors for the event of a user granting permission to use
     * the attached accessory.  If the user has checked to always allow, this will
     * be generated following attachment without further user interaction.
     *//*

    private void usbPermissionMonitor(Intent intent)
    {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                final UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    openAccessory(accessory);
                    mPermissionRequestPendding = false;
                } else {
                    //permission refused
                    mUsbManager.requestPermission(accessory, mPermissionIntent);
                }
            }
        } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
            UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            if (accessory != null && accessory.equals(mAccessory)) {
                Toast.makeText(this, "Cable detach !", Toast.LENGTH_LONG).show();
                closeAccessory();
                mUsbActive = false;
            }
        }
    }
}
*/
