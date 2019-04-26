package android.example.com.prescriptminder;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private Button bluetooth_discoverable;
    private Button bluetooth_scan;
    private Switch bluetooth_switch;
    private ListView devices_list;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> devices;
    private TextView available_text;
    private TextView connectivity_status;
    private View separator;

    private static final int REQUEST_ENABLED = 11;
    private static final int REQUEST_DISCOVERABLE = 10;
    private static final int REQUEST_COARSE_LOCATION = 9;
    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetooth_discoverable = findViewById(R.id.bluetooth_discoverable);
        devices_list = findViewById(R.id.devices_list);
        bluetooth_switch = findViewById(R.id.bluetooth_toggle);
        bluetooth_scan = findViewById(R.id.bluetooth_scan);
        devices = new ArrayList<>();
        available_text = findViewById(R.id.available_text);
        separator = findViewById(R.id.separator);
        connectivity_status = findViewById(R.id.connectivity_status);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, devices);

        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported !!");
        }

        if (bluetoothAdapter.isEnabled()) {
            bluetooth_switch.setChecked(true);
        }
        else {
            bluetooth_switch.setChecked(false);
        }


        bluetooth_discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isDiscovering()) {
                    Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);               //Make the device discoverable
                    startActivityForResult(discoverable, REQUEST_DISCOVERABLE);
                }
            }
        });

        bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    bluetoothON();

                } else {
                    bluetoothOFF();
                }
            }
        });

        bluetooth_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPairedDevices();
                available_text.setVisibility(View.VISIBLE);
                separator.setVisibility(View.VISIBLE);
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    if (checkCoarseLocationPermission()) {
                        devices.removeAll(devices);
                        bluetoothAdapter.startDiscovery();
                    }
                }
            }
        });

        checkCoarseLocationPermission();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case STATE_LISTENING:
                    connectivity_status.setText("Listening...");
                    break;
                case STATE_CONNECTING:
                    connectivity_status.setText("Connecting...");
                    break;
                case STATE_CONNECTED:
                    connectivity_status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    connectivity_status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Coarse location permission granted");
                } else {
                    showToast("Coarse location permission not granted");
                }
                break;
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bt = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bt.getName() != null) {
                    devices.add(bt.getName());
                }
                else {
                    devices.add("No Name");
                }
                updateList();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("Discovery started");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("Discovery finished");
            }
        }
    };

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLED) {
            if (resultCode == RESULT_OK) {
                showToast("Bluetooth is enabled !!");
            } else if (resultCode == RESULT_CANCELED) {
                showToast("Bluetooth enabling cancelled !!");
            }
        }
    }

    public void bluetoothON() {
        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported !!");
            finish();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLED);
        }
    }

    public void bluetoothOFF() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            available_text.setVisibility(View.INVISIBLE);
            separator.setVisibility(View.INVISIBLE);
            if (devices.size() > 0) {
                arrayAdapter.clear();
            }
        }
    }

    public void getPairedDevices() {
        if (bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();

            if (bluetoothDeviceSet.size() > 0) {
                for (BluetoothDevice bt : bluetoothDeviceSet) {
                    devices.add(bt.getName());
                }
                updateList();
            }
        }
    }

    private void updateList() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        devices_list.setAdapter(arrayAdapter);
    }
}
