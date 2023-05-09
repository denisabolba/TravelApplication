package com.example.notesapplication;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    EditText searchEdt;
    TextView Latitude,Longitude;

    //vars
    SupportMapFragment mapFragment;
    private GoogleMap nMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private MarkerOptions markerOptions;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        Latitude = findViewById(R.id.Latitude);
        Longitude = findViewById(R.id.Longitude);
        searchEdt = findViewById(R.id.searchEdt);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapInitialize();

    }

    private void mapInitialize() {
        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(16)
                .setMinUpdateIntervalMillis(3000)
                .build();

        searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    goToSearchLocation();
                }

                return false;
            }

            private void goToSearchLocation() {

                String searchLocation = searchEdt.getText().toString();

                Geocoder geocoder = new Geocoder(GoogleMaps.this);
                List<Address> list = new ArrayList<>();
                try{
                    list = geocoder.getFromLocationName(searchLocation,1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(list.size()>0){
                    Address address = list.get(0);
                    String location = address.getAdminArea();
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();
                    goToLatLang(latitude,longitude,17f);
                    if(marker != null){
                        marker.remove();
                    }
                    markerOptions = new MarkerOptions();
                    markerOptions.title(location);
                    markerOptions.draggable(true);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.position(new LatLng(latitude,longitude));
                    marker = nMap.addMarker(markerOptions);
                }

            }

            private void goToLatLang(double latitude, double longitude, float v) {

                LatLng latLng = new LatLng(latitude,longitude);
                Latitude.setText(String.valueOf(latitude));
                Longitude.setText(String.valueOf(longitude));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,17f);
                nMap.animateCamera((update));


            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GoogleMaps.this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        nMap = googleMap;

        Dexter.withContext(GoogleMaps.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        if (ActivityCompat.checkSelfPermission(GoogleMaps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GoogleMaps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        nMap.setMyLocationEnabled(true);
                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GoogleMaps.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        Toast.makeText(GoogleMaps.this, "Permission"+ permissionDeniedResponse.getPermissionName() + "" + "was denied!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        if(nMap != null){
            nMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {

                    Geocoder geocoder = new Geocoder(GoogleMaps.this);
                    List<Address> list = null;
                    try{
                        LatLng markerPosition = marker.getPosition();
                        list = geocoder.getFromLocation(markerPosition.latitude,markerPosition.longitude,1);
                        Latitude.setText(String.valueOf(markerPosition.latitude));
                        Longitude.setText(String.valueOf(markerPosition.longitude));
                        Address address = list.get(0);
                        marker.setTitle(address.getAdminArea());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
        }

    }
}