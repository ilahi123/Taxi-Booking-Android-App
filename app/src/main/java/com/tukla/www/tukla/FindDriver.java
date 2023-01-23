package com.tukla.www.tukla;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_find_driver );

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mySessionsRef = database.getReference().child("sessions");
        mySessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {

                    Session mySession = sessionSnapshot.getValue(Session.class);
                    Log.d("SESSION DATA", sessionSnapshot.toString());
                    Log.d("SESSION OBJ", mySession.toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
