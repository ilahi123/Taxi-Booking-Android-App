package com.tukla.www.tukla;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button Signup;
    private Button Login;
    private EditText email;
    private EditText password;
    private Button SignupDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Signup = findViewById(R.id.buttonsignup);
        SignupDriver = findViewById(R.id.buttonsignup2);
        Login = findViewById(R.id.buttonlogin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();


        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                finish();
                startActivity(intent);
            }
        });

        SignupDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUpDriver.class);
                finish();
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptlogin();
            }
        });
    }

    private void attemptlogin() {
        String emailId = email.getText().toString();
        String password1 = password.getText().toString();
        if(checkEmailPassword(emailId,password1)) {
            Login(emailId, password1);
        }

    }


    public boolean checkEmailPassword(String Email, String Password) {
        Log.d("Vehicle","email:"+email);
        Log.d("Vehicle","password:"+password);
        email.setError(null);
        password.setError(null);
        if(!Email.contains("@"))
        {
            email.requestFocus();
            email.setError("INVALID EMAIL");
        }
        else{

            if(Password.length()<=6)
            {
                password.requestFocus();
                password.setError("Incorrect Pasword");
            }
            else{
                return true;
            }

        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        UpdateUI(currentUser);

    }

    private void UpdateUI(FirebaseUser currentUser) {
    }


    private void Login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            //UpdateUI(firebaseUser);
                            FirebaseDatabase database=FirebaseDatabase.getInstance();

                            DatabaseReference adminsRef = database.getReference("users").child(mAuth.getUid()).child("isAdmin");
                            adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if(snapshot.getValue(Boolean.class)) {
                                        Intent intent;
                                        intent = new Intent(Login.this, AdminActivity.class);
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                                        DatabaseReference myRefUser = database.getReference("users").child(mAuth.getUid()).child("isDriver");
                                        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Intent intent;
                                                // This method is called once with the value
                                                boolean value = dataSnapshot.getValue(Boolean.class);
                                                if(value) {
                                                    intent = new Intent(Login.this, DriverActivity.class);
                                                    finish();
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(Login.this, MainActivity.class);
                                                    finish();
                                                    startActivity(intent);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
//
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Log.d("vehicle", "UNsuccesful sign in");
                            Toast.makeText(getApplicationContext(), "Unsuccesful Sign in", Toast.LENGTH_SHORT)
                                    .show();
                            UpdateUI(null);


                        }
                    }
                });

    }
}