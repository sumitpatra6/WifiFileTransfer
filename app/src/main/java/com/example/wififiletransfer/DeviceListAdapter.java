package com.example.wififiletransfer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final String LOG_TAG = DeviceListAdapter.class.getSimpleName();
    private List<WifiP2pDevice> devices;
    private Context mContext;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    public DeviceListAdapter(Context mContext, List<WifiP2pDevice> devices, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.devices = devices;
        this.mContext = mContext;
        this.manager = manager;
        this.channel = channel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.device_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.device_name);
            tv.setOnClickListener(this);
        }

        public void bindTo(WifiP2pDevice device) {
            tv.setText(device.deviceName);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "Item Clicked");
            final WifiP2pDevice currentDevice = devices.get(getAdapterPosition());
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = currentDevice.deviceAddress;
            Log.d(LOG_TAG, String.format("Going to connect to device %s", currentDevice.deviceAddress));

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(LOG_TAG, "Connected Successfully. Going to file sending page.");
                    // start the sharing activity
                    Intent intent = new Intent(mContext, FileSendActivity.class);
                    intent.putExtra(Constants.DEVICE_ADDRESS, currentDevice.deviceAddress);
                    intent.putExtra(Constants.DEVICE_NAME, currentDevice.deviceName);
                    mContext.startActivity(intent);
                }

                @Override
                public void onFailure(int i) {
                    Log.d(LOG_TAG, "Connection unsuccessful");
                    Toast.makeText(mContext, "Could not connect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
