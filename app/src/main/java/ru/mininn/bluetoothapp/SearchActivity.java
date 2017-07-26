package ru.mininn.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mdevelop on 26.07.2017.
 */

public class SearchActivity extends Activity {
    private static final int DISCOVERY_REQUEST = 10;
    private ListView listview;
    private ArrayAdapter<String> deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initView();
        requestDiscovery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            discovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);

    }

    private void initView() {

        listview = (ListView) findViewById(R.id.search_list);
        deviceAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        listview.setAdapter(deviceAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra("device", address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void requestDiscovery(){
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
                DISCOVERY_REQUEST);
    }
    private void discovery() {
        Toast.makeText(this,"discovery",Toast.LENGTH_SHORT).show();
        setProgressBarIndeterminateVisibility(true);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(SearchActivity.this,"onReceive",Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(SearchActivity.this,device.getName(),Toast.LENGTH_SHORT).show();
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    deviceAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (deviceAdapter.getCount() == 0) {
                    Toast.makeText(SearchActivity.this,"Device not found",Toast.LENGTH_LONG).show();
                }
            }
        }
    };



}
