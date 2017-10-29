package com.evasler.clientapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    Button hideBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        hideBt=(Button)findViewById(R.id.hideBt);
        hideBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MapsActivity.this.finish();
            }});
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        holderClass holder = (holderClass)getIntent().getSerializableExtra("holderClass");
        Set<ClientResult> topResults = holder.getTopResults();

        if (topResults == null) {

            return;
        }

        for (ClientResult clientResult : topResults) {
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(clientResult.getLatitude(), clientResult.getLongtitude()));
            options.title(clientResult.getPoiName());
            options.snippet(clientResult.getPoiCategory());
            markers.add(mMap.addMarker(options));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(markers.get(0).getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
