package com.tukla.www.tukla;

import static com.tukla.www.tukla.R.id.custom;
import static com.tukla.www.tukla.R.id.map;
import static com.tukla.www.tukla.R.id.myLocation;
import static com.tukla.www.tukla.R.id.nav_logOut;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LoaderManager.LoaderCallbacks<Object>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Serializable {

    //RelativeLayout products_select_option;
    Button myCurrentloc,book_button;

    private static final String TAG = MainActivity.class.getSimpleName();
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
    List<android.location.Address> addresses;
    TextView txtDropOff;
    TextView priceText;
    TextView distanceText;
    LatLng myPosition;
    LatLng positionUpdate;
    //LatLng targetDestination;
    private FirebaseAuth mAuth;
    private final String CODE_BOOK = "Book";
    private final String CODE_CANCEL = "Cancel";
    private final String CODE_DRIVER_WAIT = "Waiting Driver";
    private final String CODE_DRIVER_OK = "Driver Arrived";
    private final String CODE_SOS = "SOS";
    private final String CODE_DONE = "Done";
    String recentBookingID;
    Marker driverMarker;
    Marker destinationMarker;
    Booking myBookingObj;
    //TextView platenumber_text;
    //TextView drivername_text;
    //private String driverName;
    //private String driverPlateNumber;
    User loggedInUser;
    Session thisSession;
    //CardView driver_info;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupLocationManager();
        mAuth = FirebaseAuth.getInstance();
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(mAuth.getUid()).child("updatedAt").setValue(LocalDateTime.now().toString());
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById( R.id.map );
        mapView = mapFragment.getView();
        mapFragment.getMapAsync( this );

        CheckMapPermission();

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener( toggle );
        toggle.syncState();
        navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        txtDropOff = (TextView) findViewById(R.id.txt_dropoff);
        //
        //Buttons Select Product option
        //select_btn = (ImageButton) findViewById( R.id.img_selected );
        //products_select_option = (RelativeLayout) findViewById( R.id.products_select_option );
        myCurrentloc=(Button) findViewById( R.id.myCLocation );
        priceText = (TextView) findViewById(R.id.price_text);
        distanceText = (TextView) findViewById(R.id.distance_text);
        //drivername_text = (TextView) findViewById(R.id.drivername_text);
        //platenumber_text = (TextView) findViewById(R.id.platenumber_text);

        book_button=(Button)findViewById(R.id.book_button);
        //book_button.setText(CODE_SOS);
        book_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    if(book_button.getText().toString().equals(CODE_BOOK)) {
                        Address destinationResult = setDestination();

                        if(destinationResult!=null) {
                            //driver_info.setVisibility(View.VISIBLE);
                            mMap.clear();
                            book_button.setText(CODE_CANCEL);
                            book_button.setBackgroundColor(getColor(R.color.colorRed));
                            String myDestination = destinationResult.getAddressLine(0);
                            txtDropOff.setText(myDestination);
                            myPosition = new LatLng(MainActivity.this.latitude,MainActivity.this.longitude);

                            //positionUpdate = new LatLng( destinationResult.getLatitude(), destinationResult.getLongitude() );
                            String directionUrl = getDirectionsUrl(myPosition,positionUpdate);

                            //AsyncDirectionsAPI asyncDirectionsAPI = new AsyncDirectionsAPI();
                            //asyncDirectionsAPI.execute(directionUrl);

                            //double distanceVal = 2;
                            // creating a new variable for our request queue
                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, directionUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        double distanceVal = 0;
                                        // now we get our response from API in json object format.
                                        // in below line we are extracting a string with its key
                                        // value from our json object.
                                        // similarly we are extracting all the strings from our json object.

                                        JSONArray routesObjArray = response.getJSONArray("routes");
                                        JSONObject distanceObj = routesObjArray.getJSONObject(0);

                                        JSONArray c = distanceObj.getJSONArray("legs");
                                        for (int i = 0 ; i < distanceObj.length(); i++) {
                                            JSONObject obj = c.getJSONObject(i);
                                            JSONObject distanceFinal =  obj.getJSONObject("distance");
                                            distanceVal = distanceFinal.getDouble("value");
                                            break;
                                        }

                                        displayFare(distanceVal);
                                    } catch (JSONException e) {
                                        // if we do not extract data from json object properly.
                                        // below line of code is use to handle json exception
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                // this is the error listener method which
                                // we will call if we get any error from API.
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // below line is use to display a toast message along with our error.
                                    Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // at last we are adding our json
                            // object request to our request
                            // queue to fetch all the json data.
                            queue.add(jsonObjectRequest);

                            destinationMarker = mMap.addMarker(new MarkerOptions().position(positionUpdate)
                                    .title("Destination")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag))
                            );

                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom( positionUpdate, 15 );
                            mMap.animateCamera(update);

    //                        waitBookAccept();

                        }
                    } else if(book_button.getText().toString().equals(CODE_CANCEL)){
                        //driver_info.setVisibility(View.GONE);
                        mMap.clear();
                        book_button.setText(CODE_BOOK);
                        book_button.setBackgroundColor(getColor(R.color.green));

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference("bookings").child(recentBookingID).removeValue();
                    } else if (book_button.getText().equals(CODE_DRIVER_WAIT)) {
                        Toast.makeText(getApplicationContext(), "Driver is on the way", Toast.LENGTH_SHORT);
                    } else if (book_button.getText().toString().equals(CODE_SOS)) {
                        // Check if app has permission to make phone calls
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // Request permission if it does not have it
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                        } else {
                            // App has permission, make phone call
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+63495451698"));
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"An error occured! Try selecting another place.",Toast.LENGTH_SHORT);
                }
            }
        });

        DatabaseReference myBookingsRef = database.getReference().child("bookings");
        myBookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot bookingSnapshot : dataSnapshot.getChildren()){
//                    Booking booking = bookingSnapshot.getValue(Booking.class);
//                    if(booking.getIsAccepted()) {
//                        Intent intent = new Intent( MainActivity.this, FindDriver.class );
//                        startActivity( intent );
//                    }
                    if(bookingSnapshot.getKey().equals(recentBookingID)) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        myBookingObj = booking;
                        if(booking.getIsAccepted() && !booking.getIsArrived()) {
                            //book_button.setText(CODE_DRIVER_WAIT);
                            book_button.setBackgroundColor(getColor(R.color.blue));
                            Toast.makeText(getBaseContext(), "Driver accepted your booking, please wait", Toast.LENGTH_SHORT).show();

                            database.getReference("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot sessionSnap : dataSnapshot.getChildren()){
                                        Session sessionx = sessionSnap.getValue(Session.class);
                                        if(sessionx.getBooking().getBookingID().equals(booking.getBookingID())) {
                                            showCustomDialog(sessionx);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else if(booking.getIsAccepted() && booking.getIsArrived()) {
                            //priceText.setText(0);
                            //distanceText.setText(0);
                            //mMap.clear();
                            //txtDropOff.setText("");
                            //book_button.setText(CODE_BOOK);
                            //book_button.setBackgroundColor(getColor(R.color.green));
                            Intent intent = new Intent( MainActivity.this, DoneActivity.class );

                            intent.putExtra("BOOKING_ID", booking.getBookingID());
                            intent.putExtra("ROLE","PASSENGER");
                            finish();
                            startActivity( intent );
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mySessionsRef = database.getReference().child("sessions");
        mySessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {

                    Session mySession = sessionSnapshot.getValue(Session.class);
                    if(mySession.getBooking().getBookingID().equals(recentBookingID)) {
                        thisSession = mySession;
                        LatLng positionUpdate = new LatLng(mySession.getDriverLocation().getLatitude(),mySession.getDriverLocation().getLongitude());
                        driverMarker.remove();
                        MarkerOptions myMarkerOptions = new MarkerOptions();
                        myMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tricycle));
                        myMarkerOptions.position(positionUpdate);
                        //myMarkerOptions.title("Driver: "+mySession.getDriver().getFullname()+" | Plate No. "+mySession.getDriver().getDriver().getPlateNumber());
                        driverMarker = mMap.addMarker(myMarkerOptions);
                        driverMarker.setTag(mySession.getDriver());

                        CameraUpdate camerUpdate = CameraUpdateFactory.newLatLngZoom( positionUpdate, 20 );
                        mMap.animateCamera(camerUpdate);

                        Location driverLocation = new Location("");
                        driverLocation.setLatitude(mySession.getDriverLocation().getLatitude());
                        driverLocation.setLongitude(mySession.getDriverLocation().getLongitude());

                        Location myCurrLocation = new Location("");
                        myCurrLocation.setLatitude(myPosition.latitude);
                        myCurrLocation.setLongitude(myPosition.longitude);
                        float driverDistance = myCurrLocation.distanceTo(driverLocation);

                        if(mySession.getIsDriverArrived()) {
                            Location dest = new Location("");
                            dest.setLatitude(myBookingObj.getDestination().getLatitude());
                            dest.setLongitude(myBookingObj.getDestination().getLongitude());
                            driverDistance = myCurrLocation.distanceTo(dest);
                        } else {
                            book_button.setText(driverDistance+"m");
                        }

                        if(driverDistance<=15) {
                            if (!mySession.getIsDriverArrived()) {
                                mySessionsRef.child(sessionSnapshot.getKey()).child("isDriverArrived").setValue(true);
                                book_button.setText(CODE_SOS);
                                book_button.setBackgroundColor(getColor(R.color.colorRed));
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Your Driver is here!");
                                builder.setMessage("I am about 15 meters away. My plate number is "+myBookingObj.getDriver().getDriver().getPlateNumber());
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do something when the OK button is clicked
                                        dialog.dismiss();
                                    }
                                });

                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mySessionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Session session = dataSnapshot.getValue(Session.class);
                if(session.getBooking().getBookingID().equals(recentBookingID)) {
                    Toast.makeText(getBaseContext(), "Sorry, the driver cancelled your booking.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    if(userSnapshot.getKey().equals(mAuth.getUid())) {
                        loggedInUser = userSnapshot.getValue(User.class);
                        Log.d("SAVED USER OBJECT TAG",loggedInUser.toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        //mGoogleApiClient.connect();
        /** setup drawer **/
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(mAuth.getUid()).child("updatedAt").setValue(LocalDateTime.now().toString());

        View navHeader = navigationView.getHeaderView(0);
        CircleImageView profImg = navHeader.findViewById(R.id.profile_image);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child("images/"+mAuth.getUid()+".jpg");
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(profImg.getContext())
                        .load(uri)
                        .fitCenter()
                        .into(profImg);
            }
        });

        database.getReference("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                TextView txtViewName = navHeader.findViewById(R.id.textView2);
                txtViewName.setText(user.getFullname());

                TextView txtViewEmail = navHeader.findViewById(R.id.textView);
                txtViewEmail.setText(mAuth.getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /** */
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

        if(id==R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("ROLE","PASSENGER");
            startActivity(intent);
        } else if(id==nav_logOut) {
            if(!recentBookingID.equals(null)) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                            Session session = sessionSnapshot.getValue(Session.class);
                            if(session.getBooking().getBookingID().equals(recentBookingID)) {
                                if(!session.getIsDone()) {
                                    database.getReference("sessions").child(sessionSnapshot.getKey()).removeValue();
                                    database.getReference("bookings").child(session.getBooking().getBookingID()).removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
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
        driverMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(50,20)));

        //This line will show your current location on Map with GPS dot
        mMap.setMyLocationEnabled( true );
        locationButton();
/*
        Toast.makeText( MainActivity.this, "OnStart:"+latitude+","+longitude, Toast.LENGTH_SHORT ).show();
*/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(book_button.getText().toString().equals(CODE_BOOK)) {
                    try {
                        mMap.clear();
                        List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                        txtDropOff.setText(addressList.get(0).getAddressLine(0));

                        positionUpdate = new LatLng( latLng.latitude,latLng.longitude );
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( positionUpdate, 20 );
                        destinationMarker = mMap.addMarker(new MarkerOptions().position(positionUpdate).icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag)));
                        mMap.animateCamera( update );

                    } catch (IOException e) {
                        e.printStackTrace();
                        txtDropOff.setText("");
                    }
                }
            }
        });
    }

    private void setupLocationManager() {
        //buildGoogleApiClient();
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .addApi( Places.GEO_DATA_API )
                    .addApi( Places.PLACE_DETECTION_API )
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
                                    MainActivity.this,
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

                        Toast.makeText( MainActivity.this, "Location enabled", Toast.LENGTH_LONG ).show();
                        mRequestingLocationUpdates = true;
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText( MainActivity.this, "Location not enabled", Toast.LENGTH_LONG ).show();
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


        if (ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
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

                MainActivity.this.latitude=lat;
                MainActivity.this.longitude=lng;

                try {
                    if(now !=null){
                        now.remove();
                    }
                    LatLng positionUpdate = new LatLng( MainActivity.this.latitude,MainActivity.this.longitude );
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( positionUpdate, 15 );
                    //now=mMap.addMarker(new MarkerOptions().position(positionUpdate)
                      //      .title("Your Location"));

                    mMap.animateCamera( update );
                    //myCurrentloc.setText( ""+latitude );


                } catch (Exception ex) {

                    ex.printStackTrace();
                    Log.e( "MapException", ex.getMessage() );

                }

                //Geocode current location details
                try {
                    geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
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

                        myCurrentloc.setText(str);
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

            if (ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1002 );
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

                    if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this,
                            Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {

                        setupLocationManager();

                    }
                } else {

                    Toast.makeText( MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT ).show();
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

    private Address setDestination() {
        List<Address> addressList = null;

        try {
           // addressList = geocoder.getFromLocationName(paramLocString,1);
            addressList = geocoder.getFromLocation(destinationMarker.getPosition().latitude,destinationMarker.getPosition().longitude,1);
            return addressList.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private double displayFare(double distance) {

        double fare = 0.00;
        double distanceKm = distance/1000;
        if(distanceKm<3) {
            fare = 60.00;
        } else {
            int extraDistance = (int) (distanceKm - 2);
            fare = ((extraDistance*8)+60);
        }

        distanceText.setText(distanceKm+"");
        priceText.setText(fare+"");
        updateFirebase(fare,distanceKm);
        return fare;
    }

    private void updateFirebase(double paramFare, double paramDistance) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myBookingsRef = database.getReference().child("bookings");
        recentBookingID = myBookingsRef.push().getKey();
        LatLngDefined l1 = new LatLngDefined(myPosition.latitude,myPosition.longitude);
        LatLngDefined l2 = new LatLngDefined(positionUpdate.latitude,positionUpdate.longitude);
        myBookingObj = new Booking(recentBookingID,loggedInUser, null,LocalDateTime.now().toString(),l1,l2,false,false, paramFare, paramDistance,myCurrentloc.getText().toString(),txtDropOff.getText().toString());
        myBookingsRef.child(recentBookingID).setValue(myBookingObj);
    }

    private void showCustomDialog(Session mb) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.marker_custom_window, null);

        CircleImageView profImg = dialogView.findViewById(R.id.marker_img);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child("images/" + mb.getDriver().getUserID() + ".jpg");
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(profImg.getContext())
                        .load(uri)
                        .fitCenter()
                        .into(profImg);
            }
        });

        dialogView.findViewById(R.id.for_driver_layout).setVisibility(View.VISIBLE);

        dialogView.findViewById(R.id.txt_driver_found).setVisibility(View.VISIBLE);
        TextView name = dialogView.findViewById(R.id.marker_name);
        name.setText(mb.getDriver().getFullname());

        TextView phone = dialogView.findViewById(R.id.marker_phone_number);
        phone.setText(mb.getDriver().getPhone());

        TextView plateNum = dialogView.findViewById(R.id.marker_plate);
        plateNum.setText(mb.getDriver().getDriver().getPlateNumber());

        Button button = dialogView.findViewById(R.id.marker_btn_accept);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                dialog.dismiss();
            }
        });

    }
}
