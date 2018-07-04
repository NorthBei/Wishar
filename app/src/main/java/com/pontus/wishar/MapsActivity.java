package com.pontus.wishar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.pontus.wishar.map.MapController;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LocationCallback mLocationCallback;
    MapController mapcontroller;
    Double lat=25.052258,lng=121.544325;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Location location = intent.getParcelableExtra("location");
        if (location != null) {
            lat=location.getLatitude();
            lng=location.getLongitude();

            //do something
        }

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mapcontroller = new MapController(this, googleMap);
        String filename = "iTaiwan";
        mapcontroller.controller("wifimap/" + filename + ".json", filename);//chose your file
        mapcontroller.MoveMap(new LatLng(lat, lng), 15);//chose your location and map size
      //get position

        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.select_wifi:
                showSelectWifiDailog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectWifiDailog(){
        String[] listItems = {"Hello", "World","A","B","C","D","E","F","G","H","I","J"};

        new MaterialDialog.Builder(this)
            .title(R.string.select_wifi_dialog_title)
            .items(listItems)
            .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                    //doc : https://github.com/afollestad/material-dialogs#multi-choice-list-dialogs
                    /**
                     * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected check box to actually be selected
                     * (or the newly unselected check box to be unchecked).
                     * See the limited multi choice dialog example in the sample project for details.
                     **/
                    return true;
                }
            })
            .positiveText(R.string.dialog_check_btn)
            .negativeText(R.string.dialog_cancel_btn)
            .show();
    }
}
