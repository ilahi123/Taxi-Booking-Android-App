package com.tukla.www.tukla;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;


public class LocationAutoActivity extends AppCompatActivity implements PlaceSelectionListener {

    private List<Places> myPlacesList=new ArrayList<>( );
    private RecyclerView placesRecyclerView;
    private PlacesAdapter mPlacesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_auto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_autoLocation);
        setSupportActionBar(toolbar);

        //Class Place Adapter
        mPlacesAdapter=new PlacesAdapter( myPlacesList );

        //Recyclar view for Places
        placesRecyclerView=(RecyclerView) findViewById( R.id.placesRecyclerView) ;

        //RecyclerView Animation..
        placesRecyclerView.setHasFixedSize( true );
        RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager( getApplicationContext() );
        placesRecyclerView.setLayoutManager( mlayoutManager );
        placesRecyclerView.setItemAnimator( new DefaultItemAnimator() );
        placesRecyclerView.setAdapter( mPlacesAdapter );

        placesData();


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


    @Override
    public void onPlaceSelected(Place place) {


    }

    @Override
    public void onError(Status status) {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}