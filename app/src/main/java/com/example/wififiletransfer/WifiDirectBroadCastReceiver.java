package com.example.wififiletransfer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WifiDirectBroadCastReceiver extends BroadcastReceiver {
    private String LOG_TAG = WifiDirectBroadCastReceiver.class.getSimpleName();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private List<WifiP2pDevice> peers = new ArrayList();
    private RecyclerView recyclerView;
    private DeviceListAdapter deviceListAdapter = null;

    private WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            peers.clear();
            peers.addAll(wifiP2pDeviceList.getDeviceList());
//            Log.d(LOG_TAG, peers.toString());
            for(WifiP2pDevice device : peers){
                Log.d(LOG_TAG, device.deviceName);
            }
            if(deviceListAdapter == null){
                Log.d(LOG_TAG, "Creating the adapter");
                deviceListAdapter = new DeviceListAdapter(activity, peers, manager, channel);
                recyclerView.setAdapter(deviceListAdapter);
            }else{
                Log.d(LOG_TAG, "notifying adapter about dataset change");
                deviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    WifiDirectBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, RecyclerView recyclerView, MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Inside onReceive");
        String action = intent.getAction();
        Log.d(LOG_TAG, action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            } else {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(LOG_TAG, "Discovering");
            if (manager != null) {

                manager.requestPeers(channel, myPeerListListener);
            }
        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
