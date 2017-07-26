package ru.mininn.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DEVICE =1;
    private Button clientButton;
    private Button serverButton;
    private AcceptThread acceptThread;
    ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(recrver,filter);
        initView();
    }

    private void initView() {
        clientButton = (Button) findViewById(R.id.search_button);
        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,SearchActivity.class),REQUEST_DEVICE);
            }
        });
        serverButton = (Button) findViewById(R.id.server_button);
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acceptThread!=null){
                    acceptThread.cancel();
                    acceptThread = null;
                }
                acceptThread = new AcceptThread();
                acceptThread.run();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_DEVICE:{
                connectDevice(data);

            }
        }
    }

    public void connectDevice(Intent intent){
        String deviceName = intent.getStringExtra("device");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (deviceName != null) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceName);
            if(connectThread!=null){
                connectThread.cancel();
                connectThread = null;
            }
            connectThread = new ConnectThread(device,getApplicationContext());
            connectThread.run();
        }
    }

    BroadcastReceiver recrver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_ACL_CONNECTED: {
                    Log.d("Device","ACTION_ACL_CONNECTED");
                    break;
                }
                case BluetoothDevice.ACTION_ACL_DISCONNECTED: {
                    Log.d("Device","ACTION_ACL_DISCONNECTED");
                    break;
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connectThread!=null){
            connectThread.cancel();
            connectThread = null;
        }
        if(acceptThread!=null){
            acceptThread.cancel();
            acceptThread = null;
        }

    }
}
