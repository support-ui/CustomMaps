package com.example.custommaps;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String formattedDate;
    int hora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Calendar calander = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        formattedDate  = df.format(calander.getTime());
        hora = Integer.parseInt(formattedDate);



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

        try {
            if(hora>=20 && hora< 23){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.retro)); //Define color blanco.
                }
                Boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this,R.raw.style2)

                );

                if(!success){
                    Log.e("MapActivity","Style parsing Failed");
                }
            }else{
                if(hora>=23 && hora< 00){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.black)); //Define color blanco.
                    }
                    Boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(this,R.raw.style3)
                    );

                    if(!success){
                        Log.e("MapActivity","Style parsing Failed");
                    }
                }

            }

        }catch (Resources.NotFoundException e){
            Log.e("MapsActivity","Can't find Style. Error");
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));

        Toast.makeText(this, ""+hora, Toast.LENGTH_SHORT).show();
    }
}
