package ru.mininn.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mdevelop on 26.07.2017.
 */

public class ConnectThread extends Thread {
    private BluetoothSocket socket;
    private BluetoothDevice device;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    public ConnectThread(BluetoothDevice device, Context context) {
        this.device = device;
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    public void run() {
        try {
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
