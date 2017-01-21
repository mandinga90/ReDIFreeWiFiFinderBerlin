package com.github.mandinga90.redifreewififinderberlin.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.mandinga90.redifreewififinderberlin.R;
import com.github.mandinga90.redifreewififinderberlin.entities.Venue;
import com.github.mandinga90.redifreewififinderberlin.functional.Consumer;
import com.github.mandinga90.redifreewififinderberlin.ui.fragments.RetainFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    //private static final String NETWORK_FRAGMENT_TAG = .getString(R.string.NETWORK_FRAGMENT_TAG);
    private static final float DEFAULT_ZOOM_LEVEL = 12;
    private static final LatLng BERLIN = new LatLng(52.523924,13.403962);
    private BitmapDescriptor GREEN_MARKER, ROSE_MARKER, AZURE_MARKER, YELLOW_MARKER, ORANGE_MARKER;
    private LatLng currentMarkerLocation;
    private Marker currentMarker;
    private boolean addMode, updateMode;
    private HashMap<Marker,Venue> markerVenueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeMarkerColors();

        setupNetworkFragment();

        FloatingActionButton fabCreateNewVenue = (FloatingActionButton) this.findViewById(R.id.fab_create_venue);
        FloatingActionButton fabUpdateVenue = (FloatingActionButton) findViewById(R.id.fab_update_venue);
        FloatingActionButton fabDeleteVenue = (FloatingActionButton) findViewById(R.id.fab_delete_venue);

        refreshVisibilityOfFloatingActionButtons();

        // what happens if add button is clicked...
        fabCreateNewVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createVenueIntent = new Intent(getApplicationContext(), CreateVenueActivity.class);
                createVenueIntent.putExtra("latitude", currentMarkerLocation.latitude);
                createVenueIntent.putExtra("longitude", currentMarkerLocation.longitude);
                startActivity(createVenueIntent);
            }
        });

        // what happens if update button is clicked...
        fabUpdateVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMarker != null){
                    Intent updateVenueIntent = new Intent(getApplicationContext(), UpdateVenueActivity.class);

                    Venue currentVenue = markerVenueMap.get(currentMarker);

                    // put attributes as extra in intent
                    updateVenueIntent.putExtra("id", currentVenue.getId() );
                    updateVenueIntent.putExtra("name", currentVenue.getName() );
                    updateVenueIntent.putExtra("category", currentVenue.getCategory() );
                    updateVenueIntent.putExtra("address", currentVenue.getAddress() );
                    updateVenueIntent.putExtra("description", currentVenue.getDescription() );
                    updateVenueIntent.putExtra("latitude", currentVenue.getLatitude() );
                    updateVenueIntent.putExtra("longitude", currentVenue.getLongitude() );

                    startActivity(updateVenueIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No valid venue selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // what happens if delete button is clicked...
        fabDeleteVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMarker != null){

                    final Venue currentVenue = markerVenueMap.get(currentMarker);

                    getRetainFragment().deletePlace(new Consumer<Void>() {
                        @Override
                        public void apply(Void v) {
                            Toast.makeText(getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                            setupNetworkFragment();
                            updateMode = false;
                            addMode = false;
                            refreshVisibilityOfFloatingActionButtons();
                        }

                        @Override
                        public Object get() {
                            return currentVenue;
                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(), "No valid venue selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeMarkerColors() {
        GREEN_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        ROSE_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        AZURE_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        YELLOW_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        ORANGE_MARKER = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
    }

    private void setupNetworkFragment() {

        getRetainFragment().getPlaces(new Consumer<List<Venue>>() {
            @Override
            public void apply(List<Venue> venues) {
                setupMapView(venues);
            }

            @Override
            public Object get() {
                return null;
            }
        });
        //getRetainFragment().getCategories...
    }

    private void setupMapView(final List<Venue> venues) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(new OnMapReadyCallback(){

            @Override
            public void onMapReady(final GoogleMap googleMap) {

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BERLIN, DEFAULT_ZOOM_LEVEL));

                MainActivityPermissionsDispatcher
                        .enableMyLocationWithCheck(MainActivity.this, googleMap);

                googleMap.getUiSettings().setZoomControlsEnabled(true);

                showVenues(googleMap, venues);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        // Set current location
                        currentMarkerLocation = latLng;

                        // Creating a marker
                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(GREEN_MARKER);

                        // Setting the position for the marker
                        markerOptions.position(latLng);
                        // Show venues
                        showVenues(googleMap, venues);

                        // Adds marker to map
                        googleMap.addMarker(markerOptions);

                        // show add button
                        addMode = true;
                        updateMode = false;
                        refreshVisibilityOfFloatingActionButtons();

                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if( markerVenueMap.containsKey(marker)){

                            updateMode = true;
                            addMode = false;
                            currentMarker = marker;

                            refreshVisibilityOfFloatingActionButtons();

                            // remove created marker ?

                        }
                        else{

                            // marker not created yet
                            updateMode = false;
                            addMode = true;

                            refreshVisibilityOfFloatingActionButtons();

                        }

                        return false;
                    }
                });
            }
        });
    }

    private void refreshVisibilityOfFloatingActionButtons() {
        FloatingActionButton createNewVenue = (FloatingActionButton) findViewById(R.id.fab_create_venue);
        FloatingActionButton updateVenue = (FloatingActionButton) findViewById(R.id.fab_update_venue);
        FloatingActionButton deleteVenue = (FloatingActionButton) findViewById(R.id.fab_delete_venue);
        if(addMode){
            createNewVenue.setVisibility(View.VISIBLE);
            updateVenue.setVisibility(View.INVISIBLE);
            deleteVenue.setVisibility(View.INVISIBLE);
        }
        else if(updateMode){
            createNewVenue.setVisibility(View.INVISIBLE);
            updateVenue.setVisibility(View.VISIBLE);
            deleteVenue.setVisibility(View.VISIBLE);
        }
        else{
            createNewVenue.setVisibility(View.INVISIBLE);
            updateVenue.setVisibility(View.INVISIBLE);
            deleteVenue.setVisibility(View.INVISIBLE);
        }
    }

    private void showVenues(GoogleMap googleMap, List<Venue> venues) {
        googleMap.clear();
        for(int listIndex = 0; listIndex < venues.size(); listIndex++){
            Venue venue = venues.get(listIndex);

            BitmapDescriptor markerColor = getMarkerColor(venue.getCategory());

            MarkerOptions newVenueOnMap = new MarkerOptions()
                    .position(new LatLng(venue.getLatitude(), venue.getLongitude()))
                    .title(venue.getName())
                    .snippet(venue.getAddress())
                    .icon(markerColor);
            Marker marker = googleMap.addMarker(newVenueOnMap);
            markerVenueMap.put(marker, venue);
        }
    }

    private BitmapDescriptor getMarkerColor(String category) {
        switch(category){
            case "bar" : return ORANGE_MARKER;
            case "restaurant" : return ROSE_MARKER;
            case "coworking_space" : return AZURE_MARKER;
            default : return YELLOW_MARKER;
        }
    }

    private RetainFragment getRetainFragment(){
        RetainFragment retainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.NETWORK_FRAGMENT_TAG));
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction().add(retainFragment, getString(R.string.NETWORK_FRAGMENT_TAG)).commit();
        }
        return retainFragment;
    }

    @SuppressWarnings("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void enableMyLocation(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
    }


    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showPermissionDeniedInfo() {
        Toast.makeText(this, "Please enable Location permission for proper app usage",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
