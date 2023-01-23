package com.tukla.www.tukla;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.IOException;
        import java.time.LocalDateTime;

public class SignUpDriver extends AppCompatActivity {

    private EditText eFullName;
    private EditText eAddress;
    private EditText ePhoneNumber;
    private ImageView imgViewId;
    private EditText email_id;
    private EditText mpassword;
    private EditText mconfirmpassword;
    private EditText eToda;
    private EditText ePlateNumber;
    private Button mButton;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap IDBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_driver);

        mAuth=FirebaseAuth.getInstance();
        eFullName=findViewById(R.id.eFullName);
        eAddress=findViewById(R.id.eAddress);
        ePhoneNumber=findViewById(R.id.ePhoneNumber);
        imgViewId=findViewById(R.id.image_view);
        email_id=findViewById(R.id.eEmail);
        mpassword=findViewById(R.id.mpassword);
        mconfirmpassword=findViewById(R.id.mConfirmPassword);
        eToda=findViewById(R.id.eToda);
        ePlateNumber=findViewById(R.id.ePlateNumber);

        mButton=findViewById(R.id.button2);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailPassword(email_id.getText().toString(),mpassword.getText().toString());
            }
        });

        imgViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
            }
        });

    }

    private void checkEmailPassword(String email, String password) {
        email_id.setError(null);
        mpassword.setError(null);

        if(!email.contains("@"))
        {
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
            else if(eAddress.getText().toString().equals(""))
            {
                eAddress.requestFocus();
                eAddress.setError("Address cannot be empty");
            }
            else if(ePhoneNumber.getText().toString().equals(""))
            {
                ePhoneNumber.requestFocus();
                ePhoneNumber.setError("Phone Number cannot be empty");
            }
            else if(eToda.getText().toString().equals(""))
            {
                eToda.requestFocus();
                eToda.setError("Toda cannot be empty");
            }
            else if(ePlateNumber.getText().toString().equals(""))
            {
                ePlateNumber.requestFocus();
                ePlateNumber.setError("Plate Number cannot be empty");
            }

            else{
                registerUser(email,password);
            }

        }

    }

    private void registerUser(String email,String password) {
        final ProgressDialog dialog = ProgressDialog.show(SignUpDriver.this, "",
                "Loading. Please wait...", true);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            showErrorDailog("Succesfully Registered. Please Login");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateDatabase();
                            //UpdateUI(firebaseUser);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Unsuccesful registration", Toast.LENGTH_SHORT)
                                    .show();
                            //UpdateUI(null);
                        }
                    }
                });

    }

    private void updateDatabase() {

        try {
            uploadFirebase(IDBitmap);
        } catch (Exception e) {

        } finally {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRefUsers = database.getReference("users");
            User user = new User(mAuth.getUid(),eFullName.getText().toString(),eAddress.getText().toString(),ePhoneNumber.getText().toString(),true, false, LocalDateTime.now().toString(),false);
            myRefUsers.child(mAuth.getUid()).setValue(user);

            DatabaseReference myRefDrivers = database.getReference("drivers");
            Driver driver = new Driver(user,eToda.getText().toString(),ePlateNumber.getText().toString(), LocalDateTime.now().toString());
            myRefDrivers.child(mAuth.getUid()).setValue(driver);
        }

    }

    private  void showErrorDailog(String message)
    {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent =new Intent(SignUpDriver.this,Login.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void UpdateUI(Object o) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //setting the image to imageview
                imgViewId.setImageBitmap(bitmap);
                // uploading the image to firebase
                //uploadFirebase(bitmap);
                IDBitmap = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFirebase(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String userId =  mAuth.getCurrentUser().getUid();
        // Create a reference to "images/userId.jpg"
        StorageReference imageRef = storageRef.child("images/"+userId+".jpg");

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
}