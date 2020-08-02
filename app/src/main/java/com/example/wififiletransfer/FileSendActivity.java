package com.example.wififiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class FileSendActivity extends AppCompatActivity {

    private final String LOG_TAG = FileSendActivity.class.getSimpleName();
    private String deviceName;
    private String deviceAddress;
    private TextView deviceNameTextView;
    private  TextView deviceAddressTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_send);
        deviceNameTextView = findViewById(R.id.file_send_device_name);
        deviceAddressTextView = findViewById(R.id.file_send_device_address);
        Intent intent = getIntent();
        intent.getStringExtra("DEVICE_ADDRESS");
        deviceName = intent.getStringExtra(Constants.DEVICE_NAME);
        deviceAddress = intent.getStringExtra(Constants.DEVICE_ADDRESS);
        deviceNameTextView.setText(deviceName);
        deviceAddressTextView.setText(deviceAddress);
        getImagesPath();
    }

    private void getImagesPath(){
        Uri allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(LOG_TAG, allImagesUri.toString());
        String projection  [] =

    }
}