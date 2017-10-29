package com.evasler.clientapp;

import android.*;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.location.LocationListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;


public class CheckinNew extends AppCompatActivity {
    LocationManager mLocationManager;
    double latitude;
    double longitude;

    SharedPreferences  generalPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_new);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        generalPrefs = getSharedPreferences("preferences", MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            }
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L,
        20F, mLocationListener);
        if (mLocationManager != null) {
            Location location = mLocationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        findViewById(R.id.sendBt).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doCheckin();
            }});
        }}
    private void doCheckin(){
       String name= ((EditText)findViewById(R.id.name)).getText().toString();
       String category= ((EditText)findViewById(R.id.category)).getText().toString();
        String poi = Integer.toHexString((Double.toString(longitude) + Double.toString(latitude)).hashCode());
        String photo="photo";
        ClientRequestInsert cr= new ClientRequestInsert(poi,name,category,0,latitude,longitude,photo);
        (new initSocketInsert()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,cr);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude= location.getLatitude();
            longitude=location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    class initSocketInsert extends AsyncTask<ClientRequestInsert,String,String > {

        @Override
        protected String doInBackground(ClientRequestInsert... params) {
            String ip= generalPrefs.getString("insertServerIp","");
            int port =2998;
            Socket socket1 = null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            ClientRequestInsert cr= params[0];
            try {

                socket1 = new Socket(ip, port);

                out = new ObjectOutputStream(socket1.getOutputStream());
                out.writeObject(cr);
                in = new ObjectInputStream(socket1.getInputStream());

            }
            catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket1.close();
                } catch (IOException e)	{
                    e.printStackTrace();
                }
            }

            return null;
        }
        }
    }
