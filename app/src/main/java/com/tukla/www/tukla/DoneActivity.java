package com.tukla.www.tukla;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DoneActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        //CircleImageView imageView = findViewById(R.id.profile_image);
        TextView name = findViewById(R.id.name);
        TextView plateNumber = findViewById(R.id.plateNumberDone);
        TextView locationStart = findViewById(R.id.locationStart);
        TextView locationEnd = findViewById(R.id.locationEnd);
        TextView distanceDone = findViewById(R.id.distanceDone);
        TextView paymentPassenger = findViewById(R.id.paymentPassenger);
        EditText paymentDriver = findViewById(R.id.paymentDriver);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        LinearLayout passengerLayout = findViewById(R.id.for_passenger_feedback);
        EditText feedback = findViewById(R.id.passenger_feedback);

        //Session thisSession = (Session) getIntent().getSerializableExtra("SESSION");
        String bookingID = (String) getIntent().getSerializableExtra("BOOKING_ID");
        String role = (String) getIntent().getSerializableExtra("ROLE");

        //FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    Booking booking = sessionSnapshot.getValue(Booking.class);

                    database.getReference().child("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot sDataSnapshot) {
                            for (DataSnapshot sessionSnapshot: sDataSnapshot.getChildren()) {
                                Session ssd = sessionSnapshot.getValue(Session.class);
                                if(ssd.getBooking().getBookingID().equals(bookingID)) {

                                    if(role.equals("DRIVER")) {
                                        btnConfirm.setVisibility(View.VISIBLE);
                                        paymentDriver.setVisibility(View.VISIBLE);
                                        paymentPassenger.setVisibility(View.GONE);
                                        name.setText(booking.getUser().getFullname());
                                        paymentDriver.setText(booking.getFare()+"");
                                    }
                                    else {

                                        passengerLayout.setVisibility(View.VISIBLE);
                                        btnConfirm.setVisibility(View.VISIBLE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DoneActivity.this);
                                        builder.setTitle("Thank you for riding with us!");
                                        builder.setMessage("Please give a feedback about your trip and your driver " + booking.getDriver().getFullname());
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do something when the OK button is clicked
                                                dialog.dismiss();
                                            }
                                        });
                                        paymentPassenger.setText(booking.getFare()+"");
                                        name.setText(booking.getDriver().getFullname());
                                        plateNumber.setVisibility(View.VISIBLE);
                                        plateNumber.setText(booking.getDriver().getDriver().getPlateNumber());
                                    }
                                    locationStart.setText(booking.getOriginText());
                                    locationEnd.setText(booking.getDestinationText());
                                    distanceDone.setText(booking.getDistance()+" KM");

                                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent;
                                            if(role.equals("DRIVER")) {
                                                Session ss = new Session(booking.getDriver(),booking,LocalDateTime.now().toString(),ssd.getDriverLocation(),true,true);
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                                database.getReference().child("history")
                                                        .child(booking.getBookingID()).setValue(
                                                        new History(
                                                                ss,
                                                                Double.parseDouble(paymentDriver.getText().toString()),
                                                                LocalDateTime.now().toString()
                                                        )
                                                );
                                                FirebaseDatabase.getInstance().getReference("sessions").child(sessionSnapshot.getKey()).removeValue();
                                                FirebaseDatabase.getInstance().getReference("bookings").child(bookingID).removeValue();
                                                intent = new Intent(DoneActivity.this,DriverActivity.class);
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                database.getReference("feedbacks")
                                                        .child(booking.getBookingID())
                                                        .child("feedback")
                                                        .setValue(feedback.getText().toString());
                                                database.getReference("feedbacks")
                                                        .child(booking.getBookingID())
                                                        .child("updatedAt")
                                                        .setValue(LocalDateTime.now().toString());

                                                FirebaseDatabase.getInstance().getReference("bookings").child(bookingID).removeValue();
                                                intent = new Intent(DoneActivity.this,MainActivity.class);
                                                finish();
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}