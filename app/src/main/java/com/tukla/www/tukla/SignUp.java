package com.tukla.www.tukla;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText musername;
    private EditText email_id;
    private EditText mpassword;
    private EditText mconfirmpassword;
    private Button mButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        musername=findViewById(R.id.eUsername);
        email_id=findViewById(R.id.eEmail);
        mpassword=findViewById(R.id.mpassword);
        mconfirmpassword=findViewById(R.id.mConfirmPassword);
        mAuth=FirebaseAuth.getInstance();
        mButton=findViewById(R.id.button2);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailPassword(email_id.getText().toString(),mpassword.getText().toString());
            }
        });



    }

    private void checkEmailPassword(String email, String password) {
        Log.d("Vehicle","email:"+email);
        Log.d("Vehicle","password:"+password);
        email_id.setError(null);
        mpassword.setError(null);

        if(!email.contains("@"))
        {
            Log.d("Vehicle","in if email:"+email);
            Log.d("Vehicle","in if password:"+password);
            email_id.requestFocus();
            email_id.setError("INVALID EMAIL");
        }
        else{

            if(password.length()<=6)
            {
                mpassword.requestFocus();
                mpassword.setError("Password must be atleast 6 characters long");
            }

            else if(!mconfirmpassword.getText().toString().equals(password))
            {
                mconfirmpassword.requestFocus();
                mconfirmpassword.setError("Password does not match");


            }
            else if(musername.getText().toString().equals(""))
            {
                musername.requestFocus();
                musername.setError("Username cannot be empty");
            }

            else{
                registerUser(email,password);
            }

        }

    }

    private void registerUser(String email,String password) {
        final ProgressDialog dialog = ProgressDialog.show(SignUp.this, "",
                "Loading. Please wait...", true);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Log.d("vehicle", "succesful authentication");
                            showErrorDailog("Succesfully Registered. Please Login");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateDatabase();
                            UpdateUI(firebaseUser);
                        } else {
                            dialog.dismiss();
                            Log.d("vehicle", "UNsuccesful registration");
                            Toast.makeText(getApplicationContext(), "Unsuccesful registration", Toast.LENGTH_SHORT)
                                    .show();
                            UpdateUI(null);
                        }
                    }
                });

    }

    private void updateDatabase() {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef=myRef.child("Users").child("UserId");
        myRef.push().setValue(musername.getText().toString());
    }

    private  void showErrorDailog(String message)
    {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent =new Intent(SignUp.this,MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void UpdateUI(Object o) {
    }
}
