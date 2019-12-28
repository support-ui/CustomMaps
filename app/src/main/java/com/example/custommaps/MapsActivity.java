package com.example.custommaps;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    String direccion;
    String formattedDate;
    int hora;
    FloatingActionButton fabAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fabAdd = findViewById(R.id.fabAdd);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Calendar calander = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        formattedDate  = df.format(calander.getTime());
        hora = Integer.parseInt(formattedDate);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcador.remove();
                miUbicacion();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            if(hora>=00 && hora< 06){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.retro));
                }
                Boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this,R.raw.style2)

                );

                if(!success){
                    Log.e("MapActivity","Style parsing Failed");
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.black));
                }
                Boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this,R.raw.style)

                );
                if(!success){
                    Log.e("MapActivity","Style parsing Failed");
                }
            }


        }catch (Resources.NotFoundException e){
            Log.e("MapsActivity","Can't find Style. Error");
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                marcador.remove();
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                double touchLat=latLng.latitude;
                double touchLong=latLng.longitude;


                try {
                    Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(touchLat,touchLong, 1);
                    if (addresses.isEmpty()) {
                        Toast.makeText(getApplicationContext(),"Waiting for Location",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (addresses.size() > 0) {
                            Address DireCalle = addresses.get(0);
                            direccion = (DireCalle.getAddressLine(0));
                            marcador = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(direccion));
                            Toast.makeText(getApplicationContext(), "Direccion: " +direccion, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
            }
        });

        miUbicacion();

    }

    private void setLocation(Location loc){
        if(loc.getLongitude()!=0.0 && loc.getLongitude()!=0.0){
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                if(!list.isEmpty()){
                    Address DireCalle = list.get(0);
                    direccion = (DireCalle.getAddressLine(0));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void AgregarMarcador(double lat,double lng){
        LatLng coodernadas = new LatLng(lat,lng);
        CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coodernadas,16);
        if (marcador!=null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coodernadas)
                .title(direccion));
        mMap.animateCamera(MiUbicacion);
    }

    private void ActualizarUbicacion(Location location){
        if(location!=null){
            lat = location.getLatitude();
            lng = location.getLongitude();
            AgregarMarcador(lat,lng);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            ActualizarUbicacion(location);
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private static int PETICION_PERMISO_LOCATION = 101;

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PETICION_PERMISO_LOCATION);
        } else {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,20,locationListener);
        }

    }

}
