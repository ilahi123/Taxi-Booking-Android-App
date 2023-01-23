package com.tukla.www.tukla;

import static com.tukla.www.tukla.R.id.map;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.icu.util.Freezable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DriverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LoaderManager.LoaderCallbacks<Object>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    RelativeLayout products_select_option;
    ImageButton product1, product2;
    Button myCurrentloc,book_button;

    private static final String TAG = DriverActivity.class.getSimpleName();
    private final static int PERMISSION_MY_LOCATION = 3;

    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationRequest request;
    View mapView;
    private boolean mRequestingLocationUpdates;
    CameraUpdate cLocation;
    double latitude,longitude;
    Marker now;

    Geocoder geocoder;
    List<Address> addresses;
    TextView txtDropOff;
    TextView txtOrigin;
    TextView priceText;
    TextView distanceText;
    Map<String, Booking> hashMapBookings;
    private FirebaseAuth mAuth;
    private final String CODE_SCAN = "Scan";
    private final String CODE_CANCEL = "Cancel";
    private final String CODE_DONE = "Done";

    Button acceptButton;
    LinearLayout layoutDetails;
    String clickedBookingID;

    String sessionID;
    LatLng targetDestination;
    Marker passengerMarker;
    Marker passengerDestinationMarker;
    Driver loggedInDriverObj;
    User passengerBookedObj;

    private Boolean isDriverArrived = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupLocationManager();
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("drivers").child(mAuth.getUid()).child("updatedAt").setValue(LocalDateTime.now().toString());
        hashMapBookings = new HashMap<>();
        super.onCreate( savedInstanceState );
        setContentView( R.layout.driver_activity_main );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById( R.id.map );
        mapView = mapFragment.getView();
        mapFragment.getMapAsync( this );
        CheckMapPermission();

        layoutDetails = findViewById(R.id.layoutDetails);
        acceptButton = findViewById(R.id.accept_button);
        txtDropOff = findViewById(R.id.txt_dropoff);
        txtOrigin = findViewById(R.id.txt_origin);

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        priceText = (TextView) findViewById(R.id.price_text);
        distanceText = (TextView) findViewById(R.id.distance_text);

        book_button=(Button)findViewById(R.id.book_button);
        book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book_button.getText().toString().equals(CODE_DONE)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference bookingsRef = database.getReference("bookings");
                    bookingsRef.child(clickedBookingID).child("isArrived").setValue(true);
                    hashMapBookings = new HashMap<>();
                    book_button.setText("Accept");
                    book_button.setBackgroundColor(getColor(R.color.green));
                    addBookingsMarkers();
                } else if(book_button.getText().toString().equals(CODE_CANCEL)) {
                    layoutDetails.setVisibility(View.GONE);
                    book_button.setText(CODE_SCAN);
                    addBookingsMarkers();
                    LatLng positionUpdate = new LatLng( DriverActivity.this.latitude,DriverActivity.this.longitude );
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( positionUpdate, 15 );
                    mMap.addMarker(new MarkerOptions().position(positionUpdate)
                            .title("You are Here!"));
                    mMap.animateCamera( update );
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                book_button.setText(CODE_CANCEL);
                book_button.setBackgroundColor(getColor(R.color.colorRed));
                acceptButton.setVisibility(View.GONE);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference bookingsRef = database.getReference("bookings");
                bookingsRef.child(clickedBookingID).child("isAccepted").setValue(true);
                bookingsRef.child(clickedBookingID).child("driver").setValue(loggedInDriverObj);
                bookingsRef.child(clickedBookingID).child("driverLocation").setValue(new LatLngDefined(DriverActivity.this.latitude,DriverActivity.this.longitude));
            }
        });

        DatabaseReference bookingsRef = database.getReference("bookings");
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bookingSnapshot: dataSnapshot.getChildren()) {
                    Booking booking = bookingSnapshot.getValue(Booking.class);
                    if(bookingSnapshot.getKey().equals(clickedBookingID)) {

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });

        DatabaseReference sessionRef = database.getReference("sessions");
        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    Session mySession = sessionSnapshot.getValue(Session.class);
                    if(mySession.getBookingID().equals(clickedBookingID)) {
                        if(mySession.getIsDriverArrived()) {
                            targetDestination = new LatLng(hashMapBookings.get(clickedBookingID).getDestination().getLatitude(),hashMapBookings.get(clickedBookingID).getDestination().getLongitude());
                            passengerMarker.remove();
                            isDriverArrived = true;
                            addMarker(targetDestination, "Destination",2);
                            book_button.setText(CODE_DONE);
                            book_button.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        database.getReference("drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    if(userSnapshot.getKey().equals(mAuth.getUid())) {
                        loggedInDriverObj = userSnapshot.getValue(Driver.class);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setToPassengerMap(String userID) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefSessions = database.getReference().child("sessions");

        LatLngDefined driverLocation = new LatLngDefined( DriverActivity.this.latitude,DriverActivity.this.longitude);
        Session newSession = new Session(mAuth.getUid(),userID,clickedBookingID,LocalDateTime.now().toString(),driverLocation,false);
        sessionID = myRefSessions.push().getKey();
        myRefSessions.child(sessionID).setValue(newSession);
        targetDestination = new LatLng(
                hashMapBookings.get(clickedBookingID).getOrigin().getLatitude(),
                hashMapBookings.get(clickedBookingID).getOrigin().getLongitude()
        );

        mMap.clear();
        passengerMarker = addMarker(
                new LatLng(
                        hashMapBookings.get(clickedBookingID).getOrigin().getLatitude(),
                        hashMapBookings.get(clickedBookingID).getOrigin().getLongitude()
                ), "Pick Up Point", 1);

        passengerDestinationMarker = addMarker(
                new LatLng(
                        hashMapBookings.get(clickedBookingID).getDestination().getLatitude(),
                        hashMapBookings.get(clickedBookingID).getDestination().getLongitude()
                ), "Drop Off Point", 2);

//        Intent intent = new Intent(DriverActivity.this, FindPassenger.class);
//        finish();
//        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        //mGoogleApiClient.connect();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient.isConnected()){
            setInitialLocation();

        }

        LocationManager service = (LocationManager) getSystemService( LOCATION_SERVICE );
        boolean enabled = service.isProviderEnabled( LocationManager.GPS_PROVIDER );

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            buildAlertMessageNoGps();
        }
        if(enabled){
            //mMap.animateCamera( update );
/*
            Toast.makeText( MainActivity.this, "OnResume:"+latitude+","+longitude, Toast.LENGTH_SHORT ).show();
*/

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style ) );

            if (!success) {
                Log.e( TAG, "Style parsing failed." );
            }
        } catch (Resources.NotFoundException e) {
            Log.e( TAG, "Can't find style. Error: ", e );
        }


        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then over   riding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //This line will show your current location on Map with GPS dot
        mMap.setMyLocationEnabled( true );
        locationButton();
        addBookingsMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(isDriverArrived)
                    return false;
                if(marker.getTitle().equals(""))
                    return false;
                layoutDetails.setVisibility(View.VISIBLE);
                mMap.clear();
                clickedBookingID = marker.getTitle();

                Log.d("MARKER OPTIONS", clickedBookingID);
                marker.setTitle(hashMapBookings.get(clickedBookingID).getOriginText());

                txtOrigin.setText(hashMapBookings.get(clickedBookingID).getOriginText());
                txtDropOff.setText(hashMapBookings.get(clickedBookingID).getDestinationText());
                priceText.setText(hashMapBookings.get(clickedBookingID).getFare()+"");
                distanceText.setText(hashMapBookings.get(clickedBookingID).getDistance()+"");

                passengerMarker = addMarker(
                        new LatLng(
                            hashMapBookings.get(clickedBookingID).getOrigin().getLatitude(),
                            hashMapBookings.get(clickedBookingID).getOrigin().getLongitude()
                        ), hashMapBookings.get(clickedBookingID).getOriginText(), 1
                );

                passengerDestinationMarker = addMarker(
                        new LatLng(
                                hashMapBookings.get(clickedBookingID).getDestination().getLatitude(),
                                hashMapBookings.get(clickedBookingID).getDestination().getLongitude()
                        ), hashMapBookings.get(clickedBookingID).getDestinationText(), 2
                );

                //Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /*
        Toast.makeText( MainActivity.this, "OnStart:"+latitude+","+longitude, Toast.LENGTH_SHORT ).show();
