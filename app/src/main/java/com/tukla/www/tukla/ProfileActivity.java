package com.tukla.www.tukla;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profPic;
    private EditText name;
    private EditText address;
    private EditText phone;
    private EditText toda;
    private EditText plateNumber;
    private EditText email;
    private EditText password;
    private EditText cpassword;
    private ImageView license;
    private Bitmap profPicBitmap;
    private Bitmap licenseBitmap;
    private Boolean isDriver;
    private FirebaseAuth mAuth;
    private User user;
    private Button saveButton;
    private Button cancelButton;
    private TextView txtReject;
    private Boolean isWithProfPic=false;
    private Boolean isWithLicense=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profPic = findViewById(R.id.image_view);
        name = findViewById(R.id.eFullName);
        address = findViewById(R.id.eAddress);
        phone = findViewById(R.id.ePhoneNumber);
        toda = findViewById(R.id.eToda);
        plateNumber = findViewById(R.id.ePlateNumber);
        email = findViewById(R.id.eEmail);
        password = findViewById(R.id.mpassword);
        cpassword = findViewById(R.id.mConfirmPassword);
        license = findViewById(R.id.img_license);
        profPic.setDrawingCacheEnabled(true);
        saveButton=findViewById(R.id.button2);
        cancelButton=findViewById(R.id.button3);
        txtReject=findViewById(R.id.txt_if_rejected);

        CheckBox showHideCheckBox1 = findViewById(R.id.show_hide_checkbox_1);
        CheckBox showHideCheckBox2 = findViewById(R.id.show_hide_checkbox_2);

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

        showHideCheckBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    cpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // hide password
                    cpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // move cursor to end of text
                cpassword.setSelection(cpassword.length());
            }
        });

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                name.setText(user.getFullname());
                address.setText(user.getAddress());
                phone.setText(user.getPhone());
                email.setText(mAuth.getCurrentUser().getEmail());
                isDriver = user.getIsDriver();

                if(user.getIsRejected())
                    txtReject.setVisibility(View.VISIBLE);
                else
                    txtReject.setVisibility(View.GONE);

                StorageReference profPicRef = storageRef.child("images/"+mAuth.getUid()+".jpg");
                profPicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(profPic.getContext())
                                .load(uri)
                                .fitCenter()
                                .into(profPic);
                        profPicBitmap = profPic.getDrawingCache();
                        isWithProfPic=true;
                    }
                });

                if(user.getIsDriver()) {
                    toda.setVisibility(View.VISIBLE);
                    plateNumber.setVisibility(View.VISIBLE);
                    toda.setText(user.getDriver().getToda());
                    plateNumber.setText(user.getDriver().getPlateNumber());
                    license.setVisibility(View.VISIBLE);
                    license.setDrawingCacheEnabled(true);
                    StorageReference licRef = storageRef.child("licenses/"+mAuth.getUid()+".jpg");
                    licRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(license.getContext())
                                    .load(uri)
                                    .fitCenter()
                                    .into(license);
                            licenseBitmap = license.getDrawingCache();
                            isWithLicense=true;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /** listeners **/
        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, 1);
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, 2);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect();
            }
        });
    }

    private void redirect() {
        if(user.getIsRejected()) {
            mAuth.signOut();
            Intent intent =new Intent(ProfileActivity.this,Login.class);
            finish();
            startActivity(intent);
        } else if(isDriver) {
            Intent intent =new Intent(ProfileActivity.this,DriverActivity.class);
            finish();
            startActivity(intent);
        } else {
            Intent intent =new Intent(ProfileActivity.this,MainActivity.class);
            finish();
            startActivity(intent);
        }
    }
    private  void showPopupDialog(String message)
    {
        AlertDialog dialog =  new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkInputs() {

        password.setError(null);

        if(isDriver) {
            if(toda.getText().toString().equals(""))
            {
                toda.requestFocus();
                toda.setError("Toda cannot be empty");
            } else if(plateNumber.getText().toString().equals(""))
            {
                plateNumber.requestFocus();
                plateNumber.setError("Plate Number cannot be empty");
            } else if(!isWithLicense) {
                showPopupDialog("License ID is required!");
                license.requestFocus();
            }
        }

        if(!isWithProfPic) {
            showPopupDialog("Image is required!");
            profPic.requestFocus();
        } else if(password.getText().toString().length()<6) {
                password.requestFocus();
                password.setError("Password must be atleast 6 characters long");
        } else if(!cpassword.getText().toString().equals(password.getText().toString())) {
                cpassword.requestFocus();
                cpassword.setError("Password does not match");
        } else if(address.getText().toString().equals("")) {
                address.requestFocus();
                address.setError("Address cannot be empty");
        } else if(phone.getText().toString().equals("")) {
                phone.requestFocus();
                phone.setError("Phone Number cannot be empty");
        } else{

            mAuth.getCurrentUser().updatePassword(password.getText().toString());
            Driver driverObj = null;

            uploadFirebase(profPicBitmap, "images");

            if(isDriver) {
                uploadFirebase(licenseBitmap, "licenses");
                driverObj = new Driver(toda.getText().toString(),plateNumber.getText().toString());
            }

            User newUser = new User(mAuth.getUid(),name.getText().toString(),address.getText().toString(),phone.getText().toString(),isDriver,user.getIsVerified(), LocalDateTime.now().toString(),user.getIsAdmin(), driverObj, user.getIsRejected());
            FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).setValue(newUser);

            showPopupDialog("Profile has been updated!");

            redirect();
        }
    }

    private void uploadFirebase(Bitmap bitmap,String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String userId =  mAuth.getCurrentUser().getUid();
        // Create a reference to "images/userId.jpg"
        StorageReference imageRef = storageRef.child(path+"/"+userId+".jpg");

        // Convert the bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(data);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Exception Error",exception.getMessage());
                Toast.makeText(getBaseContext(), "Error in Registration! Try again!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult();
//                String imageUrl = downloadUrl.toString();
//                // you can now set the imageUrl to the user object
//                user.setImageUrl(imageUrl);
//                // now you can save the user object with the imageUrl to firebase
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("Users").child("UserId");
//                String key = myRef.push().getKey();
//                myRef.child(key).setValue(user);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //setting the image to imageview
                profPic.setImageBitmap(bitmap);
                // uploading the image to firebase
                //uploadFirebase(bitmap);
                profPicBitmap = bitmap;

                isWithProfPic=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //setting the image to imageview
                license.setImageBitmap(bitmap);
                // uploading the image to firebase
                //uploadFirebase(bitmap);
                licenseBitmap = bitmap;
                isWithLicense=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}