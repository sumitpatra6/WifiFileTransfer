package com.example.wififiletransfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG=MainActivity.class.getSimpleName();
    private final IntentFilter intentFilter = new IntentFilter();
    private final int FINE_LOCATION_PERMISSION_CODE = 100;
    private final int INTERNAL_STORAGE_READ_PERMISSION_CODE = 101;
    private final int INTERNAL_STORAGE_WRITE_PERMISSION_CODE = 101;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button searchButton;
    WifiP2pManager manager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.deviceRecyclerView);
        searchButton = findViewById(R.id.searching);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiDirectBroadCastReceiver(manager, mChannel, recyclerView, this);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        checkForFileAndStoragePermission();

    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "registering activity");
        super.onResume();
        registerReceiver(receiver, intentFilter);
        searchDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void checkForFileAndStoragePermission(){
        Log.d(LOG_TAG, "Checking for file system permission");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    INTERNAL_STORAGE_READ_PERMISSION_CODE);
        }
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNAL_STORAGE_WRITE_PERMISSION_CODE);
//        }
    }

    public void searchDevices() {
        Log.d(LOG_TAG, "Searching for devices.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CODE);
            Log.e(LOG_TAG, "Permission missing");
            return;
        }else{
            discoverPeers();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(LOG_TAG, String.valueOf(requestCode));
        if(requestCode == FINE_LOCATION_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(LOG_TAG, "Fine location permission granted.");
                discoverPeers();

            }else{
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == INTERNAL_STORAGE_READ_PERMISSION_CODE || requestCode == INTERNAL_STORAGE_WRITE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Log.d(LOG_TAG, "Permission not granted. Closing app.");
                System.exit(0);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void discoverPeers(){
        Log.d(LOG_TAG, "Now discovering peers");
        manager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // What to do on success
                Log.d(LOG_TAG, "Discover successful.");
                searchButton.setText(R.string.search_devices);
            }

            @Override
            public void onFailure(int i) {
                Log.e(LOG_TAG, String.format("Failure reason %d", i));
            }
        });
    }

    public void searchDevices(View view) {
        searchButton.setText(R.string.searching_devices);
        searchDevices();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}