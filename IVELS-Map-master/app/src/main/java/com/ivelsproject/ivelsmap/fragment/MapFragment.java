package com.ivelsproject.ivelsmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ivelsproject.ivelsmap.App;
import com.ivelsproject.ivelsmap.DetailsVenueActivity;
import com.melnykov.fab.FloatingActionButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.ivelsproject.ivelsmap.R;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleMap googleMap;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location myLocation;
    protected Marker myLocationMarker;
    protected Marker tempMarker;
    protected SharedPreferences preference;
    protected FloatingActionButton fab;
    protected FloatingActionButton fab2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_map);
        fab.hide(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                            .zoom(15)
                            .build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    App.toastMessage(getActivity(), getString(R.string.waiting_location));
                }

                startLocationUpdates();
            }
        });

        fab2 = (FloatingActionButton) view.findViewById(R.id.fab_uny_map);
        fab2.hide(false);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(-7.775860, 110.384000))
                        .zoom(15)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!App.isGPSEnabled(getActivity())) {
            App.showGpsAlert(getActivity());
        }
        preference = getActivity().getSharedPreferences(App.PREFS_NAME,
                Context.MODE_PRIVATE);

        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.menu_map);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show(true);
            }
        }, 500);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab2.show(true);
            }
        }, 650);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(
                        App.getDouble(preference, "latitude", -7.795580),
                        App.getDouble(preference, "longitude", 110.369490)
                ))
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (myLocation != null) {
            drawMyLocationMarker();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .zoom(14)
                    .build();

            if (tempMarker != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(tempMarker.getPosition()));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        startLocationUpdates();

        drawVenueMarker("KPLT FT UNY", "14 Room", -7.769140, 110.388206);
        drawVenueMarker("Gedung Kuliah T. Otomotif", "10 Room", -7.770465, 110.387967);
        drawVenueMarker("Gedung Kuliah T. Elektro", "9 Room", -7.771140, 110.387194);
        drawVenueMarker("Gedung Kuliah T. Informatika", "15 Room", -7.771953, 110.386685);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        drawMyLocationMarker();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        tempMarker = marker;
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        TextView title = (TextView) view.findViewById(R.id.info_title);
        title.setText(marker.getTitle());
        TextView snippet = (TextView) view.findViewById(R.id.info_snippet);
        snippet.setText(marker.getSnippet());
        TextView info = (TextView) view.findViewById(R.id.info_click_guide);
        info.setText(R.string.touch_here);

        if (myLocationMarker == null) {
            return view;
        } else {
            if (marker.getTitle().equals(myLocationMarker.getTitle())) {
                return null;
            } else {
                return view;
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), DetailsVenueActivity.class);
        startActivity(intent);
    }

    private void drawMyLocationMarker() {
        if (myLocationMarker != null) {
            myLocationMarker.remove();
        }
        if (myLocation != null) {
            SharedPreferences.Editor editor = preference.edit();
            App.putDouble(editor, "longitude", myLocation.getLongitude());
            App.putDouble(editor, "latitude", myLocation.getLatitude());
            editor.apply();

            myLocationMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                            .title(getString(R.string.my_location))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin))
            );
        }
    }

    private void drawVenueMarker(String venueName, String venueSnippet, double venueLatitude, double venueLongitude) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(venueLatitude, venueLongitude))
                .title(venueName)
                .snippet(venueSnippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
        googleMap.addMarker(marker);
    }

    public void setUpMapIfNeeded() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}