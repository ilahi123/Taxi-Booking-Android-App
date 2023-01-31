package com.tukla.www.tukla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.io.Serializable;

public class Login extends AppCompatActivity implements Serializable {

    private FirebaseAuth mAuth;
    private Button Signup;
    private Button Login;
    private EditText email;
    private EditText password;
    private Button SignupDriver;
    private CheckBox showHideCheckBox1;
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
        showHideCheckBox1 = findViewById(R.id.show_hide_checkbox_1);

        showHideCheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // hide password
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // move cursor to end of text
                password.setSelection(password.length());
            }
        });

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

            if(Password.length()<6)
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
        final ProgressDialog dialog = ProgressDialog.show(Login.this, "",
                "Loggin in. Please wait...", true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            //UpdateUI(firebaseUser);
                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            DatabaseReference userRef = database.getReference("users").child(mAuth.getUid());
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Intent intent;
                                    User user = dataSnapshot.getValue(User.class);
                                    if(user.getIsAdmin()) {
                                        intent = new Intent(Login.this, AdminActivity.class);
                                        finish();
                                        startActivity(intent);
                                    } else if(user.getIsRejected()) {
                                        intent = new Intent(Login.this, ProfileActivity.class);
                                        finish();
                                        startActivity(intent);
                                    } else if(user.getIsVerified()) {
                                        if(user.getIsDriver()) {
                                            intent = new Intent(Login.this, DriverActivity.class);
                                            //finish();
                                            startActivity(intent);
                                        } else {
                                            intent = new Intent(Login.this, MainActivity.class);
                                            //finish();
                                            startActivity(intent);
                                        }
                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(getApplicationContext(), "You are not verified!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    dialog.dismiss();
                                }
                            });

                        } else {
                            Log.d("vehicle", "Unsuccesful sign in");
                            Toast.makeText(getApplicationContext(), "Unsuccesful Sign in", Toast.LENGTH_SHORT)
                                    .show();
                            UpdateUI(null);
                            dialog.dismiss();
                        }
                    }
                });

    }
