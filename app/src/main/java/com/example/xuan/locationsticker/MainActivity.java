package com.example.xuan.locationsticker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //LocationManager locationManager;
    //LocationListener locationListener;
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
          }
        }
    }
   */

    static ArrayList<String> locations = new ArrayList<>();
    static ArrayList<LatLng> stickers = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.xuan.locationsticker", Context.MODE_PRIVATE);
        ArrayList<String> lat = new ArrayList<>();
        ArrayList<String> log = new ArrayList<>();

        locations.clear();
        stickers.clear();
        lat.clear();
        log.clear();

        try{
            locations = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Locations", ObjectSerializer.serialize(new ArrayList<>())));
            lat = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Latitude", ObjectSerializer.serialize(new ArrayList<>())));
            log = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Longitude", ObjectSerializer.serialize(new ArrayList<>())));
        }catch(Exception e){
            e.printStackTrace();
        }

        if(locations.size() > 0 && lat.size() > 0 && log.size() > 0){
            if(locations.size() == lat.size() && locations.size() == log.size()){
                for(int i = 0; i < lat.size(); i++){
                    stickers.add(new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(log.get(i))));
                }
            }
        }else{
            locations.add("Add a new place...");
            stickers.add(new LatLng(0,0));
        }
            /*
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locations.add("Add a new place...");
                    stickers.add(new LatLng(location.getLatitude(),location.getLongitude()));
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
        }
        */

        ListView listView = findViewById(R.id.listLayout);
        //store the list of locations;


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, locations);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //check whether it works for the listener
                //Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Locations", position);

                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                locations.remove(position);
                stickers.remove(position);

                arrayAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Place Deleted", Toast.LENGTH_LONG).show();

                return true;
            }
        });

    }
}
