package android.example.com.prescriptminder.fragments;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.example.com.prescriptminder.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothConnectFragment extends Fragment {

    private static BluetoothConnectFragment bluetoothConnectFragment;

    private Switch bluetooth_switch;
    private ListView devices_list;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> devices;
    private ArrayList<BluetoothDevice> btDevices;
    private TextView available_text;
    private static TextView connectivity_status;
    private EditText send_text;
    private Button send_btn;
    private View separator;
    private static Communication communication;

    private static final int REQUEST_ENABLED = 11;
    private static final int REQUEST_DISCOVERABLE = 10;
    private static final int REQUEST_COARSE_LOCATION = 9;
    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;

    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final UUID MY_UUID = UUID.fromString("00001105-0000-1000-8000-00805f9b34fb");

    public BluetoothConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        MainActivity.navigation.setSelectedItemId(R.id.navigation_connect_device);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button bluetooth_discoverable = view.findViewById(R.id.bluetooth_discoverable);
        devices_list = view.findViewById(R.id.devices_list);
        bluetooth_switch = view.findViewById(R.id.bluetooth_toggle);
        send_text = view.findViewById(R.id.send_text);
        send_btn = view.findViewById(R.id.send_btn);
        Button bluetooth_scan = view.findViewById(R.id.bluetooth_scan);
        devices = new ArrayList<>();
        btDevices = new ArrayList<>();
        available_text = view.findViewById(R.id.available_text);
        separator = view.findViewById(R.id.separator);
        connectivity_status = view.findViewById(R.id.connectivity_status);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, devices);

        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported !!");
        }

        if (bluetoothAdapter.isEnabled()) {
            bluetooth_switch.setChecked(true);
        }
        else {
            bluetooth_switch.setChecked(false);
        }

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = send_text.getText().toString().trim();
                sendUrl(text);
            }
        });

        bluetooth_discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isDiscovering()) {
                    Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(discoverable, REQUEST_DISCOVERABLE);
                }
            }
        });

        bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    devices.clear();
                    bluetoothON();

                } else {
                    bluetoothOFF();
                }
            }
        });

        bluetooth_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                available_text.setVisibility(View.VISIBLE);
                separator.setVisibility(View.VISIBLE);
                devices.clear();
                getPairedDevices();
//                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//                    if (checkCoarseLocationPermission()) {
//                        devices.removeAll(devices);
//                        bluetoothAdapter.startDiscovery();
//                    }
//                }
            }
        });

        devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btDevices.get(i));
                showToast("Device name : " + btDevices.get(i).getName());
                clientClass.start();

                connectivity_status.setText("Connecting...");
            }
        });

        checkCoarseLocationPermission();

    }

    public static BluetoothConnectFragment getBluetoothConnectFragment() {
        if (bluetoothConnectFragment == null)
            bluetoothConnectFragment = new BluetoothConnectFragment();
        return bluetoothConnectFragment;
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

    public static Boolean checkConnectivityStatus() {
        Log.e("Bluetooth", connectivity_status.getText().toString());
        if(connectivity_status.getText().toString().equals("Connected"))
            return true;
        else
            return false;
    }

    public static void sendUrl(String url) {
        if(!url.isEmpty() && checkConnectivityStatus()) {
            communication.write(url.getBytes());
            Log.e("BluetoothActivity", "URL sent : " + url);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getContext().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        getContext().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
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
                btDevices.add(bt);
                if (bt.getName() != null) {
                    arrayAdapter.add(bt.getName());
                }
                else {
                    arrayAdapter.add(bt.getAddress());
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
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLED) {
            if (resultCode == RESULT_OK) {
                showToast("Bluetooth is enabled !!");
                bluetooth_switch.setChecked(true);
            } else{
                showToast("Bluetooth enabling cancelled !!");
                bluetooth_switch.setChecked(false);
            }
        }
    }

    public void bluetoothON() {
        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported !!");
            bluetooth_switch.setChecked(false);
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
                    if(!devices.contains(bt.getName())) {
                        arrayAdapter.add(bt.getName());
                        btDevices.add(bt);
                    }
                }
                updateList();
            }
        }
    }

    private void updateList() {
        //Set<String> devices_set = new TreeSet<>();
        //devices_set.addAll(devices);
        //arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        //devices_list.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        devices_list.setAdapter(arrayAdapter);
    }

    private class ClientClass extends Thread
    {

        private BluetoothDevice device;
        private BluetoothSocket socket;

        ClientClass(BluetoothDevice device1) {
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            }
            catch (NullPointerException e) {
                try {
                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                if(socket != null) {
                    communication = new BluetoothConnectFragment.Communication(socket);
                    communication.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    private class Communication extends Thread {

        private BluetoothSocket bluetoothSocket;
        private OutputStream outputStream;

        Communication(BluetoothSocket socket){

            bluetoothSocket = socket;
            OutputStream tempOut = null;
            try {
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = tempOut;
        }

        void write(byte[] bytes) {

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