//
//    public void set() throws FileNotFoundException {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        String[] names = {
//                "ABADIA, RODOLFO H.",
//                "ABADIA, RAMON S.",
//                "ALVAREZ, JOMER B.",
//                "ABELLADA, RODELIO C.",
//                "BELARMINO, REYNALDO R.",
//                "BASCO, RENATO B.",
//                "CAPALAC, CHRISTOPHEL J.",
//                "DE LUMEN, BERGONIO A.",
//                "DALISAY, ROBERTO F.",
//                "DELOS SANTOS, FELICISIMO M.",
//                "DELA CRUZ, JULIUS C.",
//                "DELA CRUZ, JAYSON M.",
//                "DELA CRUZ, ANDIE M.",
//                "DELA CUEVA, LORENZO SP.",
//                "ESPIDIDO, RODELIO I.",
//                "EVIA, ALFREDO JR. S.",
//                "ESTACIO, DANILO C.",
//                "ESTINOR, JERRY B.",
//                "ERMITA, JAIME F.",
//                "FAJARDO, RUSTICO B.",
//                "FAJARDO, DANIEL JR. L.",
//                "FACUNDO, REDENTOR G.",
//                "GARCES, ARNIEL G.",
//                "GALOS, ANTONIO N.",
//                "JABON, MARK BANJO M.",
//                "LAYCO, LUISITO B.",
//                "MENDOZA, NESTOR A.",
//                "MUNOZ, ANTONIO S.",
//                "MASILANG, LITO M.",
//                "MANJARES, JONATHAN N.",
//                "MANGULAD, MARCIAL P.",
//                "MALABANAN, PEDRO D.",
//                "PALUSTRE, HENRY R.",
//                "PAMPLONA, CORNELIO O.",
//                "PAMPLONA. AVELINO O.",
//                "ROSARIO, EDUARDO B.",
//                "REYES, RAUL O.",
//                "SENEREZ, DIONISIO R.",
//                "SECOPITO, MENELEUS L.",
//                "SOLO, SERGIO M.",
//                "SARICAL, ANGEL JR. Q.",
//                "TAJAN, WILSON C.",
//                "TADEO, VILLAMOR II C.",
//                "YARSO, JEROME E.",
//                "LIRIO, CRISANTO M.",
//                "NAVAJA, RODOLFO JR. S.",
//                "SIMON, EDMOND R.",
//                "DAWINAN, JOHN CARLO M.",
//                "ISLES, WINSTON M.",
//                "MASILANG, LINO M.",
//                "ORIZA, EDWIN P.",
//                "PALUPIT, GILBERT C.",
//                "REBAMBA, JUNJUN S.",
//                "TERRIBLE, MARIO O.",
//                "TANDOC, JOHN PAUL M.",
//                "VELARDO, ANSELMO JR. F."
//        };
//
//        String[] plates = {
//                "375540",
//                "WR5708",
//                "QW2833",
//                "TV7885",
//                "WT3624",
//                "8226VO",
//                "1438WE",
//                "D212883",
//                "WW2719",
//                "DV7581",
//                "9238WX",
//                "VX5470",
//                "DO24711",
//                "K65454",
//                "3520DY",
//                "8831QE",
//                "D031484",
//                "3369WV",
//                "DW8175",
//                "WV9612",
//                "4951D2",
//                "DO19277",
//                "NW5118",
//                "VW5668",
//                "5491WR",
//                "D026375",
//                "D019338",
//                "8747VN",
//                "XX5549",
//                "18640W",
//                "1452QE",
//                "5866DW",
//                "4911D2",
//                "D051O62",
//                "7171WF",
//                "WU9956",
//                "5840WT",
//                "7858WP",
//                "WR5041",
//                "QW1273",
//                "4858D2",
//                "DA87076",
//                "4032DQ",
//                "WY9340",
//                "DP15373",
//                "4995WM",
//                "DO34803",
//                "WB5234",
//                "DO32773",
//                "DO17332",
//                "WY6541",
//                "DO62140",
//                "DU8238",
//                "D210558",
//                "DP33682",
//                "QW1597"
//        };
//
//        String[] phones = {
//                "09505245318",
//                "09093528161",
//                "09207010513",
//                "09102262808",
//                "09264079169",
//                "09068442295",
//                "09322533835",
//                "09161665889",
//                "09354158988",
//                "09173778769",
//                "09151792246",
//                "09198129206",
//                "09504592027",
//                "09264974005",
//                "09185240090",
//                "09755672138",
//                "09395819750",
//                "09489013010",
//                "09386234228",
//                "09777049355",
//                "09097214928",
//                "09395678241",
//                "09194189369",
//                "09106480900",
//                "09064812326",
//                "09059164432",
//                "09177316343",
//                "09174337677",
//                "09090819724",
//                "09102250904",
//                "09550394890",
//                "09204935267",
//                "09214739745",
//                "09192975539",
//                "09202516848",
//                "09296905630",
//                "09567351785",
//                "09430772419",
//                "09083787707",
//                "09066227266",
//                "09097881389",
//                "09095052029",
//                "09163209245",
//                "09611308767",
//                "09481584262",
//                "09357331344",
//                "09430710234",
//                "09953995955",
//                "09457768796",
//                "09182964457",
//                "09663424772",
//                "09216959458",
//                "09194928684",
//                "09486929410",
//                "09108403150",
//                "09300693463"
//        };
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        for(int i=0;i<names.length;i++) {
//
//            String email = "driver"+(i+1)+"@driver.com";
//            int j = i;
//            mAuth.signInWithEmailAndPassword(email,"password")
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//
//                            FirebaseUser key = mAuth.getCurrentUser();
//                            Driver driver = new Driver("SM Toda",plates[j]);
//                            User user = new User(key.getUid(),names[j],"Calamba, Laguna",phones[j],true, false, LocalDateTime.now().toString(),false, driver);
//                            DatabaseReference myRefUsers = database.getReference("users");
//                            myRefUsers.child(key.getUid()).setValue(user);
//
//                            try {
//                                upload(mAuth.getUid());
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    });
//        }
//    }
//
//    public void upload(String key) throws FileNotFoundException {
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        String url = "https://news.tulane.edu/sites/default/files/headshot_icon.jpg";
//
//        StorageReference fileRef = storageRef.child("images/" +key + ".jpg");
//
//        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] data = baos.toByteArray();
//
//                        UploadTask uploadTask = fileRef.putBytes(data);
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                // Handle unsuccessful uploads
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                // Handle successful uploads
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        // Handle loading cleared
//                    }
//                });
//    }
}