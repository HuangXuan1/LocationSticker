package com.example.xuan.locationsticker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    public void getCenter(Location location, String name){
        if(location == null){
            return;
        }
        LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(curLocation).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 15));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                getCenter(lastLocation, "Your Position");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        //check
        //Toast.makeText(this, Integer.toString(intent.getIntExtra("Locations", 0)), Toast.LENGTH_LONG).show();

        if(intent.getIntExtra("Locations", 0) == 0) {
            //Zoom in the current location;
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getCenter(location, "Your Position");
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

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                getCenter(lastLocation, "Your Position");
            }

        }else{
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.stickers.get(intent.getIntExtra("Locations", 0)).latitude);
            placeLocation.setLongitude(MainActivity.stickers.get(intent.getIntExtra("Locations", 0)).longitude);

            getCenter(placeLocation, MainActivity.locations.get(intent.getIntExtra("Locations", 0)));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try{
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(listAddresses != null && listAddresses.size() > 0){
                if(listAddresses.get(0).getThoroughfare() != null){
                    if(listAddresses.get(0).getSubThoroughfare() != null){
                        address += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(address.equals("")){
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            address += time.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title("New Location Sticker"));

        MainActivity.locations.add(address);
        MainActivity.stickers.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.xuan.locationsticker", Context.MODE_PRIVATE);
        try{
            sharedPreferences.edit().putString("Locations", ObjectSerializer.serialize(MainActivity.locations)).apply();

            ArrayList<String> lat = new ArrayList<>();
            ArrayList<String> log = new ArrayList<>();
            for(LatLng c : MainActivity.stickers){
                lat.add(Double.toString(c.latitude));
                log.add(Double.toString(c.longitude));
            }
            sharedPreferences.edit().putString("Latitude", ObjectSerializer.serialize(lat)).apply();
            sharedPreferences.edit().putString("Longitude", ObjectSerializer.serialize(log)).apply();

        }catch(Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

    }
}