*/
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference sessionsReference = firebaseDatabase.getReference("sessions");
                if(sessionID!=null) {

                    sessionsReference.child(sessionID).child("driverLocation").setValue(new LatLngDefined(location.getLatitude(),location.getLongitude()));
                    Location passengerLoc = new Location("");
                    passengerLoc.setLatitude(targetDestination.latitude);
                    passengerLoc.setLongitude(targetDestination.latitude);

//                    float distanceToPassenger = location.distanceTo(passengerLoc);
//                    Log.d("DISTANCE",distanceToPassenger+"");
//
//                    if(distanceToPassenger<=10) {
//                        sessionsReference.child(sessionID).child("driverLocation").setValue(new LatLngDefined(location.getLatitude(),location.getLongitude()));
//                    }
                }
            }
        });

    }

    private Marker addMarker(LatLng location, String title, int type) {
        MarkerOptions passengerMarkerOptions = new MarkerOptions();

        if(type==1)
            passengerMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_green_icon));
        else if(type==2)
            passengerMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag));

        passengerMarkerOptions.position(
                new LatLng(location.latitude,location.longitude)
        );
        passengerMarkerOptions.title(title);
        return mMap.addMarker(passengerMarkerOptions);
    }

    private void setupLocationManager() {
        //buildGoogleApiClient();
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .addApi( com.google.android.gms.location.places.Places.GEO_DATA_API )
                    .addApi( com.google.android.gms.location.places.Places.PLACE_DETECTION_API )
                    .build();
            //mGoogleApiClient = new GoogleApiClient.Builder(this);
        }
        googleApiClient.connect();
        createLocationRequest();
    }

    protected void createLocationRequest() {

        request = new LocationRequest();
        request.setSmallestDisplacement( 10 );
        request.setFastestInterval( 50000 );
        request.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        request.setNumUpdates( 3 );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest( request );
        builder.setAlwaysShow( true );

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings( googleApiClient,
                        builder.build() );


        result.setResultCallback( new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        setInitialLocation();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    DriverActivity.this,
                                    REQUEST_CHECK_SETTINGS );
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        } );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d( "onActivityResult()", Integer.toString( resultCode ) );

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {

                        setInitialLocation();

                        Toast.makeText( DriverActivity.this, "Location enabled", Toast.LENGTH_LONG ).show();
                        mRequestingLocationUpdates = true;
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText( DriverActivity.this, "Location not enabled", Toast.LENGTH_LONG ).show();
                        mRequestingLocationUpdates = false;
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    private void setInitialLocation() {

        if (ActivityCompat.checkSelfPermission( DriverActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( DriverActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates( googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation = location;
                double lat=location.getLatitude();
                double lng=location.getLongitude();

                DriverActivity.this.latitude=lat;
                DriverActivity.this.longitude=lng;

                try {
                    if(now !=null){
                        now.remove();
                    }
                    LatLng positionUpdate = new LatLng( DriverActivity.this.latitude,DriverActivity.this.longitude );
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( positionUpdate, 15 );
                   // now=mMap.addMarker(new MarkerOptions().position(positionUpdate)
                     //       .title("You are Here!"));

                    mMap.animateCamera( update );
                    //myCurrentloc.setText( ""+latitude );


                } catch (Exception ex) {

                    ex.printStackTrace();
                    Log.e( "MapException", ex.getMessage() );

                }

                //Geocode current location details
                try {
                    geocoder = new Geocoder(DriverActivity.this, Locale.ENGLISH);
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    StringBuilder str = new StringBuilder();
                    if (Geocoder.isPresent()) {
                        /*Toast.makeText(getApplicationContext(),
                                "geocoder present", Toast.LENGTH_SHORT).show();*/
                        android.location.Address returnAddress = addresses.get(0);

                        String localityString = returnAddress.getAddressLine (0);
                        //String city = returnAddress.getAddressLine(1);
                        //String region_code = returnAddress.getAddressLine(2);
                        //String zipcode = returnAddress.getAddressLine(3);

                        str.append( localityString ).append( "" );
                        // str.append( city ).append( "" ).append( region_code ).append( "" );
                        // str.append( zipcode ).append( "" );

                        //myCurrentloc.setText(str);
//                        Toast.makeText(getApplicationContext(), str,
//                                Toast.LENGTH_SHORT).show();

                    } else {
                    /*    Toast.makeText(getApplicationContext(),
                                "geocoder not present", Toast.LENGTH_SHORT).show();*/
                    }

// } else {
// Toast.makeText(getApplicationContext(),
// "address not available", Toast.LENGTH_SHORT).show();
// }
                } catch (IOException e) {
// TODO Auto-generated catch block

                    Log.e("tag", e.getMessage());
                }



            }

        } );
    }

    private void CheckMapPermission() {


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (ActivityCompat.checkSelfPermission( DriverActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission( DriverActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( DriverActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1002 );
            } else {

                setupLocationManager();
            }
        } else {
            setupLocationManager();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );


        switch (requestCode) {
            case 1002: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this,
                            Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {

                        setupLocationManager();

                    }
                } else {

                    Toast.makeText( DriverActivity.this, "Permission Denied", Toast.LENGTH_SHORT ).show();
                    //finish();
                }
            }
            break;
        }

    }


    public void getLatLang(String placeId) {
        Places.GeoDataApi.getPlaceById( googleApiClient, placeId )
                .setResultCallback( new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place place = places.get( 0 );

                            LatLng latLng = place.getLatLng();

                            try {

                                CameraUpdate update = CameraUpdateFactory.newLatLngZoom( latLng, 15 );
                                mMap.animateCamera( update );


                            } catch (Exception ex) {

                                ex.printStackTrace();
                                Log.e( "MapException", ex.getMessage() );

                            }

                            Log.i( "place", "Place found: " + place.getName() );
                        } else {
                            Log.e( "place", "Place not found" );
                        }
                        places.release();
                    }
                } );
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //AlertMessageNoGps();


    }


    @Override
    public void onConnectionSuspended(int i) {
        //checkLocaionStatus();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }


    //GET CURRENT LOCATION BUTTON POSITION....
    private void locationButton() {

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById( map );

        View locationButton = ((View) mapFragment.getView().findViewById( Integer.parseInt( "1" ) ).
                getParent()).findViewById( Integer.parseInt( "2" ) );
        if (locationButton != null && locationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

            // Align it to - parent BOTTOM|LEFT
            params.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
            params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
            params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT, 0 );
            params.addRule( RelativeLayout.ALIGN_PARENT_TOP, 0 );

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 150,
                    getResources().getDisplayMetrics() );
            params.setMargins( margin, margin, margin, margin );

            locationButton.setLayoutParams( params );
        }

    }


    //Button Location Search
    public void myLocation(View view) {

        //CHANGE ACTIVITY
        Intent intent = new Intent( DriverActivity.this, LocationAutoActivity.class );
        startActivity( intent );


    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setTitle( "GPS Not Enabled" )
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=AIzaSyBJfB6BWBgpsU-_EBtsZ3SiuYUDhz0sJJE";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }


    private void addBookingsMarkers() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refBookings = database.getReference("bookings");
        refBookings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    Booking booking = childSnapshot.getValue(Booking.class);

                    if(!booking.getIsAccepted()) {
                        hashMapBookings.put(key, booking);
                        addMarker(
                                new LatLng(
                                        booking.getOrigin().getLatitude(),
                                        booking.getOrigin().getLongitude()
                                ), key, 1
                        );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        mMap.addMarker(new MarkerOptions().position(positionUpdate)
//                .title("Your Location"));
    }
}
