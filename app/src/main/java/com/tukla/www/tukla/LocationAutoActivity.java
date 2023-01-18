package com.tukla.www.tukla;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LocationAutoActivity extends AppCompatActivity implements PlaceSelectionListener {

    private List<Places> myPlacesList=new ArrayList<>( );
    private RecyclerView placesRecyclerView;
    private PlacesAdapter mPlacesAdapter;
    AutoCompleteTextView locationSearch;
    String[] destlocs = new String[5];
    Geocoder geocoder;
    ArrayAdapter<String> adapter;
    ImageButton imgRightClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_auto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_autoLocation);
        setSupportActionBar(toolbar);
        geocoder = new Geocoder(this);
        locationSearch = (AutoCompleteTextView) findViewById(R.id.location_text);

        //Class Place Adapter
        //mPlacesAdapter=new PlacesAdapter( myPlacesList );

        //Recyclar view for Places
        //placesRecyclerView=(RecyclerView) findViewById( R.id.placesRecyclerView) ;

        //RecyclerView Animation..
        //placesRecyclerView.setHasFixedSize( true );
        //RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager( getApplicationContext() );
        //placesRecyclerView.setLayoutManager( mlayoutManager );
        //placesRecyclerView.setItemAnimator( new DefaultItemAnimator() );
        //placesRecyclerView.setAdapter( mPlacesAdapter );


        //placesData();

        //adapter = new ArrayAdapter<String>
          //      (this,android.R.layout.select_dialog_item,destlocs);

        //locationSearch.setThreshold(5);
       // locationSearch.setAdapter(adapter);

        locationSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "ASDASDAS", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        locationSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if(count>4) {
//                    if(count % 5 == 0) {
//
//                        //String[] x = {"Paoay","batac","laoag","currimao","teast"};
//                        adapter = new ArrayAdapter<String>
//                                (getBaseContext(),android.R.layout.select_dialog_item,setAddressItems(s.toString()));
//                        locationSearch.setAdapter(adapter);
//                    }
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    private void placesData(){
        Places place=new Places( "Cyberia Smart Homes","Cyberjaya","10.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A1","Cyberjaya,Selangor","5.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A2","Cyberjaya,Selangor","8.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A3","Cyberjaya","4.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B1","Cyberjaya","13.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B2","Cyberjaya","1.2KM" );
        myPlacesList.add(place);
        place=new Places( "Cyberia Smart Homes,Block A1","Cyberjaya,Selangor","5.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A2","Cyberjaya,Selangor","8.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A3","Cyberjaya","4.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B1","Cyberjaya","13.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B2","Cyberjaya","1.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A1","Cyberjaya,Selangor","5.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A2","Cyberjaya,Selangor","8.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block A3","Cyberjaya","4.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B1","Cyberjaya","13.2KM" );
        myPlacesList.add(place);

        place=new Places( "Cyberia Smart Homes,Block B2","Cyberjaya","1.2KM" );
        myPlacesList.add(place);




    }

    private ArrayList<String> setAddressItems(String paramLocString) {
        List<Address> addressList = null;
        ArrayList<String> returnVal = new ArrayList<String>();

        try {
            addressList = geocoder.getFromLocationName(paramLocString,5);

            for(int i=0; i<addressList.size(); i++) {
                returnVal.add(addressList.get(i).getAddressLine(0));
                Log.d("Locations",returnVal.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    public void setItems(View v) {
        Toast.makeText(getApplicationContext(), locationSearch.getText().toString(), Toast.LENGTH_SHORT).show();
        ArrayList<String> dest = setAddressItems(locationSearch.getText().toString());
        if(!dest.isEmpty()) {
            adapter = new ArrayAdapter<String>
                    (this,android.R.layout.select_dialog_item,dest);

            locationSearch.setThreshold(5);
            locationSearch.setAdapter(adapter);
        } else
            Toast.makeText(getBaseContext(), "No such place found! Try again.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPlaceSelected(Place place) {
        Toast.makeText(getApplicationContext(), "" + place.getName() + place.getLatLng(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getApplicationContext(), "" + status.toString(), Toast.LENGTH_LONG).show();
    }

}