package ru.mininn.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mdevelop on 26.07.2017.
 */

public class AcceptThread extends Thread {
    private final BluetoothServerSocket serverSocket;
    private static final String TAG = "AcceptThread";

    public AcceptThread() {
        BluetoothServerSocket tmp = null;
        try {
            tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("Server", UUID.randomUUID());
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        serverSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
