package com.tukla.www.tukla;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.time.LocalDateTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoneActivity extends AppCompatActivity implements Serializable {

    String fback = "";
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
        database.getReference("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    Session session = sessionSnapshot.getValue(Session.class);
                    if(session.getBooking().getBookingID().equals(bookingID)) {

                        if(role.equals("DRIVER")) {
                            btnConfirm.setVisibility(View.VISIBLE);
                            paymentDriver.setVisibility(View.VISIBLE);
                            paymentPassenger.setVisibility(View.GONE);
                            name.setText(session.getBooking().getUser().getFullname());
                            paymentDriver.setText(session.getBooking().getFare()+"");
                        } else {

                            database.getReference("history").child(sessionSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //History history = dataSnapshot.getValue(History.class);
                                    passengerLayout.setVisibility(View.VISIBLE);
                                    btnConfirm.setVisibility(View.VISIBLE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(DoneActivity.this);
                                    builder.setTitle("Thank you for riding with us!");
                                    builder.setMessage("Please give a feedback about your trip and your driver " + session.getDriver().getFullname());
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            paymentPassenger.setText(session.getBooking().getFare()+"");
                            name.setText(session.getDriver().getFullname());
                            plateNumber.setVisibility(View.VISIBLE);
                            plateNumber.setText(session.getDriver().getDriver().getPlateNumber());
                        }
                        locationStart.setText(session.getBooking().getOriginText());
                        locationEnd.setText(session.getBooking().getDestinationText());
                        distanceDone.setText(session.getBooking().getDistance()+" KM");

//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                        StorageReference imgRef = storageRef.child("images/"+session.getBooking().getUser().getUserID()+".jpg");
//                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Glide.with(imageView.getContext())
//                                        .load(uri)
//                                        .fitCenter()
//                                        .into(imageView);
//                            }
//                        });

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent;
                                if(role.equals("DRIVER")) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("sessions").child(sessionSnapshot.getKey()).child("isDone").setValue(true);
                                    DatabaseReference historyRef = database.getReference("history");
                                    historyRef.child(sessionSnapshot.getKey()).setValue(
                                            new History(
                                                    session,
                                                    Double.parseDouble(paymentDriver.getText().toString()),
                                                    LocalDateTime.now().toString(),
                                                    fback
                                            )
                                    );
                                    FirebaseDatabase.getInstance().getReference("bookings").child(bookingID).removeValue();
                                    intent = new Intent(DoneActivity.this,DriverActivity.class);
                                    finish();
                                    startActivity(intent);
                                } else {
                                    database.getReference("history")
                                            .child(sessionSnapshot.getKey())
                                            .child("feedback")
                                            .setValue(feedback.getText().toString());

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