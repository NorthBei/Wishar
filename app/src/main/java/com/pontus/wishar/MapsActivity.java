package com.pontus.wishar;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pontus.wishar.map.MapController;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks , GoogleMap.OnCameraIdleListener{

    private final int REQ_LOCATION = 9487;
    MapController mapcontroller;
    Double lat = 25.052258, lng = 121.544325;
    String[] listItems = {"iTaiwan", "Starbucks", "Tainan", "TaipeiFree"};
    Integer[] selected = new Integer[listItems.length];
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < listItems.length; i++) {
            selected[i] = i;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        this.googleMap = googleMap;
        googleMap.setOnCameraIdleListener(this);
        mapcontroller = new MapController(this, googleMap, listItems);

        mapcontroller.MoveMap(new LatLng(lat, lng), 15);//chose your location and map size
        mapcontroller.ShowArea(lat, lng, selected);
        if (hasLocationPermissions()) {
            codeNeedLocationPermission();
        }
    }

    @Override
    public void onCameraIdle() {
        LatLng latLng = googleMap.getCameraPosition().target;
        // float zoom = googleMap.getCameraPosition().zoom;
        mapcontroller.ShowArea(latLng.latitude, latLng.longitude, selected);

//        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
//
//        List<GeoHash> geoHashList = new GeoHashBoundingBoxQuery(new BoundingBox(
//                new WGS84Point(bounds.northeast.latitude,bounds.northeast.longitude),
//                new WGS84Point(bounds.southwest.latitude,bounds.southwest.longitude)
//        )).getSearchHashes();
//
//        List<Wifi> list = new ArrayList<>();
//
//        for (GeoHash geoHash: geoHashList) {
//            Log.d("MapActivity", "onCameraIdle: "+geoHash);
//            //Wifi[] wifidata = WisharDB.getDB(getBaseContext()).mapDao().loadAreawifis(geoHash.toBase32() + "%");
//            //list.addAll(Arrays.asList(wifidata));
//        }
    }

    @SuppressLint("MissingPermission")// handle request permission in hasLocationPermissions method
    @AfterPermissionGranted(REQ_LOCATION)
    private void codeNeedLocationPermission() {
        googleMap.setMyLocationEnabled(true);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    mapcontroller.MoveMapAnim(new LatLng(lat, lng), 15);
                    mapcontroller.ShowArea(lat, lng, selected);
                }
            }
        });
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
                showSelectWifiDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectWifiDialog() {

        new MaterialDialog.Builder(this)
                .title(R.string.select_wifi_dialog_title)
                .items(listItems)
                .itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        //doc : https://github.com/afollestad/material-dialogs#multi-choice-list-dialogs
                        mapcontroller.ShowArea(lat, lng, which);
                        selected = which;
                        Log.d("which: ", String.valueOf(which.length));
                        return true;
                    }
                })
                .positiveText(R.string.dialog_check_btn)
                .negativeText(R.string.dialog_cancel_btn)
                .alwaysCallMultiChoiceCallback()
                .show();
    }

    private boolean hasLocationPermissions() {
        String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        boolean hasPermissions = EasyPermissions.hasPermissions(this, perms);

        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this,
                    "若未允許Wishar使用定位權限，公共Wi-Fi地圖將無法完全正常運作，是否重新設定權限？",
                    REQ_LOCATION,
                    perms);
        }
        return hasPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        codeNeedLocationPermission();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}